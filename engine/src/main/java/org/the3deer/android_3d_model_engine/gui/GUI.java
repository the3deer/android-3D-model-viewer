package org.the3deer.android_3d_model_engine.gui;

import android.util.Log;

import org.the3deer.android_3d_model_engine.collision.CollisionDetection;
import org.the3deer.android_3d_model_engine.controller.TouchEvent;
import org.the3deer.util.event.EventListener;
import org.the3deer.util.math.Math3DUtils;

import java.util.Arrays;
import java.util.EventObject;

public class GUI extends Widget implements EventListener {

    public GUI(){
        super();
        addListener(this);
    }

    @Override
    public boolean onEvent(EventObject event) {
        super.onEvent(event);
        if (event instanceof TouchEvent) {
            TouchEvent touchEvent = (TouchEvent) event;
            TouchEvent.Action action = touchEvent.getAction();
            if (action == TouchEvent.Action.CLICK) {
                processClick(touchEvent.getX(), touchEvent.getY());
            } else if (action == TouchEvent.Action.MOVE){
                processMove(touchEvent);
            }
        }
        return true;
    }

    private void processMove(TouchEvent touchEvent) {
        float x = touchEvent.getX();
        float y = touchEvent.getY();
        Log.v("GUI", "Processing move... " + x + "," + y);
        float[] nearHit = CollisionDetection.unProject(width, height, viewMatrix, projectionMatrix, x, y, 0);
        float[] farHit = CollisionDetection.unProject(width, height, viewMatrix, projectionMatrix, x, y, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);

        Log.v("GUI", "near: " + Arrays.toString(nearHit) + ", far: " + Arrays.toString(farHit));

        for (int i = 0; i < this.widgets.size(); i++) {
            if (!this.widgets.get(i).isVisible() || !this.widgets.get(i).isSolid()) continue;
            float[] intersection = CollisionDetection.getBoxIntersection(nearHit, direction, this.widgets.get(i).getCurrentBoundingBox());
            if (intersection[0] >= 0 && intersection[0] <= intersection[1]) {
                Widget widget = this.widgets.get(i);
                Log.i("GUI", "Click! " + widget.getId());
                float[] point = Math3DUtils.add(nearHit, Math3DUtils.multiply(direction, intersection[0]));

                //float dx = touchEvent.getdX() / touchEvent.getWidth();
                //float dy = touchEvent.getdY() / touchEvent.getHeight();

                // calculate point 2
                float[] nearHit2 = CollisionDetection.unProject(width, height, viewMatrix, projectionMatrix,
                        touchEvent.getX2(), touchEvent.getY2(), 0);
                float[] point2 = Math3DUtils.add(nearHit2, Math3DUtils.multiply(direction, intersection[0]));

                float dx = point2[0]-point[0];
                float dy = point2[1]-point[1];

                fireEvent(new GUI.MoveEvent(this, widget, point[0], point[1], point[2], dx, dy));
            }
        }
    }

    private void processClick(float x, float y) {
        Log.v("GUI", "Processing click... " + x + "," + y);
        float[] nearHit = CollisionDetection.unProject(width, height, viewMatrix, projectionMatrix, x, y, 0);
        float[] farHit = CollisionDetection.unProject(width, height, viewMatrix, projectionMatrix, x, y, 1);
        float[] direction = Math3DUtils.substract(farHit, nearHit);
        Math3DUtils.normalize(direction);

        Log.v("GUI", "near: " + Arrays.toString(nearHit) + ", far: " + Arrays.toString(farHit));

        for (int i = 0; i < this.widgets.size(); i++) {
            if (!this.widgets.get(i).isVisible() || !this.widgets.get(i).isSolid()) continue;
            float[] intersection = CollisionDetection.getBoxIntersection(nearHit, direction, this.widgets.get(i).getCurrentBoundingBox());
            if (intersection[0] >= 0 && intersection[0] <= intersection[1]) {
                Widget widget = this.widgets.get(i);
                Log.i("GUI", "Click! " + widget.getId());
                float[] point = Math3DUtils.add(nearHit, Math3DUtils.multiply(direction, intersection[0]));
                fireEvent(new GUI.ClickEvent(this, widget, point[0], point[1], point[2]));
            }
        }
    }
}
