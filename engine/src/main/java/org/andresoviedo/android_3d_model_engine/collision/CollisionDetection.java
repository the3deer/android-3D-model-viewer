package org.andresoviedo.android_3d_model_engine.collision;

import android.opengl.GLU;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.BoundingBox;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.math.Math3DUtils;

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
        return getBoxIntersection(objects, nearHit, direction);
    }

    /**
     * Get the nearest object intersected by the specified ray or null if no object is intersected
     *
     * @param objects   the list of objects to test
     * @param p1        the ray start point
     * @param direction the ray direction
     * @return the object intersected by the specified ray
     */
    private static Object3DData getBoxIntersection(List<Object3DData> objects, float[] p1, float[] direction) {
        float min = Float.MAX_VALUE;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            if ("Point".equals(obj.getId()) || "Line".equals(obj.getId())) {
                continue;
            }
            BoundingBox box = obj.getBoundingBox();
            float[] intersection = getBoxIntersection(p1, direction, box);
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
        return intersection[0] > 0 && intersection[0] < intersection[1];
    }

    /**
     * Get the intersection points of the near and far plane for the specified ray and bounding box
     *
     * @param origin the ray origin
     * @param dir    the ray direction
     * @param b      the bounding box
     * @return the intersection points of the near and far plane
     */
    private static float[] getBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
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
    private static float[] unProject(int width, int height, float[] modelViewMatrix, float[] modelProjectionMatrix,
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
        Object3DData intersected = getBoxIntersection(objects, nearHit, direction);
        if (intersected != null) {
            Log.d("CollisionDetection", "intersected: " + intersected.getId());
            Octree octree;
            synchronized (intersected) {
                octree = intersected.getOctree();
                if (octree == null) {
                    octree = Octree.build(intersected);
                    intersected.setOctree(octree);
                }
            }
            float intersection = getTriangleIntersectionForOctree(octree, nearHit, direction);
            if (intersection != -1) {
                float[] intersectionPoint = Math3DUtils.add(nearHit, Math3DUtils.multiply(direction, intersection));
                Log.d("CollisionDetection", "Interaction point: " + Arrays.toString(intersectionPoint));
                return intersectionPoint;
            } else {
                return null;
            }
        }
        return null;
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
}

