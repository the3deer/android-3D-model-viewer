package org.andresoviedo.android_3d_model_engine.gui;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.collision.CollisionDetection;
import org.andresoviedo.android_3d_model_engine.controller.TouchEvent;
import org.andresoviedo.android_3d_model_engine.drawer.RendererFactory;
import org.andresoviedo.android_3d_model_engine.drawer.Renderer;
import org.andresoviedo.util.event.EventListener;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.EventObject;

public class GUI extends Widget implements EventListener {

    public static final int POSITION_TOP_LEFT = 0;
    public static final int POSITION_MIDDLE = 4;
    public static final int POSITION_TOP_RIGHT = 2;

    private final float[] cameraPosition = new float[]{0, 0, 1.1f};
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private int width;
    private int height;
    private float ratio;

    public GUI(){
        super();
        addListener(this);
    }

    /**
     * @param width horizontal screen pixels
     * @param height vertical screen pixels
     */
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.ratio = (float) width / height;

        // setup view & projection
        Matrix.setLookAtM(viewMatrix, 0,
                cameraPosition[0], cameraPosition[1], cameraPosition[2],
                0, 0, 0,
                0, 1, 0);
        Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1, 1, 1, 10);

        FloatBuffer vertexBuffer = IOUtils.createFloatBuffer(5 * 3);
        vertexBuffer.put(-ratio).put(-1).put(-1);
        vertexBuffer.put(+ratio).put(-1).put(-1);
        vertexBuffer.put(+ratio).put(1).put(-1);
        vertexBuffer.put(-ratio).put(1).put(-1);
        vertexBuffer.put(-ratio).put(-1).put(-1);
        setVertexBuffer(vertexBuffer);

        setVisible(true);

    }

    // set position based on layout:
    // top-left, top-middle, top-right       =   0 1 2
    // middle-left, middle, middle-right     =   3 4 5
    // bottom-left, bottom, bottom-right     =   6 7 8
    public void addWidget(Widget widget) {
        super.addWidget(widget);
        widget.setRatio(ratio);
        Log.i("GUI","Widget added: "+widget);
    }

    public void render(RendererFactory rendererFactory, float[] lightPosInWorldSpace, float[] colorMask) {
        super.render(rendererFactory, lightPosInWorldSpace, colorMask);
        for (int i = 0; i < widgets.size(); i++) {
            renderWidget(rendererFactory, widgets.get(i), lightPosInWorldSpace, colorMask);
        }
    }

    private void renderWidget(RendererFactory rendererFactory, Widget widget, float[] lightPosInWorldSpace, float[]
            colorMask) {
        if (!widget.isVisible()) return;
        widget.onDrawFrame();
        Renderer drawer = rendererFactory.getDrawer(widget, false, false, false, true);

        GLES20.glLineWidth(2.0f);
        drawer.draw(widget, projectionMatrix, viewMatrix, -1, lightPosInWorldSpace, colorMask, cameraPosition);
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
            float[] intersection = CollisionDetection.getBoxIntersection(nearHit, direction, this.widgets.get(i).getBoundingBox());
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
            float[] intersection = CollisionDetection.getBoxIntersection(nearHit, direction, this.widgets.get(i).getBoundingBox());
            if (intersection[0] >= 0 && intersection[0] <= intersection[1]) {
                Widget widget = this.widgets.get(i);
                Log.i("GUI", "Click! " + widget.getId());
                float[] point = Math3DUtils.add(nearHit, Math3DUtils.multiply(direction, intersection[0]));
                fireEvent(new GUI.ClickEvent(this, widget, point[0], point[1], point[2]));
            }
        }
    }
}
