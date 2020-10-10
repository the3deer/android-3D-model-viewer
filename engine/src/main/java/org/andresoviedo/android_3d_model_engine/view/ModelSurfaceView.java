package org.andresoviedo.android_3d_model_engine.view;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.andresoviedo.android_3d_model_engine.controller.TouchController;
import org.andresoviedo.android_3d_model_engine.services.SceneLoader;
import org.andresoviedo.util.android.AndroidUtils;
import org.andresoviedo.util.event.EventListener;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * This is the actual opengl view. From here we can detect touch gestures for example
 * 
 * @author andresoviedo
 *
 */
public class ModelSurfaceView extends GLSurfaceView implements EventListener {

	private final ModelRenderer mRenderer;

	private TouchController touchController;

	private final List<EventListener> listeners = new ArrayList<>();

	public ModelSurfaceView(Activity parent, float[] backgroundColor, SceneLoader scene){
		super(parent);
		try{
			Log.i("ModelSurfaceView","Loading [OpenGL 2] ModelSurfaceView...");

			// Create an OpenGL ES 2.0 context.
			setEGLContextClientVersion(2);

			// This is the actual renderer of the 3D space
			mRenderer = new ModelRenderer(parent, this, backgroundColor, scene);
			mRenderer.addListener(this);
			setRenderer(mRenderer);
		}catch (Exception e){
			Log.e("ModelActivity",e.getMessage(),e);
			Toast.makeText(parent, "Error loading shaders:\n" +e.getMessage(), Toast.LENGTH_LONG).show();
			throw new RuntimeException(e);
		}
	}

	public void setTouchController(TouchController touchController){
		this.touchController = touchController;
	}

	public void addListener(EventListener listener){
		listeners.add(listener);
	}

	public float[] getProjectionMatrix() {
		return mRenderer.getProjectionMatrix();
	}

	public float[] getViewMatrix() {
		return mRenderer.getViewMatrix();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		try {
			return touchController.onTouchEvent(event);
		} catch (Exception ex) {
			Log.e("ModelSurfaceView","Exception: "+ ex.getMessage(),ex);
		}
		return false;
	}

	public ModelRenderer getModelRenderer() {
		return mRenderer;
	}

	private void fireEvent(EventObject event) {
		AndroidUtils.fireEvent(listeners,event);
	}

	@Override
	public boolean onEvent(EventObject event) {
		fireEvent(event);
		return true;
	}

	public void toggleLights() {
		Log.i("ModelSurfaceView","Toggling lights...");
		mRenderer.toggleLights();
	}

	public void toggleSkyBox() {
		Log.i("ModelSurfaceView","Toggling sky box...");
		mRenderer.toggleSkyBox();
	}

    public void toggleWireframe() {
		Log.i("ModelSurfaceView","Toggling wireframe...");
        mRenderer.toggleWireframe();
    }

	public void toggleTextures() {
		Log.i("ModelSurfaceView","Toggling textures...");
		mRenderer.toggleTextures();
	}

	public void toggleColors() {
		Log.i("ModelSurfaceView","Toggling colors...");
		mRenderer.toggleColors();
	}

	public void toggleAnimation() {
		Log.i("ModelSurfaceView","Toggling animation...");
		mRenderer.toggleAnimation();
	}

	public boolean isLightsEnabled() {
		return mRenderer.isLightsEnabled();
	}

}
