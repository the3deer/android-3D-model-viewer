package org.andresoviedo.app.model3D.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.view.ModelActivity;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

/**
 * This class loads a 3D scene as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class ExampleSceneLoader extends SceneLoader {

	final float[] cubePositionData = {
		//@formatter:off
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

	final float[] cubeColorData = {		
			
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
	//@formatter:on

	private final Activity main;

	private Object3D axis;
	private Object3D objV1;
	private Object3D objV2;
	private Object3D objV3;
	private Object3D objV4;
	private Object3D objFile1;
	private Object3D objFile2;
	private Object3D objFileV5;

	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public ExampleSceneLoader(ModelActivity modelActivity) {
		super(modelActivity);
		this.main = modelActivity;
	}

	public void refresh() {
		if (objFile1 != null) {
			return;
		}

		axis = Object3DBuilder.build(new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
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

		objV1 = Object3DBuilder.build(cubePositionData, GLES20.GL_TRIANGLES);
		objV1.setColor(new float[] { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f });
		objV1.setPosition(new float[] { -1.5f, 1.5f, 1.5f });

		objV2 = Object3DBuilder.build(new float[] { -0.5f, 0.5f, 0.0f, // top left
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f, // bottom right
				0.5f, 0.5f, 0.0f }, new short[] { 0, 1, 2, 0, 2, 3 }, GLES20.GL_TRIANGLES);
		objV2.setColor(new float[] { 0.2f, 0.709803922f, 0.898039216f, 1.0f });
		objV2.setPosition(new float[] { 1.5f, 1.5f, 1.5f });

		InputStream open = null;
		try {
			open = main.getAssets().open("models/penguin.bmp");
			objV3 = Object3DBuilder.build(cubePositionData, cubeTextureCoordinateData, GLES20.GL_TRIANGLES, 3, open);
		} catch (Exception ex) {
			Log.e("example", ex.getMessage(), ex);
		} finally {
			if (open != null) {
				try {
					open.close();
				} catch (IOException ex) {
				}
			}
		}
		objV3.setColor(new float[] { 1f, 1f, 1f, 1f });
		objV3.setPosition(new float[] { -1.5f, -1.5f, 1.5f });

		try {
			open = main.getAssets().open("models/cube.bmp");
			objV4 = Object3DBuilder.build(cubePositionData, cubeColorData, cubeTextureCoordinateData,
					GLES20.GL_TRIANGLES, 3, open);
		} catch (Exception ex) {
			Log.e("example", ex.getMessage(), ex);
		} finally {
			if (open != null) {
				try {
					open.close();
				} catch (IOException ex) {
				}
			}
		}
		objV4.setPosition(new float[] { 1.5f, -1.5f, 1.5f });

		try {
			objFile1 = Object3DBuilder.build(main.getAssets(), "models/", "teapot.obj");
			objFile1.setPosition(new float[] { -1.5f, 0f, 0f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		try {
			objFile2 = Object3DBuilder.build(main.getAssets(), "models/", "cube.obj");
			objFile2.setPosition(new float[] { 0f, 0f, 0f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}
		
		try {
			objFileV5 = Object3DBuilder.build(main.getAssets(), "models/", "ToyPlane.obj");
			objFileV5.setPosition(new float[] { 1f, 0f, 0f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		objects.add(axis);
		objects.add(objV1);
		objects.add(objV2);
		objects.add(objV3);
		objects.add(objV4);
		objects.add(objFile1);
		objects.add(objFile2);
		objects.add(objFileV5);
	}

	public List<Object3D> getObjects() {
		return objects;
	}

	public ObjectV1 getTriangle1() {
		return (ObjectV1) objV1;
	}

	public ObjectV2 getSquare1() {
		return (ObjectV2) objV2;
	}
}
