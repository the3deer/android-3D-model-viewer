package org.the3deer.android_3d_model_engine.collision;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import org.the3deer.android_3d_model_engine.model.BoundingBox;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.util.math.Math3DUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Class that encapsulates all the logic for the collision detection algorithm.
 *
 * @author andresoviedo
 */
public class CollisionDetection {

    /**
     * Get the nearest object intersected by the specified window coordinates
     *
     * @param objects               the list of objects to test
     * @param height                viewport height
     * @param width                 viewport width
     * @param modelViewMatrix       model view matrix
     * @param modelProjectionMatrix model projection matrix
     * @param windowX               the window x coordinate
     * @param windowY               the window y coordinate
     * @return the nearest object intersected by the specified coordinates or null
     */
    public static Object3DData getBoxIntersection(List<Object3DData> objects, int width, int height, float[] modelViewMatrix, float[] modelProjectionMatrix, float windowX, float windowY) {
        float[] nearHit = unProject(width, height, modelViewMatrix, modelProjectionMatrix, windowX, windowY, 0);
        float[] farHit = unProject(width, height, modelViewMatrix, modelProjectionMatrix, windowX, windowY, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);
        return getBoxIntersection(objects, nearHit, farHit, direction);
    }

    /**
     * Get the nearest object intersected by the specified ray or null if no object is intersected
     *
     * @param objects   the list of objects to test
     * @param nearHit   the ray start point
     * @param farHit    the ray far hit
     * @param direction the ray direction
     * @return the object intersected by the specified ray
     */
    private static Object3DData getBoxIntersection(List<Object3DData> objects, float[] nearHit, float[] farHit, float[] direction) {
        float min = Float.MAX_VALUE;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            if ("Point".equals(obj.getId()) || "Line".equals(obj.getId())) {
                continue;
            }

            float[] invertedModelMatrix = new float[16];
            Matrix.invertM(invertedModelMatrix, 0, obj.getModelMatrix(), 0);
            float[] nearAA = new float[4];
            float[] farAA = new float[4];
            Matrix.multiplyMV(nearAA, 0, invertedModelMatrix, 0, nearHit, 0);
            Matrix.multiplyMV(farAA, 0, invertedModelMatrix, 0, farHit, 0);
            float[] dirAA = Math3DUtils.substract(farAA, nearAA);
            Math3DUtils.normalize(dirAA);

            float[] intersection = getBoxIntersection(nearAA, dirAA, obj.getBoundingBox());
            if (intersection[0] > 0 && intersection[0] <= intersection[1] && intersection[0] < min) {
                min = intersection[0];
                ret = obj;
            }
        }
        if (ret != null) {
            Log.i("CollisionDetection", "Collision detected '" + ret.getId() + "' distance: " + min);
        }
        return ret;
    }

    /*
     * Get the entry and exit point of the ray intersecting the nearest object or null if no object is intersected
     *
     * @param objects list of objects to test
     * @param p1      ray start point
     * @param p2      ray end point
     * @return the entry and exit point of the ray intersecting the nearest object
     */
    /*public static float[] getBoxIntersectionPoint(List<Object3DData> objects, float[] p1, float[] p2) {
        float[] direction = Math3DUtils.substract(p2, p1);
        Math3DUtils.normalize(direction);
        float min = Float.MAX_VALUE;
        float[] intersection2 = null;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            BoundingBoxBuilder box = obj.getBoundingBox();
            float[] intersection = getBoxIntersection(p1, direction, box);
            if (intersection[0] > 0 && intersection[0] <= intersection[1] && intersection[0] < min) {
                min = intersection[0];
                ret = obj;
                intersection2 = intersection;
            }
        }
        if (ret != null) {
            Log.i("CollisionDetection", "Collision detected '" + ret.getId() + "' distance: " + min);
            return new float[]{p1[0] + direction[0] * min, p1[1] + direction[1] * min, p1[2] + direction[2] * min};
        }
        return null;
    }*/

    /**
     * Return true if the specified ray intersects the bounding box
     *
     * @param origin origin of the ray
     * @param dir    direction of the ray
     * @param b      bounding box
     * @return true if the specified ray intersects the bounding box, false otherwise
     */
    private static boolean isBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
        float[] intersection = getBoxIntersection(origin, dir, b);
        return intersection[0] >= 0 && intersection[0] <= intersection[1];
    }

    /**
     * Get the intersection points of the near and far plane for the specified ray and bounding box
     *
     * @param origin the ray origin
     * @param dir    the ray direction
     * @param b      the bounding box
     * @return the intersection points of the near and far plane
     */
    public static float[] getBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
        float[] tMin = Math3DUtils.divide(Math3DUtils.substract(b.getMin(), origin), dir);
        float[] tMax = Math3DUtils.divide(Math3DUtils.substract(b.getMax(), origin), dir);
        float[] t1 = Math3DUtils.min(tMin, tMax);
        float[] t2 = Math3DUtils.max(tMin, tMax);
        float tNear = Math.max(Math.max(t1[0], t1[1]), t1[2]);
        float tFar = Math.min(Math.min(t2[0], t2[1]), t2[2]);
        return new float[]{tNear, tFar};
    }

    /**
     * Map window coordinates to object coordinates.
     *
     * @param height                viewport height
     * @param width                 viewport width
     * @param modelViewMatrix       model view matrix
     * @param modelProjectionMatrix model projection matrix
     * @param rx                    x point
     * @param ry                    y point
     * @param rz                    z point
     * @return the corresponding near and far vertex for the specified window coordinates
     */
    public static float[] unProject(int width, int height, float[] modelViewMatrix, float[] modelProjectionMatrix,
                                    float rx, float ry, float rz) {
        float[] xyzw = {0, 0, 0, 0};
        ry = (float) height - ry;
        int[] viewport = {0, 0, width, height};
        GLU.gluUnProject(rx, ry, rz, modelViewMatrix, 0, modelProjectionMatrix, 0,
                viewport, 0, xyzw, 0);
        xyzw[0] /= xyzw[3];
        xyzw[1] /= xyzw[3];
        xyzw[2] /= xyzw[3];
        xyzw[3] = 1;
        return xyzw;
    }

    /*public static float[] getTriangleIntersection(List<Object3DData> objects, ModelRenderer mRenderer, float
            windowX, float windowY) {
        float[] nearHit = unProject(mRenderer, windowX, windowY, 0);
        float[] farHit = unProject(mRenderer, windowX, windowY, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);
        Object3DData intersected = getBoxIntersection(objects, nearHit, direction);
        if (intersected != null) {
            Log.d("CollisionDetection", "intersected:" + intersected.getId() + ", rayOrigin:" + Arrays.toString(nearHit) + ", rayVector:" + Arrays.toString(direction));
            FloatBuffer buffer = intersected.getVertexArrayBuffer().asReadOnlyBuffer();
            float[] modelMatrix = intersected.getModelMatrix();
            buffer.position(0);
            float[] selectedv1 = null;
            float[] selectedv2 = null;
            float[] selectedv3 = null;
            float min = Float.MAX_VALUE;
            for (int i = 0; i < buffer.capacity(); i += 9) {
                float[] v1 = new float[]{buffer.get(), buffer.get(), buffer.get(), 1};
                float[] v2 = new float[]{buffer.get(), buffer.get(), buffer.get(), 1};
                float[] v3 = new float[]{buffer.get(), buffer.get(), buffer.get(), 1};
                Matrix.multiplyMV(v1, 0, modelMatrix, 0, v1, 0);
                Matrix.multiplyMV(v2, 0, modelMatrix, 0, v2, 0);
                Matrix.multiplyMV(v3, 0, modelMatrix, 0, v3, 0);
                float t = getTriangleIntersection(nearHit, direction, v1, v2, v3);
                if (t != -1 && t < min) {
                    min = t;
                    selectedv1 = v1;
                    selectedv2 = v2;
                    selectedv3 = v3;
                }
            }
            if (selectedv1 != null) {
                float[] outIntersectionPoint = Math3DUtils.add(nearHit, Math3DUtils.multiply(direction, min));
                return outIntersectionPoint;
            }
        }
        return null;
    }*/

    public static float[] getTriangleIntersection(List<Object3DData> objects, int width, int height, float[] modelViewMatrix, float[] modelProjectionMatrix, float windowX, float windowY) {
        float[] nearHit = unProject(width, height, modelViewMatrix, modelProjectionMatrix, windowX, windowY, 0);
        float[] farHit = unProject(width, height, modelViewMatrix, modelProjectionMatrix, windowX, windowY, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);
        Object3DData intersected = getBoxIntersection(objects, nearHit, farHit, direction);
        if (intersected != null) {
            return getTriangleIntersection(intersected, nearHit, farHit, direction);
        }
        return null;
    }

    public static float[] getTriangleIntersection(Object3DData hit, int width, int height, float[] viewMatrix, float[] projectionMatrix, float windowX, float windowY) {
        float[] nearHit = unProject(width, height, viewMatrix, projectionMatrix, windowX, windowY, 0);
        float[] farHit = unProject(width, height, viewMatrix, projectionMatrix, windowX, windowY, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);
        return getTriangleIntersection(hit, nearHit, farHit, direction);
    }

    private static float[] getTriangleIntersection(final Object3DData hit, float[] nearHit, float[] farHit, float[] direction) {
        Log.d("CollisionDetection", "Getting triangle intersection: " + hit.getId());

        Octree octree;
        synchronized (hit) {
            octree = hit.getOctree();
            if (octree == null) {
                octree = Octree.build(hit);
                hit.setOctree(octree);
            }
        }

        // invert ray
        float[] inverted = new float[16];
        Matrix.invertM(inverted, 0, hit.getModelMatrix(), 0);
        float[] nearAA = new float[4];
        float[] farAA = new float[4];
        Matrix.multiplyMV(nearAA, 0, inverted, 0, nearHit, 0);
        Matrix.multiplyMV(farAA, 0, inverted, 0, farHit, 0);
        float[] dirAA = Math3DUtils.substract(farAA, nearAA);
        Math3DUtils.normalize(dirAA);

        float intersection = getTriangleIntersectionForOctree(octree, nearAA, dirAA);
        if (intersection != -1) {
            float[] intersectionPoint = Math3DUtils.add(nearAA, Math3DUtils.multiply(dirAA, intersection));
            float[] realIntersection = new float[4];
            Matrix.multiplyMV(realIntersection, 0, hit.getModelMatrix(), 0, Math3DUtils.to4d(intersectionPoint), 0);
            Log.d("CollisionDetection", "Collision point: " + Arrays.toString(realIntersection));
            return realIntersection;
        } else {
            return null;
        }
    }

    private static float getTriangleIntersectionForOctree(Octree octree, float[] rayOrigin, float[] rayDirection) {
        //Log.v("CollisionDetection","Testing octree "+octree);
        if (!isBoxIntersection(rayOrigin, rayDirection, octree.boundingBox)) {
            Log.d("CollisionDetection", "No octree intersection");
            return -1;
        }
        Octree selected = null;
        float min = Float.MAX_VALUE;
        for (Octree child : octree.getChildren()) {
            if (child == null) {
                continue;
            }
            float intersection = getTriangleIntersectionForOctree(child, rayOrigin, rayDirection);
            if (intersection != -1 && intersection < min) {
                Log.d("CollisionDetection", "Octree intersection: " + intersection);
                min = intersection;
                selected = child;
            }
        }
        float[] selectedTriangle = null;
        for (float[] triangle : octree.getTriangles()) {
            float[] vertex0 = new float[]{triangle[0], triangle[1], triangle[2]};
            float[] vertex1 = new float[]{triangle[4], triangle[5], triangle[6]};
            float[] vertex2 = new float[]{triangle[8], triangle[9], triangle[10]};
            float intersection = getTriangleIntersection(rayOrigin, rayDirection, vertex0, vertex1, vertex2);
            if (intersection != -1 && intersection < min) {
                min = intersection;
                selectedTriangle = triangle;
                selected = octree;

            }
        }
        if (min != Float.MAX_VALUE) {
            Log.d("CollisionDetection", "Intersection at distance: " + min);
            Log.d("CollisionDetection", "Intersection at triangle: " + Arrays.toString(selectedTriangle));
            Log.d("CollisionDetection", "Intersection at octree: " + selected);
            return min;
        }
        return -1;
    }

    private static float getTriangleIntersection(float[] rayOrigin,
                                                 float[] rayVector,
                                                 float[] vertex0, float[] vertex1, float[] vertex2) {
        float EPSILON = 0.0000001f;
        float[] edge1, edge2, h, s, q;
        float a, f, u, v;
        edge1 = Math3DUtils.substract(vertex1, vertex0);
        edge2 = Math3DUtils.substract(vertex2, vertex0);
        h = Math3DUtils.crossProduct(rayVector, edge2);
        a = Math3DUtils.dotProduct(edge1, h);
        if (a > -EPSILON && a < EPSILON)
            return -1;
        f = 1 / a;
        s = Math3DUtils.substract(rayOrigin, vertex0);
        u = f * Math3DUtils.dotProduct(s, h);
        if (u < 0.0 || u > 1.0)
            return -1;
        q = Math3DUtils.crossProduct(s, edge1);
        v = f * Math3DUtils.dotProduct(rayVector, q);
        if (v < 0.0 || u + v > 1.0)
            return -1;
        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * Math3DUtils.dotProduct(edge2, q);
        if (t > EPSILON) // ray intersection
        {
            Log.d("CollisionDetection", "Triangle intersection at: " + t);
            return t;
        } else // This means that there is a line intersection but not a ray intersection.
            return -1;
    }

    /**
     * Calculate the line segment PaPb that is the shortest route between
     * two lines P1P2 and P3P4. Calculate also the values of mua and mub where
     * Pa = P1 + mua (P2 - P1)
     * Pb = P3 + mub (P4 - P3)
     * Return FALSE if no solution exists.
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return
     */
    public static float[] lineLineIntersection(float[] p1, float[] p2, float[] p3, float[] p4) {
        // Algorithm is ported from the C algorithm of
        // Paul Bourke at http://paulbourke.net/geometry/pointlineplane/
        // http://paulbourke.net/geometry/pointlineplane/lineline.c

        // any vertex in common?
        if (Math3DUtils.equals(p1, p3) || Math3DUtils.equals(p1, p4)) {
            return p1;
        } else if (Math3DUtils.equals(p2, p3) || Math3DUtils.equals(p2, p4)) {
            return p2;
        }

        float[] p13 = Math3DUtils.substract(p1, p3);
        float[] p43 = Math3DUtils.substract(p4, p3);
        float[] p21 = Math3DUtils.substract(p2, p1);

        double d1343 = p13[0] * (double) p43[0] + (double) p13[1] * p43[1] + (double) p13[2] * p43[2];
        double d4321 = p43[0] * (double) p21[0] + (double) p43[1] * p21[1] + (double) p43[2] * p21[2];
        double d1321 = p13[0] * (double) p21[0] + (double) p13[1] * p21[1] + (double) p13[2] * p21[2];
        double d4343 = p43[0] * (double) p43[0] + (double) p43[1] * p43[1] + (double) p43[2] * p43[2];
        double d2121 = p21[0] * (double) p21[0] + (double) p21[1] * p21[1] + (double) p21[2] * p21[2];

        double denom = d2121 * d4343 - d4321 * d4321;
        if (Math.abs(denom) < 0.00001f) {
            return null;
        }
        double numer = d1343 * d4321 - d1321 * d4343;

        double mua = numer / denom;
        double mub = (d1343 + d4321 * mua) / d4343;

        float[] ret1 = new float[3];
        float[] ret2 = new float[3];
        ret1[0] = (float) (p1[0] + mua * p21[0]);
        ret1[1] = (float) (p1[1] + mua * p21[1]);
        ret1[2] = (float) (p1[2] + mua * p21[2]);
        ret2[0] = (float) (p3[0] + mub * p43[0]);
        ret2[1] = (float) (p3[1] + mub * p43[1]);
        ret2[2] = (float) (p3[2] + mub * p43[2]);

        if (mua < 0 || mua > 1 || mub < 0 || mub > 1) {
            return null;
        }

        Math3DUtils.round(ret1, 10000);
        Math3DUtils.round(ret2, 10000);
        if (!Math3DUtils.equals(ret1, ret2)) {
            return null;
        }

        return ret1;
    }

    private static boolean sameSide(float[] p1, float[] p2, float[] a, float[] b) {
        float[] cp1 = Math3DUtils.cross(Math3DUtils.substract(b, a), Math3DUtils.substract(p1, a));
        float[] cp2 = Math3DUtils.cross(Math3DUtils.substract(b, a), Math3DUtils.substract(p2, a));
        Math3DUtils.normalize(cp1);
        Math3DUtils.normalize(cp2);
        return Math3DUtils.equals(cp1, cp2);
    }

    /*public static boolean pointInTriangle(float[] p, float[] a, float[] b, float[] c) {
        return sameSide(p, a, b, c) && sameSide(p, b, a, c) && sameSide(p, c, a, b);
    }*/

    public static boolean pointInTriangle_sameside(float[] p, float[] v1, float[] v2, float[] v3) {

        /*if (Math3DUtils.equals(p,v1) || Math3DUtils.equals(p,v2) || Math3DUtils.equals(p,v3)) {
            return false;
        }*/

        double x1 = v1[0];
        double y1 = v1[1];
        double x2 = v2[0];
        double y2 = v2[1];
        double x3 = v3[0];
        double y3 = v3[1];

        double y23 = y2 - y3;
        double x32 = x3 - x2;
        double y31 = y3 - y1;
        double x13 = x1 - x3;
        double det = y23 * x13 - x32 * y31;
        double minD = Math.min(det, 0);
        double maxD = Math.max(det, 0);

        double x = p[0];
        double y = p[1];

        double dx = x - x3;
        double dy = y - y3;
        double a = y23 * dx + x32 * dy;
        if (a < minD || a > maxD)
            return false;
        double b = y31 * dx + x13 * dy;
        if (b < minD || b > maxD)
            return false;
        double c = det - a - b;
        if (c < minD || c > maxD)
            return false;
        return true;

    }

    //https://gdbooks.gitbooks.io/3dcollisions/content/Chapter4/point_in_triangle.html
    public static boolean pointInTriangle(float[] p, float[] a, float[] b, float[] c) {
        // Lets define some local variables, we can change these
        // without affecting the references passed in
        // Vector3 p = point;
        //Vector3 a = t.p0;
        //Vector3 b = t.p1;
        //Vector3 c = t.p2;

        // Move the triangle so that the point becomes the
        // triangles origin
        a = Math3DUtils.substract(a, p);// a -= p;
        b = Math3DUtils.substract(b, p);// a -= p; //b -= p;
        c = Math3DUtils.substract(c, p);// a -= p;//c -=p;

        // The point should be moved too, so they are both
        // relative, but because we don't use p in the
        // equation anymore, we don't need it!
        // p -= p;

        // Compute the normal vectors for triangles:
        // u = normal of PBC
        // v = normal of PCA
        // w = normal of PAB

        float[] u = Math3DUtils.cross(b,c); //       Vector3 u = Cross(b, c);
        float[] v = Math3DUtils.cross(c,a); //Vector3 v = Cross(c, a);
        float[] w = Math3DUtils.cross(a,b); //Vector3 w = Cross(a, b);

        // Test to see if the normals are facing
        // the same direction, return false if not
        if (Math3DUtils.dot(u,v) < 0f){
        // if (Dot(u, v) < 0f) {
            return false;
        }
        if (Math3DUtils.dot(u,w) < 0f){
        //if (dot(u, w) < 0.0f) {
            return false;
        }

        // All normals facing the same way, return true
        return true;
    }
}

