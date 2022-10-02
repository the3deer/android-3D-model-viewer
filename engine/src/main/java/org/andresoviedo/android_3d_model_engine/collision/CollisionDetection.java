package org.andresoviedo.android_3d_model_engine.collision;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.BoundingBox;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.model.Triangle;
import org.andresoviedo.util.math.Math3DUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class that encapsulates all the logic for the collision detection algorithm.
 *
 * @author andresoviedo
 */
public class  CollisionDetection {

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
     * @param nearHit        the ray start point
     * @param farHit the ray far hit
     * @param direction the ray direction
     * @return the object intersected by the specified ray
     */
    public static Object3DData getBoxIntersection(List<Object3DData> objects, float[] nearHit, float[] farHit, float[] direction) {
        float min = Float.MAX_VALUE;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            if ("Point".equals(obj.getId()) || "Line".equals(obj.getId())) {
                continue;
            }

            float[] invertedModelMatrix = new float[16];
            Matrix.invertM(invertedModelMatrix,0, obj.getModelMatrix(),0);
            float[] nearAA = new float[4];
            float[] farAA = new float[4];
            Matrix.multiplyMV(nearAA,0,invertedModelMatrix,0,nearHit,0);
            Matrix.multiplyMV(farAA,0,invertedModelMatrix,0,farHit,0);
            float[] dirAA = Math3DUtils.substract(farAA,nearAA);
            Math3DUtils.normalize(dirAA);

            float[] intersection = getBoxIntersection(nearAA, dirAA, obj.getBoundingBox());
            if (intersection[0] > 0 && intersection[0] <= intersection[1] && intersection[0] < min) {
                min = intersection[0];
                ret = obj;
            }
        }
        if (ret != null) {
            //Log.i("CollisionDetection", "Collision detected '" + ret.getId() + "' distance: " + min);
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
    private static float[] boxIntersection = new float[2];
    public static float[] getBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
        float[] tMin = Math3DUtils.divide(Math3DUtils.substract(b.getMin(), origin), dir);
        float[] tMax = Math3DUtils.divide(Math3DUtils.substract(b.getMax(), origin), dir);
        float[] t1 = Math3DUtils.min(tMin, tMax);
        float[] t2 = Math3DUtils.max(tMin, tMax);
        float tNear = Math.max(Math.max(t1[0], t1[1]), t1[2]);
        float tFar = Math.min(Math.min(t2[0], t2[1]), t2[2]);
        boxIntersection[0] = tNear;
        boxIntersection[1] = tFar;
        return boxIntersection;
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

    public static Collision getTriangleIntersection(List<Object3DData> objects, int width, int height, float[] modelViewMatrix, float[] modelProjectionMatrix, float windowX, float windowY) {
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

    public static Collision getTriangleIntersection(Object3DData hit, int width, int height, float[] viewMatrix, float[] projectionMatrix, float windowX, float windowY) {
        float[] nearHit = unProject(width, height, viewMatrix, projectionMatrix, windowX, windowY, 0);
        float[] farHit = unProject(width, height, viewMatrix, projectionMatrix, windowX, windowY, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);
        return getTriangleIntersection(hit, nearHit, farHit, direction);
    }

    public static Collision getTriangleIntersection(final Object3DData hit, float[] nearHit, float[] farHit, float[] direction) {
        //Log.d("CollisionDetection", "Getting triangle intersection: " + hit.getId());
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
        Matrix.invertM(inverted,0, hit.getModelMatrix(),0);
        float[] nearAA = new float[4];
        float[] farAA = new float[4];
        Matrix.multiplyMV(nearAA,0,inverted,0,nearHit,0);
        Matrix.multiplyMV(farAA,0,inverted,0,farHit,0);
        float[] dirAA = Math3DUtils.substract(farAA,nearAA);
        Math3DUtils.normalize(dirAA);

        return getTriangleIntersectionForOctree(octree, nearAA, dirAA);
    }

    public static Collision getTriangleIntersection2(final Object3DData hit, float[] position, float[] direction) {
        //Log.d("CollisionDetection", "Getting triangle intersection: " + hit.getId());
        Octree octree;
        //synchronized (hit) {
            octree = hit.getOctree();
            if (octree == null) {
                octree = Octree.build(hit);
                hit.setOctree(octree);
            }
        //}
        return getTriangleIntersectionForOctree2(octree, position, direction);
    }

    static float[] vertex0 = new float[3];
    static float[] vertex1 = new float[3];
    static float[] vertex2 = new float[3];
    private static Collision getTriangleIntersectionForOctree(Octree octree, float[] rayOrigin, float[] rayDirection) {
        //Log.v("CollisionDetection","Testing octree "+octree);
        if (!isBoxIntersection(rayOrigin, rayDirection, octree.boundingBox)) {
            //Log.d("CollisionDetection", "No octree intersection");
            return null;
        }
        Octree selected = null;
        Collision minCollision = null;
        float min = Float.MAX_VALUE;
        for (int i=0; i<octree.children.length; i++){
            final Octree child = octree.children[i];
            Collision intersection = getTriangleIntersectionForOctree(child, rayOrigin, rayDirection);
            if (intersection != null && (minCollision == null || intersection.getDistance() < minCollision.getDistance() ) ) {
                //Log.d("CollisionDetection", "Octree intersection: " + intersection);
                minCollision = intersection;
                min = intersection.getDistance();
                selected = child;
            }
        }
        Triangle selectedTriangle = null;
        for (int i=0; i< octree.triangles.size(); i++) {
            // for (float[] triangle : octree.getTriangles()) {
            final Triangle triangle = octree.triangles.get(i);
            float intersection = getTriangleIntersection(rayOrigin, rayDirection, triangle.v1, triangle.v2, triangle.v3);
            if (intersection != -1 && intersection < min) {
                min = intersection;
                selectedTriangle = triangle;
                selected = octree;
            }
        }
        if (min != Float.MAX_VALUE) {
            //Log.d("CollisionDetection", "Intersection at distance: " + min);
            //Log.d("CollisionDetection", "Intersection at triangle: " + Arrays.toString(selectedTriangle));
            //Log.d("CollisionDetection", "Intersection at octree: " + selected);
            float[] intersectionPoint = Math3DUtils.add(rayOrigin, Math3DUtils.multiply(rayDirection, min));
            return new Collision(min,intersectionPoint, selectedTriangle);
        }
        return null;
    }

    private static Collision getTriangleIntersectionForOctree2(Octree octree, float[] position, float[] direction) {
        //Log.v("CollisionDetection","Testing octree "+octree);
        //if (!octree.bsphere.insideBounds(position) || !octree.boundingBox.insideBounds(position[0],position[1],position[2])) {
        if (!isBoxIntersection(position, direction, octree.boundingBox)) {
            //Log.d("CollisionDetection", "No octree intersection");
            return null;
        }
        Octree selected = null;
        Collision minCollision = null;
        float min = Float.MAX_VALUE;
        Triangle selectedTriangle = null;
        // for (Octree child : octree.getChildren()) {

        if (octree.children != null) {
            for (int i = 0; i < octree.children.length; i++) {
                Octree child = octree.children[i];
                if (!isBoxIntersection(position, direction, child.boundingBox)) {
                    //Log.d("CollisionDetection", "No octree intersection");
                    continue;
                }
                Collision intersection = getTriangleIntersectionForOctree2(child, position, direction);
                if (intersection != null && (minCollision == null || intersection.getDistance() < minCollision.getDistance())) {
                    //Log.d("CollisionDetection", "Octree intersection: " + intersection);
                    minCollision = intersection;
                    min = intersection.getDistance();
                    selectedTriangle = intersection.getTriangle();
                    selected = child;
                }
                //break;
            }
        } else {
            for (Iterator<Triangle> it = octree.triangles.iterator(); it.hasNext(); ) {
                final Triangle triangle = it.next();
                //if (triangle.bsphere.insideBounds(position) && triangle.bbox.insideBounds(position)) {
                    float intersection = getTriangleIntersection(position, direction, triangle.v1, triangle.v2, triangle.v3);
                    if (intersection != -1 && intersection < min) {
                        min = intersection;
                        selectedTriangle = triangle;
                        selected = octree;
                    }

            }
        }
        if (min != Float.MAX_VALUE) {
            //Log.d("CollisionDetection", "Intersection at distance: " + min);
            //Log.d("CollisionDetection", "Intersection at triangle: " + Arrays.toString(selectedTriangle));
            //Log.d("CollisionDetection", "Intersection at octree: " + selected);
            float[] intersectionPoint = Math3DUtils.add(position, Math3DUtils.multiply(direction, min));
            return new Collision(min,intersectionPoint, selectedTriangle);
        }
        return null;
    }

    private static float[] edge1 = {0,0,0};
    private static float[] edge2 = {0,0,0};
    private static float[] h = {0,0,0};
    private static float[] s = {0,0,0};
    private static float[] q = {0,0,0};
    public static float getTriangleIntersection(float[] rayOrigin,
                                                 float[] rayVector,
                                                 float[] vertex0, float[] vertex1, float[] vertex2) {
        float EPSILON = 0.0000001f;
        float a, f, u, v;
        edge1 = Math3DUtils.substract(vertex1, vertex0, edge1);
        edge2 = Math3DUtils.substract(vertex2, vertex0, edge2);
        h = Math3DUtils.crossProduct(rayVector, edge2, h);
        a = Math3DUtils.dotProduct(edge1, h);
        if (a > -EPSILON && a < EPSILON)
            return -1;
        f = 1 / a;
        s = Math3DUtils.substract(rayOrigin, vertex0, s);
        u = f * Math3DUtils.dotProduct(s, h);
        if (u < 0.0 || u > 1.0)
            return -1;
        q = Math3DUtils.crossProduct(s, edge1, q);
        v = f * Math3DUtils.dotProduct(rayVector, q);
        if (v < 0.0 || u + v > 1.0)
            return -1;
        // At this stage we can compute t to find out where the intersection point is on the line.
        float t = f * Math3DUtils.dotProduct(edge2, q);
        if (t > EPSILON) // ray intersection
        {
            //Log.d("CollisionDetection", "Triangle intersection at: " + t);
            return t;
        } else // This means that there is a line intersection but not a ray intersection.
            return -1;
    }

    /*public static boolean isPointInsideSphere(float[] point, float[] sphere) {
        // we are using multiplications because is faster than calling Math.pow
        var distance = Math.sqrt((point.x - sphere.x) * (point.x - sphere.x) +
                (point.y - sphere.y) * (point.y - sphere.y) +
                (point.z - sphere.z) * (point.z - sphere.z));
        return distance < sphere.radius;
    }*/
}

