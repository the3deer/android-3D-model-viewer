package org.andresoviedo.app.model3D.services;

import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DBuilder.Callback;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.view.ModelActivity;

import android.opengl.GLES20;
import android.widget.Toast;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class SceneLoader {

	/**
	 * Parent component
	 */
	protected final ModelActivity parent;
	/**
	 * The 3D axis
	 */
	private Object3DData axis;
	/**
	 * List of data objects containing info for building the opengl objects
	 */
	private List<Object3DData> objects = new ArrayList<Object3DData>();
	/**
	 * Whether to draw objects as wireframes
	 */
	private boolean drawWireframe = false;
	/**
	 * Whether to draw bounding boxes around objects
	 */
	private boolean drawBoundingBox = false;
	/**
	 * Whether to draw face normals. Normally used to debug models
	 */
	private boolean drawNormals = false;
	/**
	 * Default draw mode when loading models from files
	 */
	private int defaultDrawMode = GLES20.GL_TRIANGLE_FAN;

	public SceneLoader(ModelActivity main) {
		this.parent = main;
	}

	public void init() {
		// Draw Axis
		axis = Object3DBuilder.buildAxis();
		axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
		addObject(axis);

		// Load object
		if (parent.getParamFile() != null || parent.getParamAssetDir() != null) {
			Object3DBuilder.loadV5Async(parent, parent.getParamFile(), parent.getParamAssetDir(),
					parent.getParamAssetFilename(), new Callback() {

						@Override
						public void onLoadComplete(Object3DData data) {
							data.setDrawMode(defaultDrawMode);
							data.centerAndScale(5.0f);
							addObject(data);

						}

						@Override
						public void onLoadError(Exception ex) {
							Toast.makeText(parent.getApplicationContext(),
									"There was a problem building the model: " + ex.getMessage(), Toast.LENGTH_LONG)
									.show();
						}
					});
		}
	}

	protected void addObject(Object3DData obj) {
		synchronized (objects) {
			objects.add(obj);
		}
		requestRender();
	}

	private void requestRender() {
		parent.getgLView().requestRender();
	}

	public List<Object3DData> getObjects() {
		synchronized (objects) {
			return new ArrayList<Object3DData>(objects);
		}
	}

	public void toggleWireframe() {
		this.drawWireframe = !this.drawWireframe;
		requestRender();
	}

	public boolean isDrawWireframe() {
		return this.drawWireframe;
	}

	public void toggleBoundingBox() {
		this.drawBoundingBox = !drawBoundingBox;
		requestRender();
	}

	public boolean isDrawBoundingBox() {
		return drawBoundingBox;
	}

	public boolean isDrawNormals() {
		return drawNormals;
	}

}
