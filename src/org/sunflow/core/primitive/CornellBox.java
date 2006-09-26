package org.sunflow.core.primitive;

import org.sunflow.core.IntersectionState;
import org.sunflow.core.LightSample;
import org.sunflow.core.LightSource;
import org.sunflow.core.PrimitiveList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.image.Color;
import org.sunflow.math.BoundingBox;
import org.sunflow.math.Matrix4;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

public class CornellBox implements PrimitiveList, Shader, LightSource {
    private float minX, minY, minZ;
    private float maxX, maxY, maxZ;
    private Color left, right, top, bottom, back;
    private Color radiance;
    private int samples;
    private float lxmin, lymin, lxmax, lymax;
    private float area;
    private BoundingBox lightBounds;

    public CornellBox(Point3 corner0, Point3 corner1, Color left, Color right, Color top, Color bottom, Color back, Color radiance, int samples) {
        // figure out cube extents
        lightBounds = new BoundingBox(corner0);
        lightBounds.include(corner1);

        // cube extents
        minX = lightBounds.getMinimum().x;
        minY = lightBounds.getMinimum().y;
        minZ = lightBounds.getMinimum().z;
        maxX = lightBounds.getMaximum().x;
        maxY = lightBounds.getMaximum().y;
        maxZ = lightBounds.getMaximum().z;

        // work around epsilon problems for light test
        lightBounds.enlargeUlps();

        // cube colors
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.back = back;
        // light source
        this.radiance = radiance;
        this.samples = samples;
        lxmin = maxX / 3 + 2 * minX / 3;
        lxmax = minX / 3 + 2 * maxX / 3;
        lymin = maxY / 3 + 2 * minY / 3;
        lymax = minY / 3 + 2 * maxY / 3;
        area = (lxmax - lxmin) * (lymax - lymin);
    }

    public BoundingBox getBounds() {
        return lightBounds;
    }

    public float getBound(int i) {
        switch (i) {
            case 0:
                return minX;
            case 1:
                return maxX;
            case 2:
                return minY;
            case 3:
                return maxY;
            case 4:
                return minZ;
            case 5:
                return maxZ;
            default:
                return 0;
        }
    }

    public boolean intersects(BoundingBox box) {
        // this could be optimized
        BoundingBox b = new BoundingBox();
        b.include(new Point3(minX, minY, minZ));
        b.include(new Point3(maxX, maxY, maxZ));
        if (b.intersects(box)) {
            // the box is overlapping or enclosed
            if (!b.contains(new Point3(box.getMinimum().x, box.getMinimum().y, box.getMinimum().z)))
                return true;
            if (!b.contains(new Point3(box.getMinimum().x, box.getMinimum().y, box.getMaximum().z)))
                return true;
            if (!b.contains(new Point3(box.getMinimum().x, box.getMaximum().y, box.getMinimum().z)))
                return true;
            if (!b.contains(new Point3(box.getMinimum().x, box.getMaximum().y, box.getMaximum().z)))
                return true;
            if (!b.contains(new Point3(box.getMaximum().x, box.getMinimum().y, box.getMinimum().z)))
                return true;
            if (!b.contains(new Point3(box.getMaximum().x, box.getMinimum().y, box.getMaximum().z)))
                return true;
            if (!b.contains(new Point3(box.getMaximum().x, box.getMaximum().y, box.getMinimum().z)))
                return true;
            if (!b.contains(new Point3(box.getMaximum().x, box.getMaximum().y, box.getMaximum().z)))
                return true;
            // all vertices of the box are inside - the surface of the box is
            // not intersected
        }
        return false;
    }

    public void prepareShadingState(ShadingState state) {
        state.init();
        state.getRay().getPoint(state.getPoint());
        int n = state.getPrimitiveID();
        switch (n) {
            case 0:
                state.getNormal().set(new Vector3(1, 0, 0));
                break;
            case 1:
                state.getNormal().set(new Vector3(-1, 0, 0));
                break;
            case 2:
                state.getNormal().set(new Vector3(0, 1, 0));
                break;
            case 3:
                state.getNormal().set(new Vector3(0, -1, 0));
                break;
            case 4:
                state.getNormal().set(new Vector3(0, 0, 1));
                break;
            case 5:
                state.getNormal().set(new Vector3(0, 0, -1));
                break;
            default:
                state.getNormal().set(new Vector3(0, 0, 0));
                break;
        }
        state.getGeoNormal().set(state.getNormal());
        state.setBasis(OrthoNormalBasis.makeFromW(state.getNormal()));
        state.setShader(this);
    }

    public void intersectPrimitive(Ray r, int primID, IntersectionState state) {
        float intervalMin = Float.NEGATIVE_INFINITY;
        float intervalMax = Float.POSITIVE_INFINITY;
        float orgX = r.ox;
        float invDirX = 1 / r.dx;
        float t1, t2;
        t1 = (minX - orgX) * invDirX;
        t2 = (maxX - orgX) * invDirX;
        int sideIn = -1, sideOut = -1;
        if (invDirX > 0) {
            if (t1 > intervalMin) {
                intervalMin = t1;
                sideIn = 0;
            }
            if (t2 < intervalMax) {
                intervalMax = t2;
                sideOut = 1;
            }
        } else {
            if (t2 > intervalMin) {
                intervalMin = t2;
                sideIn = 1;
            }
            if (t1 < intervalMax) {
                intervalMax = t1;
                sideOut = 0;
            }
        }
        if (intervalMin > intervalMax)
            return;
        float orgY = r.oy;
        float invDirY = 1 / r.dy;
        t1 = (minY - orgY) * invDirY;
        t2 = (maxY - orgY) * invDirY;
        if (invDirY > 0) {
            if (t1 > intervalMin) {
                intervalMin = t1;
                sideIn = 2;
            }
            if (t2 < intervalMax) {
                intervalMax = t2;
                sideOut = 3;
            }
        } else {
            if (t2 > intervalMin) {
                intervalMin = t2;
                sideIn = 3;
            }
            if (t1 < intervalMax) {
                intervalMax = t1;
                sideOut = 2;
            }
        }
        if (intervalMin > intervalMax)
            return;
        float orgZ = r.oz;
        float invDirZ = 1 / r.dz;
        t1 = (minZ - orgZ) * invDirZ; // no front wall
        t2 = (maxZ - orgZ) * invDirZ;
        if (invDirZ > 0) {
            if (t1 > intervalMin) {
                intervalMin = t1;
                sideIn = 4;
            }
            if (t2 < intervalMax) {
                intervalMax = t2;
                sideOut = 5;
            }
        } else {
            if (t2 > intervalMin) {
                intervalMin = t2;
                sideIn = 5;
            }
            if (t1 < intervalMax) {
                intervalMax = t1;
                sideOut = 4;
            }
        }
        if (intervalMin > intervalMax)
            return;
        assert sideIn != -1;
        assert sideOut != -1;
        // can't hit minY wall, there is none
        if (sideIn != 2 && r.isInside(intervalMin)) {
            r.setMax(intervalMin);
            state.setIntersection(sideIn, 0, 0);
        } else if (sideOut != 2 && r.isInside(intervalMax)) {
            r.setMax(intervalMax);
            state.setIntersection(sideOut, 0, 0);
        }
    }

    public Color getRadiance(ShadingState state) {
        int side = state.getPrimitiveID();
        Color kd = null;
        switch (side) {
            case 0:
                kd = left;
                break;
            case 1:
                kd = right;
                break;
            case 3:
                kd = back;
                break;
            case 4:
                kd = bottom;
                break;
            case 5:
                float lx = state.getPoint().x;
                float ly = state.getPoint().y;
                if (lx >= lxmin && lx < lxmax && ly >= lymin && ly < lymax && state.getRay().dz > 0)
                    return state.includeLights() ? radiance : Color.BLACK;
                kd = top;
                break;
            default:
                assert false;
        }
        // make sure we are on the right side of the material
        state.faceforward();
        // setup lighting
        state.initLightSamples();
        state.initCausticSamples();
        return state.diffuse(kd);
    }

    public void scatterPhoton(ShadingState state, Color power) {
        int side = state.getPrimitiveID();
        Color kd = null;
        switch (side) {
            case 0:
                kd = left;
                break;
            case 1:
                kd = right;
                break;
            case 3:
                kd = back;
                break;
            case 4:
                kd = bottom;
                break;
            case 5:
                float lx = state.getPoint().x;
                float ly = state.getPoint().y;
                if (lx >= lxmin && lx < lxmax && ly >= lymin && ly < lymax && state.getRay().dz > 0)
                    return;
                kd = top;
                break;
            default:
                assert false;
        }
        // make sure we are on the right side of the material
        if (Vector3.dot(state.getNormal(), state.getRay().getDirection()) > 0) {
            state.getNormal().negate();
            state.getGeoNormal().negate();
        }
        state.storePhoton(state.getRay().getDirection(), power, kd);
        double avg = kd.getAverage();
        double rnd = state.getRandom(0, 0, 1);
        if (rnd < avg) {
            // photon is scattered
            power.mul(kd).mul(1 / (float) avg);
            OrthoNormalBasis onb = OrthoNormalBasis.makeFromW(state.getNormal());
            double u = 2 * Math.PI * rnd / avg;
            double v = state.getRandom(0, 1, 1);
            float s = (float) Math.sqrt(v);
            float s1 = (float) Math.sqrt(1.0 - v);
            Vector3 w = new Vector3((float) Math.cos(u) * s, (float) Math.sin(u) * s, s1);
            w = onb.transform(w, new Vector3());
            state.traceDiffusePhoton(new Ray(state.getPoint(), w), power);
        }
    }

    public int getNumSamples() {
        return samples;
    }

    public boolean isVisible(ShadingState state) {
        Point3 p = state.getPoint();
        return lightBounds.contains(p) && p.z < maxZ;
    }

    public void getSample(int i, ShadingState state, LightSample dest) {
        // random offset on unit square, we use the infinite version of
        // getRandom
        // because the light sampling is adaptive
        double randX = state.getRandom(i, 0);
        double randY = state.getRandom(i, 1);

        Point3 p = new Point3();
        p.x = (float) (lxmin * (1 - randX) + lxmax * randX);
        p.y = (float) (lymin * (1 - randY) + lymax * randY);
        p.z = maxZ;

        // prepare shadow ray to sampled point
        dest.setShadowRay(new Ray(state.getPoint(), p));

        // check that the direction of the sample is the same as the normal
        float cosNx = dest.dot(state.getNormal());
        if (cosNx <= 0)
            return;

        // light source facing point ?
        // (need to check with light source's normal)
        float cosNy = dest.getShadowRay().dz;
        if (cosNy > 0) {
            // compute geometric attenuation and probability scale factor
            float r = dest.getShadowRay().getMax();
            float g = cosNy / (r * r);
            float scale = g * area;
            // set final sample radiance
            dest.setRadiance(radiance, radiance);
            dest.getDiffuseRadiance().mul(scale);
            dest.getSpecularRadiance().mul(scale);
            dest.traceShadow(state);
        }
    }

    public void getPhoton(double randX1, double randY1, double randX2, double randY2, Point3 p, Vector3 dir, Color power) {
        p.x = (float) (lxmin * (1 - randX2) + lxmax * randX2);
        p.y = (float) (lymin * (1 - randY2) + lymax * randY2);
        p.z = maxZ;

        double u = 2 * Math.PI * randX1;
        double s = Math.sqrt(randY1);
        dir.set((float) (Math.cos(u) * s), (float) (Math.sin(u) * s), (float) -Math.sqrt(1.0f - randY1));
        Color.mul((float) Math.PI * area, radiance, power);
    }

    public boolean isAdaptive() {
        return true;
    }

    public float getPower() {
        return radiance.copy().mul((float) Math.PI * area).getLuminance();
    }

    public int getNumPrimitives() {
        return 1;
    }

    public float getPrimitiveBound(int primID, int i) {
        switch (i) {
            case 0:
                return minX;
            case 1:
                return maxX;
            case 2:
                return minY;
            case 3:
                return maxY;
            case 4:
                return minZ;
            case 5:
                return maxZ;
            default:
                return 0;
        }
    }

    public BoundingBox getWorldBounds(Matrix4 o2w) {
        BoundingBox bounds = new BoundingBox(minX, minY, minZ);
        bounds.include(maxX, maxY, maxZ);
        if (o2w == null)
            return bounds;
        return o2w.transform(bounds);
    }
}