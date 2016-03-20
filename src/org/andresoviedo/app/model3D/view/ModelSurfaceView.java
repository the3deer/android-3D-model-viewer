package org.andresoviedo.app.model3D.view;

import org.andresoviedo.app.model3D.controller.TouchController;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * This is the actual opengl view. From here we can detect touch gestures for example
 * 
 * @author andresoviedo
 *
 */
public class ModelSurfaceView extends GLSurfaceView {

	private ModelRenderer mRenderer;
	private TouchController touchHandler;

	public ModelSurfaceView(Context context) {
		super(context);

		// Create an OpenGL ES 2.0 context.
		setEGLContextClientVersion(2);

		// This is the actual renderer of the 3D space
		mRenderer = new ModelRenderer(this);
		setRenderer(mRenderer);

		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		touchHandler = new TouchController(this, mRenderer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return touchHandler.onTouchEvent(event);
	}

}