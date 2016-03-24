package org.andresoviedo.app.model3D.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.view.ModelSurfaceView;

import android.opengl.GLES20;
import android.util.Log;

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
	private final ModelSurfaceView main;
	/**
	 * The 3D file to load
	 */
	private final File file;

	private ObjectV1 axis;
	private WavefrontLoader wavefrontLoader;
	private Object3D wavefrontModel;

	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public SceneLoader(ModelSurfaceView main, File file) {
		this.main = main;
		this.file = file;
	}

	public void init() {
		axis = new ObjectV1(new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
				0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left
				0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // up
				0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // down
				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // z+
				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, // z-

				0.95f, 0.05f, 0, 1, 0, 0, 0.95f, -0.05f, 0, 1, 0f, 0f, // Arrow X (>)
				-0.95f, 0.05f, 0, -1, 0, 0, -0.95f, -0.05f, 0, -1, 0f, 0f, // Arrow X (<)
				-0.05f, 0.95f, 0, 0, 1, 0, 0.05f, 0.95f, 0, 0, 1f, 0f, // Arrox Y (^)
				-0.05f, 0, 0.95f, 0, 0, 1, 0.05f, 0, 0.95f, 0, 0, 1, // Arrox z (v)

				1.05F, 0.05F, 0, 1.10F, -0.05F, 0, 1.05F, -0.05F, 0, 1.10F, 0.05F, 0, // Letter X
				-0.05F, 1.05F, 0, 0.05F, 1.10F, 0, -0.05F, 1.10F, 0, 0.0F, 1.075F, 0, // Letter Y
				-0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, -0.05F, -0.05F, 1.05F, -0.05F, -0.05F,
				1.05F, 0.05F, -0.05F, 1.05F }, GLES20.GL_LINES);
		axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });

		wavefrontLoader = new WavefrontLoader("wavefront_loader");

		try {
			wavefrontLoader.loadModelFromFileSystem(main.getContext().getAssets(), file);
			wavefrontModel = wavefrontLoader.createGLES20Object(file.getParentFile(), null, GLES20.GL_TRIANGLES, 3);
			wavefrontModel.setPosition(new float[] { 0f, 0.0f, 0.0f });
			wavefrontModel.setColor(new float[] { 0.9f, 0.0f, 0.0f, 0.5f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		objects.add(axis);
		objects.add(wavefrontModel);
	}

	public List<Object3D> getObjects() {
		return objects;
	}

	public ObjectV1 getTriangle1() {
		return null;
	}

	public ObjectV2 getSquare1() {
		return null;
	}
}
