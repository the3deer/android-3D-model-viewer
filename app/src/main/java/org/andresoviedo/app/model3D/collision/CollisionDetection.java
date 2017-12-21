package org.andresoviedo.app.model3D.collision;

import android.opengl.GLU;
import android.util.Log;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.view.ModelRenderer;
import org.andresoviedo.app.util.math.Math3DUtils;

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
     * @param objects   the list of objects to test
     * @param mRenderer the model renderer to unproject window coordinates to world coordinates
     * @param windowX   the window x coordinate
     * @param windowY   the window y coordinate
     * @return the nearest object intersected by the specified coordinates or null
     */
    public static Object3DData getIntersection(List<Object3DData> objects, ModelRenderer mRenderer, float windowX, float windowY) {
        float[] nearHit = unproject(mRenderer, windowX, windowY, 0);
        float[] farHit = unproject(mRenderer, windowX, windowY, 1);
        return getIntersection(objects, nearHit, farHit);
    }

    /**
     * Get the nearest object intersected by the specified ray or null if no object is intersected
     *
     * @param objects the list of objects to test
     * @param p1      the ray start point
     * @param p2      the ray end point
     * @return the object intersected by the specified ray
     */
    public static Object3DData getIntersection(List<Object3DData> objects, float[] p1, float[] p2) {
        float[] direction = Math3DUtils.substract(p2, p1);
        Math3DUtils.normalize(direction);
        float min = Float.MAX_VALUE;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            if (obj.getId().startsWith("Line")) continue;
            BoundingBox box = obj.getBoundingBox();
            float[] intersection = getBoxIntersection(p1, direction, box);
            if (intersection[0] > 0 && intersection[0] < intersection[1] && intersection[0] < min) {
                min = intersection[0];
                ret = obj;
            }
        }
        if (ret != null) {
            Log.i("CollisionDetection", "Collision detected '" + ret.getId() + "' distance: " + min);
        }
        return ret;
    }

    /**
     * Get the entry and exit point of the ray intersecting the nearest object or null if no object is intersected
     *
     * @param objects list of objects to test
     * @param p1      ray start point
     * @param p2      ray end point
     * @return the entry and exit point of the ray intersecting the nearest object
     */
    public static float[] getIntersectionPoint(List<Object3DData> objects, float[] p1, float[] p2) {
        float[] direction = Math3DUtils.substract(p2, p1);
        Math3DUtils.normalize(direction);
        float min = Float.MAX_VALUE;
        float[] intersection2 = null;
        Object3DData ret = null;
        for (Object3DData obj : objects) {
            if (obj.getId().startsWith("Line")) continue;
            BoundingBox box = obj.getBoundingBox();
            float[] intersection = getBoxIntersection(p1, direction, box);
            if (intersection[0] > 0 && intersection[0] < intersection[1] && intersection[0] < min) {
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
    }

    /**
     * Return true if the specified ray intersects the bounding box
     *
     * @param origin origin of the ray
     * @param dir    direction of the ray
     * @param b      bounding box
     * @return true if the specified ray intersects the bounding box, false otherwise
     */
    public static boolean isBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
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
    public static float[] getBoxIntersection(float[] origin, float[] dir, BoundingBox b) {
        float[] tMin = Math3DUtils.divide(Math3DUtils.substract(b.getCurrentMin(), origin), dir);
        float[] tMax = Math3DUtils.divide(Math3DUtils.substract(b.getCurrentMax(), origin), dir);
        float[] t1 = Math3DUtils.min(tMin, tMax);
        float[] t2 = Math3DUtils.max(tMin, tMax);
        float tNear = Math.max(Math.max(t1[0], t1[1]), t1[2]);
        float tFar = Math.min(Math.min(t2[0], t2[1]), t2[2]);
        return new float[]{tNear, tFar};
    }

    /**
     * Get the corresponding near and far vertex for the specified window coordinates
     *
     * @param mRenderer
     * @param rx
     * @param ry
     * @param rz
     * @return the corresponding near and far vertex for the specified window coordinates
     */
    public static float[] unproject(ModelRenderer mRenderer, float rx, float ry, float rz) {
        float[] xyzw = {0, 0, 0, 0};
        ry = (float) mRenderer.getHeight() - ry;
        int[] viewport = {0, 0, mRenderer.getWidth(), mRenderer.getHeight()};
        GLU.gluUnProject(rx, ry, rz, mRenderer.getModelViewMatrix(), 0, mRenderer.getModelProjectionMatrix(), 0,
                viewport, 0, xyzw, 0);
        xyzw[0] /= xyzw[3];
        xyzw[1] /= xyzw[3];
        xyzw[2] /= xyzw[3];
        xyzw[3] = 1;
        return xyzw;
    }
}
