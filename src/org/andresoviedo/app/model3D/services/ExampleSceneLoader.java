package org.andresoviedo.app.model3D.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.model.ObjectV3;
import org.andresoviedo.app.model3D.view.ModelSurfaceView;

import android.opengl.GLES20;
import android.util.Log;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class ExampleSceneLoader {

	private final ModelSurfaceView main;

	private ObjectV1 axis;
	private ObjectV1 triangle1;
	private ObjectV1 triangle2;

	private ObjectV2 square1;
	private ObjectV2 square2;
	private Object3D square3;
	private WavefrontLoader wavefrontLoader;
	private Object3D wavefrontModel;
	private Object3D bicho;

	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public ExampleSceneLoader(ModelSurfaceView main) {
		this.main = main;
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

		triangle1 = new ObjectV1(new float[] {
				// in counterclockwise order:
				0.0f, 0.5f, 0.0f, // top
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f // bottom right
		}, GLES20.GL_TRIANGLES);
		triangle1.setColor(new float[] { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f });

		triangle2 = new ObjectV1(new float[] {
				// in counterclockwise order:
				0.0f, 0.5f, 0.0f, // top
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f // bottom right
		}, GLES20.GL_TRIANGLES);
		triangle2.setColor(new float[] { 0f, 0.76953125f, 0.22265625f, 0.0f });

		square1 = new ObjectV2(new float[] { -0.5f, 0.5f, 0.0f, // top left
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f, // bottom right
				0.5f, 0.5f, 0.0f }, new short[] { 0, 1, 2, 0, 2, 3 }, GLES20.GL_TRIANGLES);
		square1.setColor(new float[] { 0.2f, 0.709803922f, 0.898039216f, 1.0f });
		square1.setColor(new float[] { 0.2f, 0.709803922f, 0.898039216f, 1.0f });

		square2 = new ObjectV2(new float[] { -0.5f, 0.5f, 0.0f, // top left
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f, // bottom right
				0.5f, 0.5f, 0.0f }, new short[] { 0, 1, 2, 0, 2, 3 }, GLES20.GL_TRIANGLES);

		square3 = new ObjectV3(
				new float[] {
				//@formatter:off
				-0.5f, 0.5f, 0.0f, // top left
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f, // bottom right
				0.5f, 0.5f, 0.0f, /* up right */
				-0.5f, 0.5f, 0.25f, /*  */
				0.5f, 0.5f, 0.25f }, 
				// @formatter:on
				new short[] { 0, 1, 2, 0, 2, 3, 0, 4, 3, 5, 4, 3 },
				new float[] { 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0 }, null, GLES20.GL_TRIANGLE_STRIP,
				-1, null);

		square3.setColor(new float[] { 0f, 0.76953125f, 0.22265625f, 0.5f });

		try {
			InputStream open = main.getContext().getAssets().open("models/penguin.bmp");
			bicho = new ObjectV3(
					new float[] {
						//@formatter:off
						-0.5f, 0.5f, 0.0f, // top left
						-0.5f, -0.5f, 0.0f, // bottom left
						0.5f, -0.5f, 0.0f, // bottom right
						0.5f, 0.5f, 0.0f, /* up right */}, 
						// @formatter:on
					new short[] { 0, 1, 2, 0, 2, 3, }, new float[] { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 },
					new float[] { 0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f }, GLES20.GL_TRIANGLE_STRIP, -1, open);

			open.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bicho.setColor(new float[] { 0f, 0.0f, 1f, 0.5f });

		wavefrontLoader = new WavefrontLoader("wavefront_loader");

		square1.setPosition(new float[] { -1.5f, -1.5f, -1.5f });
		square2.setPosition(new float[] { 1.5f, 1.5f, 1.5f });
		triangle1.setPosition(new float[] { 1.5f, 1.5f, -1.5f });
		triangle2.setPosition(new float[] { 1.5f, -1.5f, 1.5f });

		square3.setPosition(new float[] { 0f, 0.0f, -1.0f });

		try {
			wavefrontLoader.loadModelFromClasspath(main.getContext().getAssets(), "models/teapot.obj");
			wavefrontModel = wavefrontLoader.createGLES20Object(main.getContext().getAssets(), GLES20.GL_TRIANGLE_STRIP,
					3);
			wavefrontModel.setPosition(new float[] { 0f, 0.0f, 0.0f });
			wavefrontModel.setColor(new float[] { 0.9f, 0.0f, 0.0f, 0.5f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		bicho.setPosition(new float[] { -0.5f, 0.0f, 0.0f });
		bicho.setRotation(new float[] { 0f, 1.0f, 0.0f });

		objects.add(axis);
		objects.add(triangle1);
		objects.add(triangle2);
		objects.add(square1);
		objects.add(square2);
		objects.add(bicho);
		objects.add(wavefrontModel);
	}

	public List<Object3D> getObjects() {
		return objects;
	}

	public ObjectV1 getTriangle1() {
		return triangle1;
	}

	public ObjectV2 getSquare1() {
		return square1;
	}
}
