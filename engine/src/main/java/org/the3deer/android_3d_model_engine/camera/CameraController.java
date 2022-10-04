package org.the3deer.android_3d_model_engine.camera;


import org.the3deer.android_3d_model_engine.controller.TouchEvent;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.android_3d_model_engine.model.Projection;
import org.the3deer.android_3d_model_engine.view.ViewEvent;
import org.the3deer.util.event.EventListener;

import java.util.EventObject;

public final class CameraController implements EventListener {

    private final Camera handlerDefault ;
    private final Camera handlerIsometric;
    private final Camera handlerOrtho;
    private final Camera handlerPOV;

    private final Camera camera;

    private Camera handler;
    private int width;
    private int height;

    public CameraController(Camera camera) {
        this.camera = camera;
        this.handlerDefault = new DefaultCamera(camera);
        this.handlerIsometric = new IsometricCamera(camera);
        this.handlerOrtho = new OrthographicCamera(camera);
        this.handlerPOV = new PointOfViewCamera(camera);
        this.handler = handlerDefault;
        this.handler.enable();
    }

    private void updateHandler(Projection projection) {
        switch (projection) {
            case PERSPECTIVE:
                this.handler = handlerDefault;
                break;
            case ISOMETRIC:
                this.handler = handlerIsometric;
                break;
            case ORTHOGRAPHIC:
                this.handler = handlerOrtho;
                break;
            case FREE:
                this.handler = handlerPOV;
                break;
            default:
                throw new UnsupportedOperationException("Unsupported projection: "+projection);
        }
        this.camera.setDelegate(this.handler);
        this.handler.enable();
    }

    @Override
    public boolean onEvent(EventObject event) {
        if (event instanceof ViewEvent) {
            final ViewEvent viewEvent = (ViewEvent) event;
            switch (viewEvent.getCode()) {
                case SURFACE_CREATED:
                case SURFACE_CHANGED:
                    this.width = ((ViewEvent) event).getWidth();
                    this.height = ((ViewEvent) event).getHeight();
                    break;
                case PROJECTION_CHANGED:
                    camera.setProjection(viewEvent.getProjection());
                    updateHandler(viewEvent.getProjection());
                    break;
            }
        } else if (event instanceof TouchEvent) {
            TouchEvent touchEvent = (TouchEvent) event;
            switch (touchEvent.getAction()) {
                case CLICK:
                    break;
                case MOVE:
                    float dx1 = touchEvent.getdX();
                    float dy1 = touchEvent.getdY();
                    float max = Math.max(width, height);
                    dx1 = (float) (dx1 / max * Math.PI * 2);
                    dy1 = (float) (dy1 / max * Math.PI * 2);
                    handler.translateCamera(dx1, dy1);
                    break;
                case PINCH:
                    final float zoomFactor = ((TouchEvent) event).getZoom();
                    handler.MoveCameraZ((float) (-zoomFactor * Constants.near * Math.log(camera.getDistance())));
                    break;
                case ROTATE:
                    float rotation = touchEvent.getAngle();
                    handler.Rotate(rotation);
                    break;
                case SPREAD:
                    // TODO:
            }
        }
        return true;
    }
}
