package org.andresoviedo.app.model3D.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.view.ModelSurfaceView;
import org.apache.commons.io.IOUtils;

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
	private final ModelSurfaceView main;
	/**
	 * The file from where the model should be loaded
	 */
	private File modelFile = null;
	/**
	 * Whether to draw the axis
	 */
	private boolean drawAxis = true;
	/**
	 * The 3D axis
	 */
	private ObjectV1 axis;
	/**
	 * 3D object data
	 */
	private Object3DData obj3DData;
	/**
	 * The OpenGL object
	 */
	private Object3D obj3D;
	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public SceneLoader(ModelSurfaceView main) {
		this.main = main;
	}

	public void init() {
		// Draw the axis
		if (drawAxis) {
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
					-0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, -0.05F, -0.05F, 1.05F, -0.05F,
					-0.05F, 1.05F, 0.05F, -0.05F, 1.05F }, GLES20.GL_LINES);
			axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
			objects.add(axis);
		}

		obj3DData = main.getModelActivity().getParamObject3D();
		if (main.getModelActivity().getParamUri() != null) {
			modelFile = new File(main.getModelActivity().getParamUri());
		}

		if (obj3DData == null && modelFile == null) {
			Toast.makeText(main.getContext(), "There is no file to load model from", Toast.LENGTH_LONG).show();
			return;
		}

		if (modelFile != null) {
			try {
				InputStream assetIs = new FileInputStream(modelFile);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				IOUtils.copy(assetIs, bos);
				assetIs.close();

				ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
				WavefrontLoader wfl = new WavefrontLoader("wavefront_loader");
				wfl.loadModel(bis);
				bis.close();

				obj3DData = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
						wfl.getFaceMats(), wfl.getMaterials());
			} catch (Exception ex) {
				Toast.makeText(main.getContext(), "There was a problem loading model from '" + modelFile + "'",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		try {
			if (modelFile != null) {
				obj3D = Object3DBuilder.createGLES20Object(obj3DData, modelFile.getParentFile(), null, null,
						GLES20.GL_TRIANGLES, 3);
			} else {
				obj3D = Object3DBuilder.createGLES20Object(obj3DData, null, "models/", main.getContext().getAssets(),
						GLES20.GL_TRIANGLES, 3);
			}
			objects.add(obj3D);
		} catch (IOException ex) {
			Toast.makeText(main.getContext(), "There was a problem creating 3D object", Toast.LENGTH_LONG).show();
		}
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
