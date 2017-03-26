package org.andresoviedo.app.model3D.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.services.WavefrontLoader;
import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Material;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;
import org.andresoviedo.app.util.math.Math3DUtils;
import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.util.Log;

public final class Object3DBuilder {

	public static interface Callback {
		public void onLoadError(Exception ex);

		public void onLoadComplete(Object3DData data);
	}

	private static final int COORDS_PER_VERTEX = 3;
	/**
	 * Default vertices colors
	 */
	private static float[] DEFAULT_COLOR = { 1.0f, 1.0f, 0, 1.0f };

	final static float[] axisVertexLinesData = new float[] {
		//@formatter:off
		0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
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
		1.05F, 0.05F, -0.05F, 1.05F // letter z
		//@formatter:on
	};

	final static float[] squarePositionData = new float[] {
			// @formatter:off
			-0.5f, 0.5f, 0.5f, // top left front
			-0.5f, -0.5f, 0.5f, // bottom left front
			0.5f, -0.5f, 0.5f, // bottom right front
			0.5f, 0.5f, 0.5f, // upper right front
			-0.5f, 0.5f, -0.5f, // top left back
			-0.5f, -0.5f, -0.5f, // bottom left back
			0.5f, -0.5f, -0.5f, // bottom right back
			0.5f, 0.5f, -0.5f // upper right back
			// @formatter:on
	};

	final static short[] squareDrawOrderData = new short[] {
			// @formatter:off
			// front
			0, 1, 2, 
			0, 2, 3,
			// back
			7, 6, 5,
			4, 7, 5,
			// up
			4, 0, 3,
			7, 4, 3,
			// bottom
			1, 5, 6,
			2, 1, 6,
			// left
			4, 5, 1,
			0, 4, 1,
			// right
			3, 2, 6,
			7, 3, 6
			// @formatter:on
	};

	final static float[] cubePositionData = {
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
			-1.0f, -1.0f, -1.0f
			};	

		final static float[] cubeColorData = {		
				
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

		final static float[] cubeNormalData =
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


		final static float[] cubeTextureCoordinateData =
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

	final static short[] cubeDrawOrder = new short[] {};

	private Object3DV0 object3dv0;
	private Object3DV1 object3dv1;
	private Object3DV2 object3dv2;
	private Object3DV3 object3dv3;
	private Object3DV4 object3dv4;
	private Object3DV5 object3dv5;
	private Object3DV6 object3dv6;

	public static Object3DData buildPoint(float[] point) {
		return new Object3DData(createNativeByteBuffer(point.length * 4).asFloatBuffer().put(point))
				.setDrawMode(GLES20.GL_POINTS);
	}

	public static Object3DData buildAxis() {
		return new Object3DData(
				createNativeByteBuffer(axisVertexLinesData.length * 4).asFloatBuffer().put(axisVertexLinesData))
						.setDrawMode(GLES20.GL_LINES);
	}

	public static Object3DData buildCubeV1() {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData))
						.setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV1").centerAndScale(1.0f);
	}

	public static Object3DData buildCubeV1_with_normals() {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData))
						.setVertexColorsArrayBuffer(
								createNativeByteBuffer(cubeColorData.length * 4).asFloatBuffer().put(cubeColorData))
						.setVertexNormalsArrayBuffer(
								createNativeByteBuffer(cubeNormalData.length * 4).asFloatBuffer().put(cubeNormalData))
						.setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV1_light").centerAndScale(1.0f);
	}

	public static Object3DData buildSquareV2() {
		return new Object3DData(
				createNativeByteBuffer(squarePositionData.length * 4).asFloatBuffer().put(squarePositionData),
				createNativeByteBuffer(squareDrawOrderData.length * 2).asShortBuffer().put(squareDrawOrderData)
						.asReadOnlyBuffer()).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV2").centerAndScale(1.0f);
	}

	public static Object3DData buildCubeV3(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV3").centerAndScale(1.0f);
	}

	public static Object3DData buildCubeV4(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData),
				createNativeByteBuffer(cubeColorData.length * 4).asFloatBuffer().put(cubeColorData).asReadOnlyBuffer(),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV4").centerAndScale(1.0f);
	}

	public static Object3DData loadV5(AssetManager assets, String assetDir, String assetFilename) {
		try {
			InputStream is = assets.open(assetDir + assetFilename);
			WavefrontLoader wfl = new WavefrontLoader(assetFilename);
			wfl.loadModel(is);
			is.close();

			Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
					wfl.getFaceMats(), wfl.getMaterials());
			data3D.setId(assetFilename);
			data3D.setAssetsDir(assetDir);
			// data3D.setDrawMode(GLES20.GL_TRIANGLES);
			generateArrays(assets, data3D);
			if (data3D.getVertexColorsArrayBuffer() != null) {
				data3D.setVersion(5);
			} else {
				data3D.setVersion(3);
			}
			return data3D;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public Object3D getDrawer(Object3DData obj, boolean usingTextures, boolean usingLights) throws IOException {

		if (object3dv1 == null) {
			object3dv1 = new Object3DV1();
		}
		if (object3dv2 == null) {
			object3dv2 = new Object3DV2();
		}
		if (object3dv3 == null) {
			object3dv3 = new Object3DV3();
		}
		if (object3dv4 == null) {
			object3dv4 = new Object3DV4();
		}
		if (object3dv5 == null) {
			object3dv5 = new Object3DV5();
		}
		if (object3dv6 == null) {
			object3dv6 = new Object3DV6();
		}

		if (usingTextures && usingLights && obj.getVertexColorsArrayBuffer() != null && obj.getTextureData() != null
				&& obj.getTextureCoordsArrayBuffer() != null && obj.getVertexNormalsArrayBuffer() != null
				&& obj.getVertexNormalsArrayBuffer() != null) {
			return object3dv6;
		} else if (usingLights && obj.getVertexColorsArrayBuffer() != null
				&& obj.getVertexNormalsArrayBuffer() != null) {
			return object3dv5;
		} else if (usingTextures && obj.getVertexColorsArrayBuffer() != null && obj.getTextureData() != null
				&& obj.getTextureCoordsArrayBuffer() != null) {
			return object3dv4;
		} else if (usingTextures && obj.getVertexColorsArrayBuffer() == null && obj.getTextureData() != null
				&& obj.getTextureCoordsArrayBuffer() != null) {
			return object3dv3;
		} else if (obj.getVertexColorsArrayBuffer() != null) {
			return object3dv2;
		} else {
			return object3dv1;
		}
	}

	public static Object3DData generateArrays(AssetManager assets, Object3DData obj) throws IOException {
		int drawMode = GLES20.GL_TRIANGLES;
		int drawSize = 0;

		ArrayList<Tuple3> verts = obj.getVerts();
		ArrayList<Tuple3> vertexNormals = obj.getNormals();
		ArrayList<Tuple3> texCoords = obj.getTexCoords();
		Faces faces = obj.getFaces(); // model faces
		FaceMaterials faceMats = obj.getFaceMats();
		Materials materials = obj.getMaterials();

		FloatBuffer vertexBuffer = createNativeByteBuffer(3 * verts.size() * 4).asFloatBuffer();
		for (Tuple3 vert : verts) {
			vertexBuffer.put(vert.getX());
			vertexBuffer.put(vert.getY());
			vertexBuffer.put(vert.getZ());
		}

		// TODO: generate face normals
		FloatBuffer vertexArrayBuffer = null;
		ShortBuffer drawOrderBuffer = null;
		if (obj.isDrawUsingArrays()) {
			Log.i("Object3DBuilder", "Generating vertex array buffer...");
			vertexArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			for (int[] face : faces.facesVertIdxs) {
				for (int i = 0; i < face.length; i++) {
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 1));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 2));
				}
			}
		}else{
			Log.i("Object3DBuilder", "Generating draw order buffer...");
			// this only works for faces made of a single triangle
			drawOrderBuffer = createNativeByteBuffer(faces.getVerticesReferencesCount()*2).asShortBuffer();
			for (int[] face : faces.facesVertIdxs) {
				for (int i = 0; i < face.length; i++) {
					drawOrderBuffer.put((short)face[i]);
				}
			}
		}

		boolean onlyTriangles = true;
		List<int[]> drawModeList = new ArrayList<int[]>();
		int currentVertexPos = 0;
		for (int[] face : faces.facesVertIdxs) {
			if (face.length == 3) {
				drawModeList.add(new int[] { GLES20.GL_TRIANGLES, currentVertexPos, face.length });
			} else {
				onlyTriangles = false;
				drawModeList.add(new int[] { GLES20.GL_TRIANGLE_FAN, currentVertexPos, face.length });
			}
			currentVertexPos += face.length;
		}

		if (onlyTriangles) {
			// TODO: test this with all models
			drawMode = GLES20.GL_TRIANGLES;
			drawSize = 0;
			drawModeList = null;
		}

		FloatBuffer vertexNormalsBuffer = createNativeByteBuffer(3 * vertexNormals.size() * 4).asFloatBuffer();
		for (Tuple3 norm : vertexNormals) {
			vertexNormalsBuffer.put(norm.getX());
			vertexNormalsBuffer.put(norm.getY());
			vertexNormalsBuffer.put(norm.getZ());
		}

		FloatBuffer vertexNormalsArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		for (int[] normal : faces.facesNormIdxs) {
			for (int i = 0; i < normal.length; i++) {
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3));
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3 + 1));
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3 + 2));
			}
		}

		FloatBuffer textureCoordsBuffer = createNativeByteBuffer(2 * texCoords.size() * 4).asFloatBuffer();
		for (Tuple3 texCor : texCoords) {
			textureCoordsBuffer.put(texCor.getX());
			textureCoordsBuffer.put(obj.isFlipTextCoords() ? 1 - texCor.getY() : texCor.getY());
		}

		FloatBuffer textureCoordsArraysBuffer = createNativeByteBuffer(2 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		try {
			for (int[] text : faces.facesTexIdxs) {
				for (int i = 0; i < text.length; i++) {
					textureCoordsArraysBuffer.put(textureCoordsBuffer.get(text[i] * 2));
					textureCoordsArraysBuffer.put(textureCoordsBuffer.get(text[i] * 2 + 1));
				}
			}
		} catch (Exception ex) {
			Log.e("WavefrontLoader", "Failure to load texture coordinates");
		}

		if (materials != null) {
			materials.readMaterials(obj.getCurrentDir(), obj.getAssetsDir(), assets);
		}

		FloatBuffer colorArrayBuffer = createNativeByteBuffer(4 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		float[] currentColor = DEFAULT_COLOR;
		for (int i = 0; i < faces.facesVertIdxs.size(); i++) {
			if (!faceMats.isEmpty() && faceMats.findMaterial(i) != null) {
				Material mat = materials.getMaterial(faceMats.findMaterial(i));
				if (mat != null) {
					currentColor = mat.getKdColor();
				}
			}
			int[] face = faces.facesVertIdxs.get(i);
			for (int j = 0; j < face.length; j++) {
				colorArrayBuffer.put(currentColor);
			}
		}

		// materials = null;

		byte[] textureData = null;
		if (materials != null && !materials.materials.isEmpty()) {
			// FileInputStream is = new FileInputStream(fileName);
			String texture = null;
			for (Material mat : materials.materials.values()) {
				if (mat.getTexture() != null) {
					texture = mat.getTexture();
					break;
				}
			}
			if (texture != null) {
				if (obj.getCurrentDir() != null) {
					File file = new File(obj.getCurrentDir(), texture);
					Log.i("materials", "Loading texture '" + file + "'...");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					FileInputStream fis = new FileInputStream(file);
					IOUtils.copy(fis, bos);
					fis.close();
					textureData = bos.toByteArray();
					bos.close();
				} else {
					Log.i("materials", "Loading texture '" + obj.getAssetsDir() + texture + "'...");
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					InputStream fis = assets.open(obj.getAssetsDir() + texture);
					IOUtils.copy(fis, bos);
					fis.close();
					textureData = bos.toByteArray();
					bos.close();
				}
			} else {
				Log.i("Loader", "Found material(s) but no texture");
			}
		}

		obj.setVertexBuffer(vertexBuffer);
		obj.setDrawOrder(drawOrderBuffer);
		obj.setDrawSize(drawSize);
		obj.setVertexNormalsBuffer(vertexNormalsBuffer);
		obj.setTextureCoordsBuffer(textureCoordsBuffer);

		obj.setVertexArrayBuffer(vertexArrayBuffer);
		obj.setVertexNormalsArrayBuffer(vertexNormalsArrayBuffer);
		obj.setTextureCoordsArrayBuffer(textureCoordsArraysBuffer);

		obj.setDrawModeList(drawModeList);
		obj.setDrawMode(drawMode);
		obj.setVertexColorsArrayBuffer(colorArrayBuffer);
		obj.setTextureData(textureData);

		return obj;
	}

	public Object3D getBoundingBoxDrawer() {
		return object3dv2;
	}

	public Object3D getFaceNormalsDrawer() {
		return object3dv1;
	}

	public Object3D getPointDrawer() {
		if (object3dv0 == null) {
			object3dv0 = new Object3DV0();
		}
		return object3dv0;
	}

	public static Object3DData buildBoundingBox(Object3DData obj) {
		BoundingBox boundingBox = new BoundingBox(
				obj.getVertexArrayBuffer() != null ? obj.getVertexArrayBuffer() : obj.getVertexBuffer(),
				obj.getColor());
		return new Object3DData(boundingBox.getVertices()).setDrawModeList(boundingBox.getDrawModeList())
				.setVertexColorsArrayBuffer(boundingBox.getColors()).setDrawOrder(boundingBox.getDrawOrder())
				.setDrawMode(boundingBox.getDrawMode()).setDrawSize(boundingBox.getDrawSize())
				.setPosition(obj.getPosition()).setColor(obj.getColor()).setId(obj.getId() + "_boundingBox");
	}

	/**
	 * Generate a new object that contains the all the line normals for all the faces for the specified object
	 * 
	 * TODO: This only works for objects made of triangles. Make it useful for any kind of polygonal face
	 * 
	 * @param obj
	 *            the object to which we calculate the normals.
	 * @return the model with all the normal lines
	 */
	public static Object3DData buildFaceNormals(Object3DData obj) {
		if (obj.getDrawMode() != GLES20.GL_TRIANGLES) {
			return null;
		}

		FloatBuffer vertexBuffer = obj.getVertexArrayBuffer() != null ? obj.getVertexArrayBuffer()
				: obj.getVertexBuffer();
		if (vertexBuffer == null) {
			Log.v("Builder", "Generating face normals for '" + obj.getId() + "' I found that there is no vertex data");
			return null;
		}

		FloatBuffer normalsLines;
		ShortBuffer drawBuffer = obj.getDrawOrder();
		if (drawBuffer != null) {
			Log.v("Builder", "Generating face normals for '" + obj.getId() + "' using indices...");
			int size = /* 2 points */ 2 * 3 * /* 3 points per face */ (drawBuffer.capacity() / 3)
					* /* bytes per float */4;
			normalsLines = createNativeByteBuffer(size).asFloatBuffer();
			drawBuffer.position(0);
			for (int i = 0; i < drawBuffer.capacity(); i += 3) {
				int v1 = drawBuffer.get() * COORDS_PER_VERTEX;
				int v2 = drawBuffer.get() * COORDS_PER_VERTEX;
				int v3 = drawBuffer.get() * COORDS_PER_VERTEX;
				float[][] normalLine = Math3DUtils.calculateFaceNormal(
						new float[] { vertexBuffer.get(v1), vertexBuffer.get(v1 + 1), vertexBuffer.get(v1 + 2) },
						new float[] { vertexBuffer.get(v2), vertexBuffer.get(v2 + 1), vertexBuffer.get(v2 + 2) },
						new float[] { vertexBuffer.get(v3), vertexBuffer.get(v3 + 1), vertexBuffer.get(v3 + 2) });
				normalsLines.put(normalLine[0]).put(normalLine[1]);
			}
		} else {
			if (vertexBuffer.capacity() % (/* COORDS_PER_VERTEX */3 * /* VERTEX_PER_FACE */ 3) != 0) {
				// something in the data is wrong
				Log.v("Builder", "Generating face normals for '" + obj.getId()
						+ "' I found that vertices are not multiple of 9 (3*3): " + vertexBuffer.capacity());
				return null;
			}

			Log.v("Builder", "Generating face normals for '" + obj.getId() + "'...");
			normalsLines = createNativeByteBuffer(6 * vertexBuffer.capacity() / 9 * 4).asFloatBuffer();
			vertexBuffer.position(0);
			for (int i = 0; i < vertexBuffer.capacity() / /* COORDS_PER_VERTEX */ 3 / /* VERTEX_PER_FACE */3; i++) {
				float[][] normalLine = Math3DUtils.calculateFaceNormal(
						new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() },
						new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() },
						new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() });
				normalsLines.put(normalLine[0]).put(normalLine[1]);

				// debug
				@SuppressWarnings("unused")
				String normal = new StringBuilder().append(normalLine[0][0]).append(",").append(normalLine[0][1])
						.append(",").append(normalLine[0][2]).append("-").append(normalLine[1][0]).append(",")
						.append(normalLine[1][1]).append(",").append(normalLine[1][2]).toString();
				// Log.v("Builder", "fNormal[" + i + "]:(" + normal + ")");
			}
		}

		return new Object3DData(normalsLines).setDrawMode(GLES20.GL_LINES).setColor(obj.getColor())
				.setPosition(obj.getPosition()).setVersion(1);
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public static void loadV5Async(Activity parent, File file, String assetsDir, String assetName,
			final Callback callback) {
		Log.i("Loader", "Opening " + (file != null ? " file " + file : "asset " + assetsDir + assetName) + "...");
		final InputStream modelDataStream;
		try {
			if (file != null) {
				modelDataStream = new FileInputStream(file);
			} else if (assetsDir != null) {
				modelDataStream = parent.getAssets().open(assetsDir + assetName);
			} else {
				throw new IllegalArgumentException("Model data source not specified");
			}
		} catch (IOException ex) {
			throw new RuntimeException(
					"There was a problem opening file/asset '" + (file != null ? file : assetsDir + assetName) + "'");
		}

		Log.i("Loader", "Loading model...");
		LoaderTask loaderTask = new LoaderTask(parent, file != null ? file.getParentFile() : null, assetsDir,
				file != null ? file.getName() : assetName) {

			@Override
			protected void onPostExecute(Object3DData data) {
				super.onPostExecute(data);
				try {
					modelDataStream.close();
				} catch (IOException ex) {
					Log.e("Menu", "Problem closing stream: " + ex.getMessage(), ex);
				}
				if (error != null) {
					callback.onLoadError(error);
				} else {
					callback.onLoadComplete(data);
				}
			}
		};
		loaderTask.execute(modelDataStream);
	}

}

/**
 * This component allows loading the model without blocking the UI.
 * 
 * @author andresoviedo
 *
 */
class LoaderTask extends AsyncTask<InputStream, Integer, Object3DData> {

	/**
	 * The parent activity
	 */
	private final Activity parent;
	/**
	 * Directory where the model is located (null when its loaded from asset)
	 */
	private final File currentDir;
	/**
	 * Asset directory where the model is loaded (null when its loaded from the filesystem)
	 */
	private final String assetsDir;
	/**
	 * Id of the data being loaded
	 */
	private final String modelId;
	/**
	 * The dialog that will show the progress of the loading
	 */
	private final ProgressDialog dialog;
	/**
	 * Exception when loading data (if any)
	 */
	protected Exception error;

	/**
	 * Build a new progress dialog for loading the data model asynchronously
	 * 
	 * @param currentDir
	 *            the directory where the model is located (null when the model is an asset)
	 * @param modelId
	 *            the id the data being loaded
	 * 
	 */
	public LoaderTask(Activity parent, File currentDir, String assetsDir, String modelId) {
		this.parent = parent;
		this.currentDir = currentDir;
		this.assetsDir = assetsDir;
		this.modelId = modelId;
		this.dialog = new ProgressDialog(parent);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// this.dialog = ProgressDialog.show(this.parent, "Please wait ...", "Loading model data...", true);
		// this.dialog.setTitle(modelId);
		// this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	@Override
	protected Object3DData doInBackground(InputStream... params) {
		try {
			publishProgress(0);

			WavefrontLoader wfl = new WavefrontLoader("");
			wfl.loadModel(params[0]);
			publishProgress(1);

			Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
					wfl.getFaceMats(), wfl.getMaterials());
			data3D.setId(modelId);
			data3D.setCurrentDir(currentDir);
			data3D.setAssetsDir(assetsDir);

			Object3DBuilder.generateArrays(parent.getAssets(), data3D);
			publishProgress(2);
			return data3D;
		} catch (IOException ex) {
			error = ex;
			return null;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		switch (values[0]) {
		case 0:
			this.dialog.setMessage("Loading data...");
			break;
		case 1:
			this.dialog.setMessage("Building 3D model...");
			break;
		case 2:
			this.dialog.setMessage("Model '" + modelId + "' built");
			break;
		}
	}

	@Override
	protected void onPostExecute(Object3DData success) {
		super.onPostExecute(success);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}

class BoundingBox {

	// number of coordinates per vertex in this array
	protected static final int COORDS_PER_VERTEX = 3;
	protected static final int COORDS_PER_COLOR = 4;

	public FloatBuffer vertices;
	public FloatBuffer vertexArray;
	public FloatBuffer colors;
	public ShortBuffer drawOrder;

	public float xMin = Float.MAX_VALUE;
	public float xMax = Float.MIN_VALUE;
	public float yMin = Float.MAX_VALUE;
	public float yMax = Float.MIN_VALUE;
	public float zMin = Float.MAX_VALUE;
	public float zMax = Float.MIN_VALUE;

	public float[] center;
	public float[] sizes;
	public float radius;

	/**
	 * Build a bounding box for the specified 3D object vertex buffer.
	 * 
	 * @param vertexBuffer
	 *            the 3D object vertex buffer
	 * @param color
	 *            the color of the bounding box
	 */
	public BoundingBox(FloatBuffer vertexBuffer, float[] color) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				8 * COORDS_PER_VERTEX * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		vertices = bb.asFloatBuffer();

		ByteBuffer bb2 = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				(6 * 4) * 2);
		// use the device hardware's native byte order
		bb2.order(ByteOrder.nativeOrder());
		drawOrder = bb2.asShortBuffer();

		// vertex colors
		ByteBuffer bb3 = ByteBuffer.allocateDirect(24 * COORDS_PER_COLOR * 4);
		// use the device hardware's native byte order
		bb3.order(ByteOrder.nativeOrder());
		colors = bb3.asFloatBuffer();

		for (int i = 0; i < colors.capacity() / 4; i++) {
			if (color != null && color.length == 4) {
				colors.put(color);
			} else {
				colors.put(1.0f).put(0.0f).put(1.0f).put(1.0f);
			}
		}

		// back-face
		drawOrder.put((short) 0);
		drawOrder.put((short) 1);
		drawOrder.put((short) 2);
		drawOrder.put((short) 3);

		// front-face
		drawOrder.put((short) 4);
		drawOrder.put((short) 5);
		drawOrder.put((short) 6);
		drawOrder.put((short) 7);

		// left-face
		drawOrder.put((short) 4);
		drawOrder.put((short) 5);
		drawOrder.put((short) 1);
		drawOrder.put((short) 0);

		// right-face
		drawOrder.put((short) 3);
		drawOrder.put((short) 2);
		drawOrder.put((short) 6);
		drawOrder.put((short) 7);

		// top-face
		drawOrder.put((short) 1);
		drawOrder.put((short) 2);
		drawOrder.put((short) 6);
		drawOrder.put((short) 5);

		// bottom-face
		drawOrder.put((short) 0);
		drawOrder.put((short) 3);
		drawOrder.put((short) 7);
		drawOrder.put((short) 4);

		recalculate(vertexBuffer);
	}

	public ShortBuffer getDrawOrder() {
		return drawOrder;
	}

	public FloatBuffer getColors() {
		return colors;
	}

	public int getDrawMode() {
		return GLES20.GL_LINE_LOOP;
	}

	public int getDrawSize() {
		return 4;
	}

	public List<int[]> getDrawModeList() {
		List<int[]> ret = new ArrayList<int[]>();
		int drawOrderPos = 0;
		for (int i = 0; i < drawOrder.capacity(); i += 4) {
			ret.add(new int[] { GLES20.GL_LINE_LOOP, drawOrderPos, 4 });
			drawOrderPos += 4;
		}
		return ret;
	}

	BoundingBox(FloatBuffer vertexBuffer, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
		calculateVertex();
		calculateOther(vertexBuffer);
	}

	public void recalculate(FloatBuffer vertexBuffer) {

		calculateMins(vertexBuffer);
		calculateVertex();
		calculateOther(vertexBuffer);
	}

	/**
	 * This works only when COORDS_PER_VERTEX = 3
	 * 
	 * @param vertexBuffer
	 */
	private void calculateMins(FloatBuffer vertexBuffer) {
		vertexBuffer.position(0);
		while (vertexBuffer.hasRemaining()) {
			float vertexx = vertexBuffer.get();
			float vertexy = vertexBuffer.get();
			float vertexz = vertexBuffer.get();
			if (vertexx < xMin) {
				xMin = vertexx;
			}
			if (vertexx > xMax) {
				xMax = vertexx;
			}
			if (vertexy < yMin) {
				yMin = vertexy;
			}
			if (vertexy > yMax) {
				yMax = vertexy;
			}
			if (vertexz < zMin) {
				zMin = vertexz;
			}
			if (vertexz > zMax) {
				zMax = vertexz;
			}
		}
	}

	private void calculateVertex() {
		vertices.position(0);
		//@formatter:off
		vertices.put(xMin).put(yMin).put(zMin);  // down-left (far)
		vertices.put(xMin).put(yMax).put(zMin);  // up-left (far)
		vertices.put(xMax).put(yMax).put(zMin);  // up-right (far)
		vertices.put(xMax).put(yMin).put(zMin);  // down-right  (far)
		vertices.put(xMin).put(yMin).put(zMax);  // down-left (near)
		vertices.put(xMin).put(yMax).put(zMax);  // up-left (near)
		vertices.put(xMax).put(yMax).put(zMax);  // up-right (near)
		vertices.put(xMax).put(yMin).put(zMax);  // down-right (near)
		//@formatter:on
	}

	private void calculateOther(FloatBuffer vertexBuffer) {
		center = new float[] { (xMax + xMin) / 2, (yMax + yMin) / 2, (zMax + zMin) / 2 };
		sizes = new float[] { xMax - xMin, yMax - yMin, zMax - zMin };

		vertexBuffer.position(0);

		// calculated bounding sphere
		double radius = 0;
		double radiusTemp;
		vertexBuffer.position(0);
		while (vertexBuffer.hasRemaining()) {
			float vertexx = vertexBuffer.get();
			float vertexy = vertexBuffer.get();
			float vertexz = vertexBuffer.get();
			radiusTemp = Math.sqrt(Math.pow(vertexx - center[0], 2) + Math.pow(vertexy - center[1], 2)
					+ Math.pow(vertexz - center[2], 2));
			if (radiusTemp > radius) {
				radius = radiusTemp;
			}
		}
		this.radius = (float) radius;
	}

	public FloatBuffer getVertices() {
		return vertices;
	}

	public FloatBuffer getVertexArray() {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				drawOrder.capacity() * COORDS_PER_VERTEX * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer ret = bb.asFloatBuffer();
		ret.position(0);
		for (int i = 0; i < drawOrder.capacity(); i++) {
			ret.put(vertices.get(drawOrder.get(i) * 3)); // x
			ret.put(vertices.get(drawOrder.get(i) * 3 + 1)); // y
			ret.put(vertices.get(drawOrder.get(i) * 3 + 2)); // z
		}
		return ret;
	}

	public String sizeToString() {
		return "x[" + sizes[0] + "],y[" + sizes[1] + "],z[" + sizes[2] + "]";
	}

	public String centerToString() {
		return "x[" + center[0] + "],y[" + center[1] + "],z[" + center[2] + "]";
	}

	public String limitsToString() {
		StringBuffer ret = new StringBuffer();
		ret.append("xMin[" + xMin + "], xMax[" + xMax + "], yMin[" + yMin + "], yMax[" + yMax + "], zMin[" + zMin
				+ "], zMax[" + zMax + "]");
		return ret.toString();
	}

	public float[] getCenter() {
		return center;
	}

	public void setCenter(float[] center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public FloatBuffer getNormals() {
		return createEmptyNormalsFloatBuffer(getVertices().capacity());
	}

	private static FloatBuffer createEmptyNormalsFloatBuffer(int size) {
		FloatBuffer buffer = createNativeByteBuffer(size * 3 * 4).asFloatBuffer();
		buffer.position(0);
		for (int i = 0; i < size; i++) {
			buffer.put(0.0f).put(1.0f).put(0.0f);
		}
		return buffer;
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

}