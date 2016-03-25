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

import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Material;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;

import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;

public final class Object3DBuilder {

	private static final boolean DRAW_ARRAYS = true;
	private static boolean flipTexCoords = true;
	private static float[] DEFAULT_COLOR = { 1.0f, 1.0f, 0, 1.0f };

	public static Object3D createGLES20Object(Object3DData obj, File currentDir, String assetsDir, AssetManager am,
			int drawMode, int drawSize) throws IOException
	/*
	 * render the model to a display list, so it can be drawn quicker later
	 */
	{
		ArrayList<Tuple3> verts = obj.getVerts();
		ArrayList<Tuple3> normals = obj.getNormals();
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
		if (DRAW_ARRAYS) {
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

		FloatBuffer normalsBuffer = createNativeByteBuffer(3 * normals.size() * 4).asFloatBuffer();
		normalsBuffer.position(0);
		for (Tuple3 norm : normals) {
			normalsBuffer.put(norm.getX());
			normalsBuffer.put(norm.getY());
			normalsBuffer.put(norm.getZ());
		}

		FloatBuffer normalsArray = createNativeByteBuffer(3 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
		normalsArray.position(0);
		for (int[] normal : faces.facesNormIdxs) {
			for (int i = 0; i < normal.length; i++) {
				normalsArray.put(normalsBuffer.get(normal[i] * 3));
				normalsArray.put(normalsBuffer.get(normal[i] * 3 + 1));
				normalsArray.put(normalsBuffer.get(normal[i] * 3 + 2));
			}
		}

		FloatBuffer textCoordsBuffer = createNativeByteBuffer(2 * texCoords.size() * 4).asFloatBuffer();
		textCoordsBuffer.position(0);
		for (Tuple3 texCor : texCoords) {
			textCoordsBuffer.put(texCor.getX());
			textCoordsBuffer.put(flipTexCoords ? 1 - texCor.getY() : texCor.getY());
		}

		FloatBuffer textureArraysBuffer = createNativeByteBuffer(2 * faces.getVerticesReferencesCount() * 4)
				.asFloatBuffer();
		try {
			for (int[] text : faces.facesTexIdxs) {
				for (int i = 0; i < text.length; i++) {
					textureArraysBuffer.put(textCoordsBuffer.get(text[i] * 2));
					textureArraysBuffer.put(textCoordsBuffer.get(text[i] * 2 + 1));
				}
			}
		} catch (Exception ex) {
			Log.e("WavefrontLoader", "Failure to load texture coordinates");
		}

		if (materials != null) {
			materials.readMaterials(currentDir, assetsDir, am);
		}

		FloatBuffer colorArrayBuffer = null;
		if (!faceMats.isEmpty()) {
			colorArrayBuffer = createNativeByteBuffer(4 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
			colorArrayBuffer.position(0);
			float[] currentColor = DEFAULT_COLOR;
			for (int i = 0; i < faces.facesVertIdxs.size(); i++) {
				if (faceMats.findMaterial(i) != null) {
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
		}

		// materials = null;

		List<InputStream> textureIs = null;
		if (materials != null && !materials.materials.isEmpty()) {
			textureIs = new ArrayList<InputStream>();
			// FileInputStream is = new FileInputStream(fileName);
			String texture = null;
			for (Material mat : materials.materials.values()) {
				if (mat.getTexture() != null) {
					texture = mat.getTexture();
					break;
				}
			}
			if (texture != null) {
				if (currentDir != null) {
					File file = new File(currentDir, texture);
					Log.v("materials", "Loading texture '" + file + "'...");
					textureIs.add(new FileInputStream(file));
				} else {
					Log.v("materials", "Loading texture '" + assetsDir + texture + "'...");
					textureIs.add(am.open(assetsDir + texture));
				}
			} else {
				Log.i("Loader", "Found material(s) but no texture");
			}
		}

		// TODO: removed because i im refactoring the ObjectV3.. return new ObjectV3(vertexBuffer, indexBuffer,
		// normalsBuffer,
		// textCoordsBuffer, GLES20.GL_TRIANGLES, 3,
		// materials != null ? materials.materials.get(0).getTexture() : null);

		if (colorArrayBuffer != null) {

			return new ObjectV5(DRAW_ARRAYS ? vertexArrayBuffer : vertexBuffer, drawModeList, colorArrayBuffer,
					DRAW_ARRAYS ? null : null, normalsArray, textureArraysBuffer, drawMode, drawSize,
					textureIs != null ? textureIs : null);
		} else {
			return new ObjectV3(DRAW_ARRAYS ? vertexArrayBuffer : vertexBuffer, DRAW_ARRAYS ? null : null,
					normalsBuffer, textureArraysBuffer, drawMode, drawSize,
					textureIs != null ? textureIs.get(0) : null);
		}

		// if (materials != null) {
		// // materials.readMaterials();
		// }
		// modelDispList = gl.glGenLists(1);
		// gl.glNewList(modelDispList, GLES20.GL_COMPILE);
		//
		// // gl.glPushMatrix();
		// // render the model face-by-face
		// String faceMat;
		// for (int i = 0; i < faces.getNumFaces(); i++) {
		// faceMat = faceMats.findMaterial(i); // get material used by face i
		// if (faceMat != null)
		// flipTexCoords = materials.renderWithMaterial(faceMat, gl); // render
		// // using
		// // that
		// // material
		// faces.renderFace(i, flipTexCoords, gl); // draw face i
		// }
		// if (materials != null)
		// materials.switchOffTex(gl);
		// // gl.glPopMatrix();
		//
		// gl.glEndList();
		// return modelDispList;
	}// end of drawToList()

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
