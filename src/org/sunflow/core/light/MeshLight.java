package org.sunflow.core.light;

import org.sunflow.SunflowAPI;
import org.sunflow.core.LightSample;
import org.sunflow.core.LightSource;
import org.sunflow.core.ParameterList;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.core.primitive.TriangleMesh;
import org.sunflow.image.Color;
import org.sunflow.math.MathUtils;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;

public class MeshLight extends TriangleMesh implements Shader {
    private Color radiance;
    private int numSamples;

    public MeshLight() {
        radiance = Color.WHITE;
        numSamples = 4;
    }

    public boolean update(ParameterList pl, SunflowAPI api) {
        radiance = pl.getColor("radiance", radiance);
        numSamples = pl.getInt("samples", numSamples);
        return super.update(pl, api);
    }

    public void init(String name, SunflowAPI api) {
        api.geometry(name, this);
        api.shader(name + ".shader", this);
        api.parameter("shaders", name + ".shader");
        api.instance(name + ".instance", name);
        for (int i = 0, j = 0; i < triangles.length; i += 3, j++) {
            TriangleLight t = new TriangleLight(j);
            String lname = String.format("%s.light[%d]", name, j);
            api.light(lname, t);
        }
    }

    private class TriangleLight implements LightSource {
        private int tri3;
        private float area;
        private Vector3 ng;

        TriangleLight(int tri) {
            tri3 = 3 * tri;
            int a = triangles[tri3 + 0];
            int b = triangles[tri3 + 1];
            int c = triangles[tri3 + 2];
            Point3 v0p = getPoint(a);
            Point3 v1p = getPoint(b);
            Point3 v2p = getPoint(c);
            ng = Point3.normal(v0p, v1p, v2p);
            area = 0.5f * ng.length();
            ng.normalize();
        }

        public boolean update(ParameterList pl, SunflowAPI api) {
            return true;
        }

        public int getNumSamples() {
            return numSamples;
        }

        public int getLowSamples() {
            return 1;
        }

        public boolean isVisible(ShadingState state) {
            Point3 p = state.getPoint();
            Vector3 n = state.getNormal();
            Vector3 sub = new Vector3();
            int a = triangles[tri3 + 0];
            int b = triangles[tri3 + 1];
            int c = triangles[tri3 + 2];
            Point3 v0p = getPoint(a);
            Point3.sub(v0p, p, sub);
            if ((Vector3.dot(sub, n) > 0.0) || (Vector3.dot(sub, ng) < 0.0))
                return true;
            Point3 v1p = getPoint(b);
            Point3.sub(v1p, p, sub);
            if ((Vector3.dot(sub, n) > 0.0) || (Vector3.dot(sub, ng) < 0.0))
                return true;
            Point3 v2p = getPoint(c);
            Point3.sub(v2p, p, sub);
            if ((Vector3.dot(sub, n) > 0.0) || (Vector3.dot(sub, ng) < 0.0))
                return true;
            return false;
        }

        private final boolean intersectTriangleKensler(Ray r) {
            int a = 3 * triangles[tri3 + 0];
            int b = 3 * triangles[tri3 + 1];
            int c = 3 * triangles[tri3 + 2];
            float edge0x = points[b + 0] - points[a + 0];
            float edge0y = points[b + 1] - points[a + 1];
            float edge0z = points[b + 2] - points[a + 2];
            float edge1x = points[a + 0] - points[c + 0];
            float edge1y = points[a + 1] - points[c + 1];
            float edge1z = points[a + 2] - points[c + 2];
            float nx = edge0y * edge1z - edge0z * edge1y;
            float ny = edge0z * edge1x - edge0x * edge1z;
            float nz = edge0x * edge1y - edge0y * edge1x;
            float v = r.dot(nx, ny, nz);
            float iv = 1 / v;
            float edge2x = points[a + 0] - r.ox;
            float edge2y = points[a + 1] - r.oy;
            float edge2z = points[a + 2] - r.oz;
            float va = nx * edge2x + ny * edge2y + nz * edge2z;
            float t = iv * va;
            if (t <= 0)
                return false;
            float ix = edge2y * r.dz - edge2z * r.dy;
            float iy = edge2z * r.dx - edge2x * r.dz;
            float iz = edge2x * r.dy - edge2y * r.dx;
            float v1 = ix * edge1x + iy * edge1y + iz * edge1z;
            float beta = iv * v1;
            if (beta < 0)
                return false;
            float v2 = ix * edge0x + iy * edge0y + iz * edge0z;
            if ((v1 + v2) * v > v * v)
                return false;
            float gamma = iv * v2;
            if (gamma < 0)
                return false;
            r.setMax(t);
            return true;
        }

        public void getSample(int i, int n, ShadingState state, LightSample dest) {
            int index0 = triangles[tri3 + 0];
            int index1 = triangles[tri3 + 1];
            int index2 = triangles[tri3 + 2];
            Vector3 p0 = Point3.sub(getPoint(index0), state.getPoint(), new Vector3()).normalize();
            Vector3 p1 = Point3.sub(getPoint(index1), state.getPoint(), new Vector3()).normalize();
            Vector3 p2 = Point3.sub(getPoint(index2), state.getPoint(), new Vector3()).normalize();

            float cc = MathUtils.clamp(Vector3.dot(p0, p1), -1.0f, 1.0f);

            Vector3 n0 = Vector3.cross(p0, p1, new Vector3());
            Vector3 n1 = Vector3.cross(p1, p2, new Vector3());
            Vector3 n2 = Vector3.cross(p2, p0, new Vector3());
            float len0 = n0.length(), len1 = n1.length(), len2 = n2.length();

            if (len0 > 1e-6f)
                n0.div(len0);
            else
                return;
            if (len1 > 1e-6f)
                n1.div(len1);
            else
                return;
            if (len2 > 1e-6f)
                n2.div(len2);
            else
                return;

            float calpha = MathUtils.clamp(-Vector3.dot(n2, n0), -1.0f, 1.0f);
            float cbeta = MathUtils.clamp(-Vector3.dot(n0, n1), -1.0f, 1.0f);
            float cgamma = MathUtils.clamp(-Vector3.dot(n1, n2), -1.0f, 1.0f);

            float alpha = (float) Math.acos(calpha);
            float beta = (float) Math.acos(cbeta);
            float gamma = (float) Math.acos(cgamma);

            float area = alpha + beta + gamma - (float) Math.PI;

            float dot = Vector3.dot(p2, p0);
            Vector3 h = new Vector3();
            h.x = p2.x - dot * p0.x;
            h.y = p2.y - dot * p0.y;
            h.z = p2.z - dot * p0.z;
            float hlen = h.length();
            if (hlen < 1e-6f)
                return;
            h.div(hlen);

            float salpha = (float) Math.sin(alpha);
            float product = salpha * cc;

            // now sample

            // random offset on unit square
            double randX = state.getRandom(i, 0);
            double randY = state.getRandom(i, 1);

            float phi = (float) randX * area - alpha + (float) Math.PI;
            float sphi = (float) Math.sin(phi);
            float cphi = (float) Math.cos(phi);

            float u = cphi + calpha;
            float v = sphi - product;

            float cbt = -v;
            float sbt = u;

            float q = (cbt + calpha * (cphi * cbt + sphi * sbt)) / (salpha * (sphi * cbt - cphi * sbt));
            float q1 = 1.0f - q * q;
            if (q1 < 0.0f)
                q1 = 0.0f;

            Vector3 nc = new Vector3();
            float sqrtq1 = (float) Math.sqrt(q1);
            nc.x = q * p0.x + sqrtq1 * h.x;
            nc.y = q * p0.y + sqrtq1 * h.y;
            nc.z = q * p0.z + sqrtq1 * h.z;

            float z = 1.0f - (float) randY * (1.0f - Vector3.dot(nc, p1));
            float z1 = 1.0f - z * z;
            if (z1 < 0.0f)
                z1 = 0.0f;

            dot = Vector3.dot(nc, p1);
            Vector3 nd = new Vector3();
            nd.x = nc.x - dot * p1.x;
            nd.y = nc.y - dot * p1.y;
            nd.z = nc.z - dot * p1.z;
            nd.normalize();
            float sqrtz1 = (float) Math.sqrt(z1);
            Vector3 result = new Vector3();
            result.x = z * p1.x + sqrtz1 * nd.x;
            result.y = z * p1.y + sqrtz1 * nd.y;
            result.z = z * p1.z + sqrtz1 * nd.z;

            // compute intersection with triangle (if any)
            Ray shadowRay = new Ray(state.getPoint(), result);
            if (!intersectTriangleKensler(shadowRay))
                return;

            dest.setShadowRay(shadowRay);

            // check that the direction of the sample is the same as the
            // normal
            float cosNx = dest.dot(state.getNormal());
            if (cosNx <= 0)
                return;

            // light source facing point ?
            // (need to check with light source's normal)
            float cosNy = -dest.dot(ng);
            if (cosNy > 0) {
                // prepare sample
                dest.setRadiance(radiance, radiance);
                dest.getDiffuseRadiance().mul(area);
                dest.getSpecularRadiance().mul(area);
                dest.traceShadow(state);
            }
        }

        public void getPhoton(double randX1, double randY1, double randX2, double randY2, Point3 p, Vector3 dir, Color power) {
            double s = Math.sqrt(1 - randX2);
            float u = (float) (randY2 * s);
            float v = (float) (1 - s);
            float w = 1 - u - v;
            int index0 = 3 * triangles[tri3 + 0];
            int index1 = 3 * triangles[tri3 + 1];
            int index2 = 3 * triangles[tri3 + 2];
            p.x = w * points[index0 + 0] + u * points[index1 + 0] + v * points[index2 + 0];
            p.y = w * points[index0 + 1] + u * points[index1 + 1] + v * points[index2 + 1];
            p.z = w * points[index0 + 2] + u * points[index1 + 2] + v * points[index2 + 2];
            p.x += 0.001f * ng.x;
            p.y += 0.001f * ng.y;
            p.z += 0.001f * ng.z;
            OrthoNormalBasis onb = OrthoNormalBasis.makeFromW(ng);
            u = (float) (2 * Math.PI * randX1);
            s = Math.sqrt(randY1);
            onb.transform(new Vector3((float) (Math.cos(u) * s), (float) (Math.sin(u) * s), (float) (Math.sqrt(1 - randY1))), dir);
            Color.mul((float) Math.PI * area, radiance, power);
        }

        public float getPower() {
            return radiance.copy().mul((float) Math.PI * area).getLuminance();
        }
    }

    public Color getRadiance(ShadingState state) {
        if (!state.includeLights())
            return Color.BLACK;
        state.faceforward();
        // emit constant radiance
        return state.isBehind() ? Color.BLACK : radiance;
    }

    public void scatterPhoton(ShadingState state, Color power) {
        // do not scatter photons
    }
}