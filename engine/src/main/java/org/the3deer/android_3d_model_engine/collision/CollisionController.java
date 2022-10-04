package org.the3deer.android_3d_model_engine.collision;

import android.util.Log;

import org.the3deer.android_3d_model_engine.controller.TouchEvent;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.objects.Point;
import org.the3deer.android_3d_model_engine.services.SceneLoader;
import org.the3deer.android_3d_model_engine.view.ModelSurfaceView;
import org.the3deer.util.android.AndroidUtils;
import org.the3deer.util.event.EventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;

/**
 * Collision controller that, based on View settings (width, height and projection matrices)
 * it can detect a collision between an Object and a Ray casted from the screen to the farthest point
 *
 * Collision controller processes {@link TouchEvent} and fires {@link CollisionEvent}
 */
public class CollisionController implements EventListener {

    private final ModelSurfaceView view;
    private final SceneLoader scene;
    private final List<Object3DData> objects;

    private final List<EventListener> listeners = new ArrayList<>();

    public CollisionController(ModelSurfaceView view, SceneLoader scene) {
        this.view = view;
        this.scene = scene;
        this.objects = scene.getObjects();
    }

    public List<Object3DData> getObjects() {
        return objects;
    }

    public void addListener(EventListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public boolean onEvent(EventObject event) {
        Log.v("CollisionController", "Processing event... " + event.toString());
        if (event instanceof TouchEvent) {
            TouchEvent touchEvent = (TouchEvent) event;
            if (touchEvent.getAction() == TouchEvent.CLICK) {
                if (objects.isEmpty()) return true;
                Log.v("CollisionController", objects.get(0).getCurrentDimensions().toString());
                final float x = touchEvent.getX();
                final float y = touchEvent.getY();
                Log.v("CollisionController", "Testing for collision... (" + objects.size() + ") " + x + "," + y);
                Object3DData objectHit = CollisionDetection.getBoxIntersection(
                        objects, view.getWidth(), view.getHeight(),
                        view.getViewMatrix(), view.getProjectionMatrix(), x, y);
                if (objectHit != null) {

                    // intersection point
                    Object3DData point3D = null;

                    if (this.scene.isCollision()) {

                        Log.i("CollisionController", "Collision. Getting triangle intersection... " + objectHit.getId());
                        float[] point = CollisionDetection.getTriangleIntersection(objectHit, view.getWidth(), view.getHeight
                                (), view.getViewMatrix(), view.getProjectionMatrix(), x, y);

                        if (point != null) {
                            Log.i("CollisionController", "Building intersection point: " + Arrays.toString(point));
                            point3D = Point.build(point).setColor(new float[]{1.0f, 0f, 0f, 1f});
                        }
                    }

                    final CollisionEvent collisionEvent = new CollisionEvent(this, objectHit, x, y, point3D);
                    AndroidUtils.fireEvent(listeners, collisionEvent);
                    return true;
                }
            }
        }
        return false;
    }

}