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

import org.andresoviedo.app.model3D.services.WavefrontLoader;
import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Material;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

public final class Object3DBuilder {

	/**
	 * Default vertices colors
	 */
	private static float[] DEFAULT_COLOR = { 1.0f, 1.0f, 0, 1.0f };

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
			data3D = Object3DBuilder.generateArrays(assets, data3D);
			return build(data3D, GLES20.GL_TRIANGLES, 3);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object3D build(Object3DData obj, int drawMode, int drawSize) throws IOException {
		if (obj.getVertexColorsArrayBuffer() != null) {
			return new ObjectV5(obj.getVertexArrayBuffer(), obj.getDrawModeList(), obj.getVertexColorsArrayBuffer(),
					null, obj.getTextureCoordsArrayBuffer(), drawMode, drawSize, obj.getTextureStream0());
		} else {
			return new ObjectV3(obj.getVertexArrayBuffer(), obj.getTextureCoordsArrayBuffer(), drawMode, drawSize,
					obj.getTextureStream0());
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
		vertexBuffer.position(0);
		for (Tuple3 vert : verts) {
			vertexBuffer.put(vert.getX());
			vertexBuffer.put(vert.getY());
			vertexBuffer.put(vert.getZ());
		}

		FloatBuffer vertexArrayBuffer = null;
		if (obj.isDrawUsingArrays()) {
			vertexArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			vertexArrayBuffer.position(0);
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
		vertexNormalsBuffer.position(0);
		for (Tuple3 norm : vertexNormals) {
			vertexNormalsBuffer.put(norm.getX());
			vertexNormalsBuffer.put(norm.getY());
			vertexNormalsBuffer.put(norm.getZ());
		}

		FloatBuffer vertexNormalsArrayBuffer = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		vertexNormalsArrayBuffer.position(0);
		for (int[] normal : faces.facesNormIdxs) {
			for (int i = 0; i < normal.length; i++) {
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3));
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3 + 1));
				vertexNormalsArrayBuffer.put(vertexNormalsBuffer.get(normal[i] * 3 + 2));
			}
		}

		FloatBuffer textureCoordsBuffer = createNativeByteBuffer(2 * texCoords.size() * 4).asFloatBuffer();
		textureCoordsBuffer.position(0);
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
