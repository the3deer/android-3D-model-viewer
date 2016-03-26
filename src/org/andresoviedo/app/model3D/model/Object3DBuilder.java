package org.andresoviedo.app.model3D.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.services.WavefrontLoader;
import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Material;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;
import org.andresoviedo.app.util.math.Math3DUtils;

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

	/**
	 * Default vertices colors
	 */
	private static float[] DEFAULT_COLOR = { 1.0f, 1.0f, 0, 1.0f };

	final static float[] squarePositionData = new float[] { -0.5f, 0.5f, 0.0f, // top left
			-0.5f, -0.5f, 0.0f, // bottom left
			0.5f, -0.5f, 0.0f, // bottom right
			0.5f, 0.5f, 0.0f };// upper right

	final static short[] squareDrawOrderData = new short[] { 0, 1, 2, 0, 2, 3 };

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
			-1.0f, -1.0f, -1.0f,
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

	public static Object3DData buildCubeV1() {
		return new Object3DData(createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer()
				.put(cubePositionData).asReadOnlyBuffer()).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV1");
	}

	public static Object3DData buildSquareV2() {
		return new Object3DData(
				createNativeByteBuffer(squarePositionData.length * 4).asFloatBuffer().put(squarePositionData)
						.asReadOnlyBuffer(),
				createNativeByteBuffer(squareDrawOrderData.length * 2).asShortBuffer().put(squareDrawOrderData)
						.asReadOnlyBuffer()).setDrawMode(GLES20.GL_TRIANGLES).setId("square1");
	}

	public static Object3DData buildCubeV3(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData)
						.asReadOnlyBuffer(),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV3");
	}

	public static Object3DData buildCubeV4(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData)
						.asReadOnlyBuffer(),
				createNativeByteBuffer(cubeColorData.length * 4).asFloatBuffer().put(cubeColorData).asReadOnlyBuffer(),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV4");
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
			data3D.setDrawMode(GLES20.GL_TRIANGLES);
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

	public static Object3D buildAxis() {
		return build(new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
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
	}

	public static Object3D build(float[] verts, int drawMode) {
		return new ObjectV1(createNativeByteBuffer(verts.length * 4).asFloatBuffer().put(verts), drawMode);
	}

	public static Object3D build(float[] verts, short[] drawOrder, int drawMode) {
		return new ObjectV2(createNativeByteBuffer(verts.length * 4).asFloatBuffer().put(verts),
				createNativeByteBuffer(verts.length * 2).asShortBuffer().put(drawOrder), drawMode);
	}

	public static Object3D build(float[] verts, float[] textCoord, int drawType, int drawSize, InputStream texture) {
		return new ObjectV3(createNativeByteBuffer(4 * verts.length).asFloatBuffer().put(verts).asReadOnlyBuffer(),
				createNativeByteBuffer(4 * textCoord.length).asFloatBuffer().put(textCoord).asReadOnlyBuffer(),
				drawType, drawSize, texture);
	}

	public static Object3D build(float[] verts, float[] vertColors, float[] textCoord, int drawType, int drawSize,
			InputStream texture) {
		return new ObjectV4(createNativeByteBuffer(4 * verts.length).asFloatBuffer().put(verts).asReadOnlyBuffer(),
				createNativeByteBuffer(4 * vertColors.length).asFloatBuffer().put(vertColors).asReadOnlyBuffer(),
				createNativeByteBuffer(4 * textCoord.length).asFloatBuffer().put(textCoord).asReadOnlyBuffer(),
				drawType, drawSize, texture);
	}

	public static Object3D build(Object3DData obj) throws IOException {
		switch (obj.getVersion()) {
		case 1:
			return new ObjectV1(obj.getVertexArrayBuffer(), obj.getDrawMode()).setColor(obj.getColor());
		case 2:
			return new ObjectV2(obj.getVertexBuffer(), obj.getDrawBuffer(), obj.getDrawMode()).setColor(obj.getColor());
		case 3:
			return new ObjectV3(obj.getVertexArrayBuffer(), obj.getTextureCoordsArrayBuffer(), obj.getDrawMode(),
					obj.getDrawSize(), obj.getTextureStream0()).setColor(obj.getColor());
		case 4:
			return new ObjectV4(obj.getVertexArrayBuffer(), obj.getVertexColorsArrayBuffer(),
					obj.getTextureCoordsArrayBuffer(), obj.getDrawMode(), obj.getDrawSize(), obj.getTextureStream0());
		case 5:
			return new ObjectV5(obj.getVertexArrayBuffer(), obj.getDrawModeList(), obj.getVertexColorsArrayBuffer(),
					obj.getDrawBuffer(), obj.getTextureCoordsArrayBuffer(), obj.getDrawMode(), obj.getDrawSize(),
					obj.getTextureStream0());
		default:
			return null;
		}
	}

	public static Object3D build(AssetManager assets, String assetDir, String modelId) {
		try {
			InputStream is = assets.open(assetDir + modelId);
			WavefrontLoader wfl = new WavefrontLoader(modelId);
			wfl.loadModel(is);
			is.close();

			Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
					wfl.getFaceMats(), wfl.getMaterials());
			data3D.setId(modelId);
			data3D.setAssetsDir(assetDir);
			data3D = generateArrays(assets, data3D);
			if (data3D.getVertexColorsArrayBuffer() != null) {
				data3D.setVersion(5);
			} else {
				data3D.setDrawMode(GLES20.GL_TRIANGLES).setDrawSize(3);
				data3D.setVersion(3);
			}
			return build(data3D);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object3DData generateArrays(AssetManager assets, Object3DData obj) throws IOException {
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
		if (obj.isDrawUsingArrays()) {
			vertexArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			for (int[] face : faces.facesVertIdxs) {
				for (int i = 0; i < face.length; i++) {
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 1));
					vertexArrayBuffer.put(vertexBuffer.get(face[i] * 3 + 2));
				}
			}
		}

		List<int[]> drawModeList = new ArrayList<int[]>();
		int currentVertexPos = 0;
		for (int[] face : faces.facesVertIdxs) {
			if (face.length == 3) {
				drawModeList.add(new int[] { GLES20.GL_TRIANGLES, currentVertexPos, face.length });
			} else {
				drawModeList.add(new int[] { GLES20.GL_TRIANGLE_FAN, currentVertexPos, face.length });
			}
			currentVertexPos += face.length;
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

		List<InputStream> textureStreams = null;
		if (materials != null && !materials.materials.isEmpty()) {
			textureStreams = new ArrayList<InputStream>();
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
					Log.v("materials", "Loading texture '" + file + "'...");
					textureStreams.add(new FileInputStream(file));
				} else {
					Log.v("materials", "Loading texture '" + obj.getAssetsDir() + texture + "'...");
					textureStreams.add(assets.open(obj.getAssetsDir() + texture));
				}
			} else {
				Log.i("Loader", "Found material(s) but no texture");
			}
		}

		obj.setVertexBuffer(vertexBuffer);
		obj.setVertexNormalsBuffer(vertexNormalsBuffer);
		obj.setTextureCoordsBuffer(textureCoordsBuffer);

		obj.setVertexArrayBuffer(vertexArrayBuffer);
		obj.setVertexNormalsArrayBuffer(vertexNormalsArrayBuffer);
		obj.setTextureCoordsArrayBuffer(textureCoordsArraysBuffer);

		obj.setDrawModeList(drawModeList);
		obj.setVertexColorsArrayBuffer(colorArrayBuffer);
		obj.setTextureStreams(textureStreams);

		return obj;
	}

	public static Object3DData buildBoundingBox(Object3DData obj) {
		BoundingBox boundingBox = new BoundingBox(
				obj.getVertexBuffer() != null ? obj.getVertexBuffer() : obj.getVertexArrayBuffer(), obj.getColor());
		return new Object3DData(boundingBox.getVertices()).setDrawModeList(boundingBox.getDrawModeList())
				.setVertexColorsArrayBuffer(boundingBox.getColors()).setDrawOrder(boundingBox.getDrawOrder())
				.setDrawMode(boundingBox.getDrawMode()).setDrawSize(boundingBox.getDrawSize())
				.setPosition(obj.getPosition()).setColor(obj.getColor()).setVersion(5);
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
		if (vertexBuffer == null
				|| vertexBuffer.capacity() % (/* COORDS_PER_VERTEX */3 * /* VERTEX_PER_FACE */ 3) != 0) {
			// something in the data is wrong
			Log.v("Builder", "Generating face normals for '" + obj.getId()
					+ "' I found that vertices are not multiple of 9 (3*3): " + vertexBuffer.capacity());
			return null;
		}

		Log.v("Builder", "Generating face normals for '" + obj.getId() + "'...");
		FloatBuffer normalsLines = createNativeByteBuffer(2 * vertexBuffer.capacity() * 4).asFloatBuffer();
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