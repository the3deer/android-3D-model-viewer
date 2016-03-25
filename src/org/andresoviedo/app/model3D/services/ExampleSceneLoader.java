package org.andresoviedo.app.model3D.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.model.ObjectV3;
import org.andresoviedo.app.model3D.model.ObjectV4;
import org.andresoviedo.app.model3D.view.ModelActivity;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class ExampleSceneLoader extends SceneLoader {

	private final Activity main;

	private ObjectV1 axis;
	private ObjectV1 triangle1;
	private ObjectV1 triangle2;

	private ObjectV2 square1;
	private ObjectV2 square2;
	private Object3D square3;

	private WavefrontLoader wfl;
	private Object3DData objData;
	private Object3D objGL;

	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public ExampleSceneLoader(ModelActivity modelActivity) {
		super(modelActivity);
		this.main = modelActivity;
	}

	private void init() {
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

		wfl = new WavefrontLoader("wavefront_loader");

		square1.setPosition(new float[] { -1.5f, -1.5f, -1.5f });
		square2.setPosition(new float[] { 1.5f, 1.5f, 1.5f });
		triangle1.setPosition(new float[] { 1.5f, 1.5f, -1.5f });
		triangle2.setPosition(new float[] { 1.5f, -1.5f, 1.5f });

		square3.setPosition(new float[] { 0f, 0.0f, -1.0f });

		try {
			InputStream bis = main.getAssets().open("models/teapot.obj");
			wfl.loadModel(bis);
			bis.close();

			Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
					wfl.getFaceMats(), wfl.getMaterials());
			data3D.setId("teapot.obj");
			data3D.setAssetsDir("models/");
			data3D = Object3DBuilder.generateArrays(main.getAssets(), data3D);
			objData = data3D;
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}
	}

	public void refresh() {
		if (objGL != null) {
			return;
		}

		init();

		try {
			objGL = Object3DBuilder.createGLES20Object(objData, GLES20.GL_TRIANGLES, 3);
			objGL.setPosition(new float[] { 0f, 0.0f, 0.0f });
			objGL.setColor(new float[] { 0.9f, 0.0f, 0.0f, 0.5f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		objects.add(axis);
		objects.add(triangle1);
		objects.add(triangle2);
		objects.add(square1);
		objects.add(square2);
		// objects.add(wavefrontModel);

		// X, Y, Z
		final float[] cubePositionData = {
				// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle,
				// if the points are counter-clockwise we are looking at the "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
				// usually represent the backside of an object and aren't visible anyways.

				// Front face
						-1.0f, 1.0f, 1.0f,				
						-1.0f, -1.0f, 1.0f,
						1.0f, 1.0f, 1.0f, 
						-1.0f, -1.0f, 1.0f, 				
						1.0f, -1.0f, 1.0f,
						1.0f, 1.0f, 1.0f,

				// Right face
						1.0f, 1.0f, 1.0f,				
						1.0f, -1.0f, 1.0f,
						1.0f, 1.0f, -1.0f,
						1.0f, -1.0f, 1.0f,				
						1.0f, -1.0f, -1.0f,
						1.0f, 1.0f, -1.0f,

				// Back face
						1.0f, 1.0f, -1.0f,				
						1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,
						1.0f, -1.0f, -1.0f,				
						-1.0f, -1.0f, -1.0f,
						-1.0f, 1.0f, -1.0f,

				// Left face
						-1.0f, 1.0f, -1.0f,				
						-1.0f, -1.0f, -1.0f,
						-1.0f, 1.0f, 1.0f, 
						-1.0f, -1.0f, -1.0f,				
						-1.0f, -1.0f, 1.0f, 
				-1.0f, 1.0f, 1.0f,

				// Top face
						-1.0f, 1.0f, -1.0f,				
						-1.0f, 1.0f, 1.0f, 
						1.0f, 1.0f, -1.0f, 
						-1.0f, 1.0f, 1.0f, 				
						1.0f, 1.0f, 1.0f, 
						1.0f, 1.0f, -1.0f,

				// Bottom face
						1.0f, -1.0f, -1.0f,				
						1.0f, -1.0f, 1.0f, 
						-1.0f, -1.0f, -1.0f,
						1.0f, -1.0f, 1.0f, 				
						-1.0f, -1.0f, 1.0f,
						-1.0f, -1.0f, -1.0f,
				};	

		// R, G, B, A
				final float[] cubeColorData =
				{				
				// Front face (red)
						1.0f, 0.0f, 0.0f, 1.0f,				
						1.0f, 0.0f, 0.0f, 1.0f,
						1.0f, 0.0f, 0.0f, 1.0f,
						1.0f, 0.0f, 0.0f, 1.0f,				
						1.0f, 0.0f, 0.0f, 1.0f,
						1.0f, 0.0f, 0.0f, 1.0f,

				// Right face (green)
						0.0f, 1.0f, 0.0f, 1.0f,				
						0.0f, 1.0f, 0.0f, 1.0f,
						0.0f, 1.0f, 0.0f, 1.0f,
						0.0f, 1.0f, 0.0f, 1.0f,				
						0.0f, 1.0f, 0.0f, 1.0f,
						0.0f, 1.0f, 0.0f, 1.0f,

				// Back face (blue)
						0.0f, 0.0f, 1.0f, 1.0f,				
						0.0f, 0.0f, 1.0f, 1.0f,
						0.0f, 0.0f, 1.0f, 1.0f,
						0.0f, 0.0f, 1.0f, 1.0f,				
						0.0f, 0.0f, 1.0f, 1.0f,
						0.0f, 0.0f, 1.0f, 1.0f,

				// Left face (yellow)
						1.0f, 1.0f, 0.0f, 1.0f,				
						1.0f, 1.0f, 0.0f, 1.0f,
						1.0f, 1.0f, 0.0f, 1.0f,
						1.0f, 1.0f, 0.0f, 1.0f,				
						1.0f, 1.0f, 0.0f, 1.0f,
						1.0f, 1.0f, 0.0f, 1.0f,

				// Top face (cyan)
						0.0f, 1.0f, 1.0f, 1.0f,				
						0.0f, 1.0f, 1.0f, 1.0f,
						0.0f, 1.0f, 1.0f, 1.0f,
						0.0f, 1.0f, 1.0f, 1.0f,				
						0.0f, 1.0f, 1.0f, 1.0f,
						0.0f, 1.0f, 1.0f, 1.0f,

				// Bottom face (magenta)
						1.0f, 0.0f, 1.0f, 1.0f,				
						1.0f, 0.0f, 1.0f, 1.0f,
						1.0f, 0.0f, 1.0f, 1.0f,
						1.0f, 0.0f, 1.0f, 1.0f,				
						1.0f, 0.0f, 1.0f, 1.0f,
						1.0f, 0.0f, 1.0f, 1.0f
				};

		// X, Y, Z
		// The normal is used in light calculations and is a vector which points
		// orthogonal to the plane of the surface. For a cube model, the normals
		// should be orthogonal to the points of each face.
				final float[] cubeNormalData =
				{												
				// Front face
						0.0f, 0.0f, 1.0f,				
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,				
						0.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f,

				// Right face
						1.0f, 0.0f, 0.0f,				
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,				
						1.0f, 0.0f, 0.0f,
						1.0f, 0.0f, 0.0f,

				// Back face
						0.0f, 0.0f, -1.0f,				
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,				
						0.0f, 0.0f, -1.0f,
						0.0f, 0.0f, -1.0f,

				// Left face
						-1.0f, 0.0f, 0.0f,				
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,				
						-1.0f, 0.0f, 0.0f,
						-1.0f, 0.0f, 0.0f,

				// Top face
						0.0f, 1.0f, 0.0f,			
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,				
						0.0f, 1.0f, 0.0f,
						0.0f, 1.0f, 0.0f,

				// Bottom face
						0.0f, -1.0f, 0.0f,			
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f,				
						0.0f, -1.0f, 0.0f,
						0.0f, -1.0f, 0.0f
				};

		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
		// What's more is that the texture coordinates are the same for every face.
				final float[] cubeTextureCoordinateData =
				{												
				// Front face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,				

				// Right face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	

				// Back face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	

				// Left face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	

				// Top face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f,	

				// Bottom face
						0.0f, 0.0f, 				
						0.0f, 1.0f,
						1.0f, 0.0f,
						0.0f, 1.0f,
						1.0f, 1.0f,
						1.0f, 0.0f
				};

		try {
			InputStream open = main.getAssets().open("models/cube.bmp");
			List<InputStream> openl = new ArrayList<InputStream>();
			openl.add(open);
			ObjectV4 obj4 = new ObjectV4(cubePositionData, cubeColorData, null, cubeNormalData,
					cubeTextureCoordinateData, GLES20.GL_TRIANGLES, 3, openl);
			objects.add(obj4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
