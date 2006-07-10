package org.sunflow.core.primitive;

import org.sunflow.SunflowAPI;
import org.sunflow.core.BoundedPrimitive;
import org.sunflow.core.IntersectionState;
import org.sunflow.core.Ray;
import org.sunflow.core.Shader;
import org.sunflow.core.ShadingState;
import org.sunflow.math.BoundingBox;
import org.sunflow.math.MathUtils;
import org.sunflow.math.OrthoNormalBasis;
import org.sunflow.math.Point3;
import org.sunflow.math.Vector3;
import org.sunflow.system.UI;

public class Mesh {
    private static boolean smallTriangles = false;
    protected float[] points;
    protected int[] triangles;
    private Shader[] shaders;
    private float[] normals;
    private float[] uvs;
    private byte[] faceShaders;
    private InterpType normalInterp;
    private InterpType uvInterp;

    public static void setSmallTriangles(boolean smallTriangles) {
        if (smallTriangles)
            UI.printInfo("[TRI] Activating small mesh mode");
        else
            UI.printInfo("[TRI] Disabling small mesh mode");
        Mesh.smallTriangles = smallTriangles;
    }

    public enum InterpType {
        NONE, VERTEX, FACEVARYING,
    }

    public Mesh() {
        points = null;
        triangles = null;
        shaders = null;
        normals = uvs = null;
        faceShaders = null;
        normalInterp = uvInterp = InterpType.NONE;
    }

    public void init(SunflowAPI api) {
        if (points == null)
            UI.printWarning("[TRI] Incomplete mesh, cannot initialize");
        else {
            int nTriangles = triangles == null ? points.length / 9 : triangles.length / 3;
            if (smallTriangles) {
                for (int i = 0; i < nTriangles; i++)
                    api.primitive(new SmallTriangle(this, i));
            } else {
                for (int i = 0; i < nTriangles; i++)
                    api.primitive(new Triangle(i));
            }
        }
    }

    public void shader(Shader shader) {
        this.shaders = new Shader[] { shader };
    }

    public void shader(Shader[] shaders) {
        this.shaders = shaders;
    }

    public void points(float[] points) {
        this.points = points;
    }

    public void triangles(int[] triangles) {
        this.triangles = triangles;
    }

    public void normals(InterpType t, float[] normals) {
        switch (t) {
            case NONE:
                normalInterp = t;
                this.normals = null;
                break;
            case VERTEX:
                if (points == null)
                    UI.printWarning("[TRI] Unable to set vertex normals before points");
                else if (normals != null && normals.length == points.length) {
                    normalInterp = t;
                    this.normals = normals;
                } else
                    UI.printWarning("[TRI] Invalid number of vertex normals.");
                break;
            case FACEVARYING:
                if (triangles == null)
                    UI.printWarning("[TRI] Unable to set facevarying normals before triangles");
                else if (normals != null && normals.length == triangles.length * 3) {
                    normalInterp = t;
                    this.normals = normals;
                } else
                    UI.printWarning("[TRI] Invalid number of facevarying normals.");
                break;
        }
    }

    public void uvs(InterpType t, float[] uvs) {
        switch (t) {
            case NONE:
                uvInterp = t;
                this.uvs = null;
                break;
            case VERTEX:
                if (points == null)
                    UI.printWarning("[TRI] Unable to set vertex uvs before points");
                else if (uvs != null && uvs.length * 3 == points.length * 2) {
                    uvInterp = t;
                    this.uvs = uvs;
                } else
                    UI.printWarning("[TRI] Invalid number of vertex uvs.");
                break;
            case FACEVARYING:
                if (triangles == null)
                    UI.printWarning("[TRI] Unable to set facevarying uvs before triangles");
                else if (uvs != null && (uvs.length == triangles.length * 2)) {
                    uvInterp = t;
                    this.uvs = uvs;
                } else
                    UI.printWarning("[TRI] Invalid number of facevarying normals.");
                break;
        }
    }

    public void faceShaders(byte[] faceShaders) {
        if (triangles == null)
            UI.printWarning("[TRI] Unable to set face shaders before triangles.");
        else if (faceShaders != null && 3 * faceShaders.length == triangles.length)
            this.faceShaders = faceShaders;
        else
            UI.printWarning("[TRI] Invalid number of face shaders.");
    }

    protected Point3 getPoint(int i) {
        i *= 3;
        return new Point3(points[i], points[i + 1], points[i + 2]);
    }

    private BoundingBox getBounds(int tri3) {
        int a, b, c;
        if (triangles != null) {
            a = 3 * triangles[tri3 + 0];
            b = 3 * triangles[tri3 + 1];
            c = 3 * triangles[tri3 + 2];
        } else {
            a = 3 * (tri3 + 0);
            b = 3 * (tri3 + 1);
            c = 3 * (tri3 + 2);

        }
        BoundingBox box = new BoundingBox(points[a + 0], points[a + 1], points[a + 2]);
        box.include(points[b + 0], points[b + 1], points[b + 2]);
        box.include(points[c + 0], points[c + 1], points[c + 2]);
        return box;
    }

    private void prepareShadingState(ShadingState state, int t) {
        state.init();
        float u = state.getU();
        float v = state.getV();
        float w = 1 - u - v;
        state.getRay().getPoint(state.getPoint());
        int tri = 3 * t;
        int index0 = tri + 0, index1 = tri + 1, index2 = tri + 2;
        if (triangles != null) {
            index0 = triangles[index0];
            index1 = triangles[index1];
            index2 = triangles[index2];
        }
        Point3 v0p = getPoint(index0);
        Point3 v1p = getPoint(index1);
        Point3 v2p = getPoint(index2);
        Vector3 ng = Vector3.cross(Point3.sub(v1p, v0p, new Vector3()), Point3.sub(v2p, v0p, new Vector3()), new Vector3());
        ng.normalize();
        state.getGeoNormal().set(ng);
        switch (normalInterp) {
            case NONE: {
                state.getNormal().set(ng);
                break;
            }
            case VERTEX: {
                int i30 = 3 * index0;
                int i31 = 3 * index1;
                int i32 = 3 * index2;
                state.getNormal().x = w * normals[i30 + 0] + u * normals[i31 + 0] + v * normals[i32 + 0];
                state.getNormal().y = w * normals[i30 + 1] + u * normals[i31 + 1] + v * normals[i32 + 1];
                state.getNormal().z = w * normals[i30 + 2] + u * normals[i31 + 2] + v * normals[i32 + 2];
                state.getNormal().normalize();
                break;
            }
            case FACEVARYING: {
                int idx = 3 * tri;
                state.getNormal().x = w * normals[idx + 0] + u * normals[idx + 3] + v * normals[idx + 6];
                state.getNormal().y = w * normals[idx + 1] + u * normals[idx + 4] + v * normals[idx + 7];
                state.getNormal().z = w * normals[idx + 2] + u * normals[idx + 5] + v * normals[idx + 8];
                state.getNormal().normalize();
                break;
            }
        }
        float uv00 = 0, uv01 = 0, uv10 = 0, uv11 = 0, uv20 = 0, uv21 = 0;
        switch (uvInterp) {
            case NONE: {
                state.getUV().x = 0;
                state.getUV().y = 0;
                break;
            }
            case VERTEX: {
                int i20 = 2 * index0;
                int i21 = 2 * index1;
                int i22 = 2 * index2;
                uv00 = uvs[i20 + 0];
                uv01 = uvs[i20 + 1];
                uv10 = uvs[i21 + 0];
                uv11 = uvs[i21 + 1];
                uv20 = uvs[i22 + 0];
                uv21 = uvs[i22 + 1];
                break;
            }
            case FACEVARYING: {
                int idx = tri << 1;
                uv00 = uvs[idx + 0];
                uv01 = uvs[idx + 1];
                uv10 = uvs[idx + 2];
                uv11 = uvs[idx + 3];
                uv20 = uvs[idx + 4];
                uv21 = uvs[idx + 5];
                break;
            }
        }
        if (uvInterp != InterpType.NONE) {
            // get exact uv coords and compute tangent vectors
            state.getUV().x = w * uv00 + u * uv10 + v * uv20;
            state.getUV().y = w * uv01 + u * uv11 + v * uv21;
            float du1 = uv00 - uv20;
            float du2 = uv10 - uv20;
            float dv1 = uv01 - uv21;
            float dv2 = uv11 - uv21;
            Vector3 dp1 = Point3.sub(v0p, v2p, new Vector3()), dp2 = Point3.sub(v1p, v2p, new Vector3());
            float determinant = du1 * dv2 - dv1 * du2;
            if (determinant == 0.0f) {
                state.setBasis(OrthoNormalBasis.makeFromW(state.getNormal()));
            } else {
                float invdet = 1.f / determinant;
                Vector3 dpdu = new Vector3();
                dpdu.x = (dv2 * dp1.x - dv1 * dp2.x) * invdet;
                dpdu.y = (dv2 * dp1.y - dv1 * dp2.y) * invdet;
                dpdu.z = (dv2 * dp1.z - dv1 * dp2.z) * invdet;
                Vector3 dpdv = new Vector3();
                dpdv.x = (-du2 * dp1.x + du1 * dp2.x) * invdet;
                dpdv.y = (-du2 * dp1.y + du1 * dp2.y) * invdet;
                dpdv.z = (-du2 * dp1.z + du1 * dp2.z) * invdet;

                state.setBasis(OrthoNormalBasis.makeFromWV(state.getNormal(), dpdv));
            }
        } else
            state.setBasis(OrthoNormalBasis.makeFromW(state.getNormal()));
        int shaderIndex = faceShaders == null ? 0 : (faceShaders[t] & 0xFF);
        state.setShader(shaders[shaderIndex]);
    }

    protected class Triangle implements BoundedPrimitive {
        // private data for fast triangle intersection testing
        private int flags;
        private float nu, nv, nd;
        private float bnu, bnv, bnd;
        private float cnu, cnv, cnd;

        public Triangle(int tri) {
            flags = tri << 2;
            int index0 = 3 * tri + 0, index1 = 3 * tri + 1, index2 = 3 * tri + 2;
            if (triangles != null) {
                index0 = triangles[index0];
                index1 = triangles[index1];
                index2 = triangles[index2];
            }
            Point3 v0p = getPoint(index0);
            Point3 v1p = getPoint(index1);
            Point3 v2p = getPoint(index2);
            Vector3 ng = Vector3.cross(Point3.sub(v1p, v0p, new Vector3()), Point3.sub(v2p, v0p, new Vector3()), new Vector3()).normalize();
            if (Math.abs(ng.x) > Math.abs(ng.y) && Math.abs(ng.x) > Math.abs(ng.z))
                flags |= 0;
            else if (Math.abs(ng.y) > Math.abs(ng.z))
                flags |= 1;
            else
                flags |= 2;
            float ax, ay, bx, by, cx, cy;
            switch (flags & 3) {
                case 0: {
                    nu = ng.y / ng.x;
                    nv = ng.z / ng.x;
                    nd = v0p.x + (nu * v0p.y) + (nv * v0p.z);
                    ax = v0p.y;
                    ay = v0p.z;
                    bx = v2p.y - ax;
                    by = v2p.z - ay;
                    cx = v1p.y - ax;
                    cy = v1p.z - ay;
                    break;
                }
                case 1: {
                    nu = ng.z / ng.y;
                    nv = ng.x / ng.y;
                    nd = (nv * v0p.x) + v0p.y + (nu * v0p.z);
                    ax = v0p.z;
                    ay = v0p.x;
                    bx = v2p.z - ax;
                    by = v2p.x - ay;
                    cx = v1p.z - ax;
                    cy = v1p.x - ay;
                    break;
                }
                case 2:
                default: {
                    nu = ng.x / ng.z;
                    nv = ng.y / ng.z;
                    nd = (nu * v0p.x) + (nv * v0p.y) + v0p.z;
                    ax = v0p.x;
                    ay = v0p.y;
                    bx = v2p.x - ax;
                    by = v2p.y - ay;
                    cx = v1p.x - ax;
                    cy = v1p.y - ay;
                }
            }
            float det = bx * cy - by * cx;
            bnu = -by / det;
            bnv = bx / det;
            bnd = (by * ax - bx * ay) / det;
            cnu = cy / det;
            cnv = -cx / det;
            cnd = (cx * ay - cy * ax) / det;
        }

        protected int getTriangleNum() {
            return flags >>> 2;
        }

        public BoundingBox getBounds() {
            return Mesh.this.getBounds(3 * (flags >>> 2));
        }

        public float getBound(int i) {
            int a, b, c, t = 3 * getTriangleNum();
            if (Mesh.this.triangles == null) {
                a = 3 * (t + 0);
                b = 3 * (t + 1);
                c = 3 * (t + 2);
            } else {
                a = 3 * Mesh.this.triangles[t + 0];
                b = 3 * Mesh.this.triangles[t + 1];
                c = 3 * Mesh.this.triangles[t + 2];
            }
            int axis = i >>> 1;
            if ((i & 1) == 0)
                return MathUtils.min(Mesh.this.points[a + axis], Mesh.this.points[b + axis], Mesh.this.points[c + axis]);
            else
                return MathUtils.max(Mesh.this.points[a + axis], Mesh.this.points[b + axis], Mesh.this.points[c + axis]);
        }

        public boolean intersects(BoundingBox box) {
            return box.intersects(getBounds());
        }

        public void prepareShadingState(ShadingState state) {
            Mesh.this.prepareShadingState(state, flags >>> 2);
        }

        public void intersect(Ray r, IntersectionState state) {
            switch (flags & 3) {
                case 0: {
                    float det = 1.0f / (r.dx + nu * r.dy + nv * r.dz);
                    float t = (nd - r.ox - nu * r.oy - nv * r.oz) * det;
                    if (!r.isInside(t))
                        return;
                    float hu = r.oy + t * r.dy;
                    float hv = r.oz + t * r.dz;
                    float u = hu * bnu + hv * bnv + bnd;
                    if (u < 0.0f)
                        return;
                    float v = hu * cnu + hv * cnv + cnd;
                    if (v < 0.0f)
                        return;
                    if (u + v > 1.0f)
                        return;
                    r.setMax(t);
                    state.setIntersection(this, u, v);
                    return;
                }
                case 1: {
                    float det = 1.0f / (r.dy + nu * r.dz + nv * r.dx);
                    float t = (nd - r.oy - nu * r.oz - nv * r.ox) * det;
                    if (!r.isInside(t))
                        return;
                    float hu = r.oz + t * r.dz;
                    float hv = r.ox + t * r.dx;
                    float u = hu * bnu + hv * bnv + bnd;
                    if (u < 0.0f)
                        return;
                    float v = hu * cnu + hv * cnv + cnd;
                    if (v < 0.0f)
                        return;
                    if (u + v > 1.0f)
                        return;
                    r.setMax(t);
                    state.setIntersection(this, u, v);
                    return;
                }
                case 2: {
                    float det = 1.0f / (r.dz + nu * r.dx + nv * r.dy);
                    float t = (nd - r.oz - nu * r.ox - nv * r.oy) * det;
                    if (!r.isInside(t))
                        return;
                    float hu = r.ox + t * r.dx;
                    float hv = r.oy + t * r.dy;
                    float u = hu * bnu + hv * bnv + bnd;
                    if (u < 0.0f)
                        return;
                    float v = hu * cnu + hv * cnv + cnd;
                    if (v < 0.0f)
                        return;
                    if (u + v > 1.0f)
                        return;
                    r.setMax(t);
                    state.setIntersection(this, u, v);
                    return;
                }
            }
        }
    }

    private static class SmallTriangle implements BoundedPrimitive {
        private Mesh mesh;
        private int t;

        public SmallTriangle(Mesh mesh, int t) {
            this.mesh = mesh;
            this.t = 3 * t;
        }

        public BoundingBox getBounds() {
            return mesh.getBounds(t);
        }

        public float getBound(int i) {
            int a, b, c;
            if (mesh.triangles == null) {
                a = 3 * (t + 0);
                b = 3 * (t + 1);
                c = 3 * (t + 2);
            } else {
                a = 3 * mesh.triangles[t + 0];
                b = 3 * mesh.triangles[t + 1];
                c = 3 * mesh.triangles[t + 2];
            }
            int axis = i >>> 1;
            if ((i & 1) == 0)
                return MathUtils.min(mesh.points[a + axis], mesh.points[b + axis], mesh.points[c + axis]);
            else
                return MathUtils.max(mesh.points[a + axis], mesh.points[b + axis], mesh.points[c + axis]);
        }

        public boolean intersects(BoundingBox box) {
            return box.intersects(getBounds());
        }

        public void prepareShadingState(ShadingState state) {
            mesh.prepareShadingState(state, t / 3);
        }

        public void intersect(Ray r, IntersectionState state) {
            int a, b, c;
            if (mesh.triangles == null) {
                a = 3 * (t + 0);
                b = 3 * (t + 1);
                c = 3 * (t + 2);
            } else {
                a = 3 * mesh.triangles[t + 0];
                b = 3 * mesh.triangles[t + 1];
                c = 3 * mesh.triangles[t + 2];
            }
            double edge1x = mesh.points[b + 0] - mesh.points[a + 0];
            double edge1y = mesh.points[b + 1] - mesh.points[a + 1];
            double edge1z = mesh.points[b + 2] - mesh.points[a + 2];
            double edge2x = mesh.points[c + 0] - mesh.points[a + 0];
            double edge2y = mesh.points[c + 1] - mesh.points[a + 1];
            double edge2z = mesh.points[c + 2] - mesh.points[a + 2];
            double pvecx = r.dy * edge2z - r.dz * edge2y;
            double pvecy = r.dz * edge2x - r.dx * edge2z;
            double pvecz = r.dx * edge2y - r.dy * edge2x;
            double qvecx, qvecy, qvecz;
            double u, v;
            double det = edge1x * pvecx + edge1y * pvecy + edge1z * pvecz;
            if (det > 0) {
                double tvecx = r.ox - mesh.points[a + 0];
                double tvecy = r.oy - mesh.points[a + 1];
                double tvecz = r.oz - mesh.points[a + 2];
                u = (tvecx * pvecx + tvecy * pvecy + tvecz * pvecz);
                if (u < 0.0 || u > det)
                    return;
                qvecx = tvecy * edge1z - tvecz * edge1y;
                qvecy = tvecz * edge1x - tvecx * edge1z;
                qvecz = tvecx * edge1y - tvecy * edge1x;
                v = (r.dx * qvecx + r.dy * qvecy + r.dz * qvecz);
                if (v < 0.0 || u + v > det)
                    return;
            } else if (det < 0) {
                double tvecx = r.ox - mesh.points[a + 0];
                double tvecy = r.oy - mesh.points[a + 1];
                double tvecz = r.oz - mesh.points[a + 2];
                u = (tvecx * pvecx + tvecy * pvecy + tvecz * pvecz);
                if (u > 0.0 || u < det)
                    return;
                qvecx = tvecy * edge1z - tvecz * edge1y;
                qvecy = tvecz * edge1x - tvecx * edge1z;
                qvecz = tvecx * edge1y - tvecy * edge1x;
                v = (r.dx * qvecx + r.dy * qvecy + r.dz * qvecz);
                if (v > 0.0 || u + v < det)
                    return;
            } else
                return;
            double inv_det = 1.0 / det;
            float t = (float) ((edge2x * qvecx + edge2y * qvecy + edge2z * qvecz) * inv_det);
            if (r.isInside(t)) {
                r.setMax(t);
                state.setIntersection(this, (float) (u * inv_det), (float) (v * inv_det));
            }
        }
    }
}