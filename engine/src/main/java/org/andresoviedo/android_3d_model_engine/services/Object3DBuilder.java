package org.andresoviedo.android_3d_model_engine.services;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Joint;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.FaceMaterials;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Faces;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Material;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Materials;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader.Tuple3;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.math.Math3DUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public final class Object3DBuilder {

	private static final int COORDS_PER_VERTEX = 3;
	/**
	 * Default vertices colors
	 */
	private static float[] DEFAULT_COLOR = {1.0f, 1.0f, 0, 1.0f};

	final static float[] axisVertexLinesData = new float[]{
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

	final static float[] squarePositionData = new float[]{
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

	final static int[] squareDrawOrderData = new int[]{
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

	final static float[] cubeColorDataWithTransparency = {

			// Front face (red)
			1.0f, 0.0f, 0.0f, 0.5f,
			1.0f, 0.0f, 0.0f, 0.5f,
			1.0f, 0.0f, 0.0f, 0.5f,
			1.0f, 0.0f, 0.0f, 0.5f,
			1.0f, 0.0f, 0.0f, 0.5f,
			1.0f, 0.0f, 0.0f, 0.5f,

			// Right face (green)
			0.0f, 1.0f, 0.0f, 0.5f,
			0.0f, 1.0f, 0.0f, 0.5f,
			0.0f, 1.0f, 0.0f, 0.5f,
			0.0f, 1.0f, 0.0f, 0.5f,
			0.0f, 1.0f, 0.0f, 0.5f,
			0.0f, 1.0f, 0.0f, 0.5f,

			// Back face (blue)
			0.0f, 0.0f, 1.0f, 0.5f,
			0.0f, 0.0f, 1.0f, 0.5f,
			0.0f, 0.0f, 1.0f, 0.5f,
			0.0f, 0.0f, 1.0f, 0.5f,
			0.0f, 0.0f, 1.0f, 0.5f,
			0.0f, 0.0f, 1.0f, 0.5f,

			// Left face (yellow)
			1.0f, 1.0f, 0.0f, 0.5f,
			1.0f, 1.0f, 0.0f, 0.5f,
			1.0f, 1.0f, 0.0f, 0.5f,
			1.0f, 1.0f, 0.0f, 0.5f,
			1.0f, 1.0f, 0.0f, 0.5f,
			1.0f, 1.0f, 0.0f, 0.5f,

			// Top face (cyan)
			0.0f, 1.0f, 1.0f, 0.5f,
			0.0f, 1.0f, 1.0f, 0.5f,
			0.0f, 1.0f, 1.0f, 0.5f,
			0.0f, 1.0f, 1.0f, 0.5f,
			0.0f, 1.0f, 1.0f, 0.5f,
			0.0f, 1.0f, 1.0f, 0.5f,

			// Bottom face (magenta)
			1.0f, 0.0f, 1.0f, 0.5f,
			1.0f, 0.0f, 1.0f, 0.5f,
			1.0f, 0.0f, 1.0f, 0.5f,
			1.0f, 0.0f, 1.0f, 0.5f,
			1.0f, 0.0f, 1.0f, 0.5f,
			1.0f, 0.0f, 1.0f, 0.5f
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

	public static Object3DData buildPoint(float[] point) {
		return new Object3DData(createNativeByteBuffer(point.length * 4).asFloatBuffer().put(point))
				.setDrawMode(GLES20.GL_POINTS).setId("Point");
	}

	public static Object3DData buildLine(float[] line) {
		return new Object3DData(createNativeByteBuffer(line.length * 4).asFloatBuffer().put(line))
				.setDrawMode(GLES20.GL_LINES).setId("Line").setFaces(new Faces(0));
	}

	public static Object3DData buildAxis() {
		return new Object3DData(
				createNativeByteBuffer(axisVertexLinesData.length * 4).asFloatBuffer().put(axisVertexLinesData))
				.setDrawMode(GLES20.GL_LINES).setFaces(new Faces(0));
	}

	public static Object3DData buildCubeV1() {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData))
				.setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV1").centerAndScale(1.0f).setFaces(new Faces(8));
	}

	public static Object3DData buildCubeV1_with_normals() {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData))
				.setVertexColorsArrayBuffer(
						createNativeByteBuffer(cubeColorData.length * 4).asFloatBuffer().put(cubeColorData))
				.setVertexNormalsArrayBuffer(
						createNativeByteBuffer(cubeNormalData.length * 4).asFloatBuffer().put(cubeNormalData))
				.setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV1_light").centerAndScale(1.0f).setFaces(new Faces(8));
	}

	public static Object3DData buildSquareV2() {
		IntBuffer drawBuffer = createNativeByteBuffer(squareDrawOrderData.length * 4).asIntBuffer().put(squareDrawOrderData);
		FloatBuffer vertexBuffer = createNativeByteBuffer(squarePositionData.length * 4).asFloatBuffer().put(squarePositionData);
		return new Object3DData(vertexBuffer,drawBuffer.asReadOnlyBuffer()).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV2")
				.centerAndScale(1.0f).setFaces(new Faces(8)).setDrawOrder(drawBuffer).setVertexArrayBuffer(vertexBuffer);
	}

	public static Object3DData buildCubeV3(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV3").centerAndScale(1.0f).setFaces(new Faces(8));
	}

	public static Object3DData buildCubeV4(byte[] textureData) {
		return new Object3DData(
				createNativeByteBuffer(cubePositionData.length * 4).asFloatBuffer().put(cubePositionData),
				createNativeByteBuffer(cubeColorDataWithTransparency.length * 4).asFloatBuffer().put(cubeColorDataWithTransparency)
						.asReadOnlyBuffer(),
				createNativeByteBuffer(cubeTextureCoordinateData.length * 4).asFloatBuffer()
						.put(cubeTextureCoordinateData).asReadOnlyBuffer(),
				textureData).setDrawMode(GLES20.GL_TRIANGLES).setId("cubeV4").centerAndScale(1.0f).setFaces(new Faces(8));
	}

	public static Object3DData loadV5(Activity activity, Uri modelUri) {
		try {
			//final String modelId = assetDir + "/" + assetFilename;

			InputStream is = new URL(modelUri.toString()).openStream();
			WavefrontLoader wfl = new WavefrontLoader(modelUri.toString());
			wfl.analyzeModel(is);
			is.close();

			wfl.allocateBuffers();

			is = new URL(modelUri.toString()).openStream();
			wfl.loadModel(is);
			is.close();

			Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
					wfl.getFaceMats(), wfl.getMaterials());
			data3D.setId(modelUri.toString());
			data3D.setUri(modelUri);
			// data3D.setAssetsDir(assetDir);
			data3D.setDimensions(wfl.getDimensions());
			//data3D.centerAndScale(5,new float[]{0,0,0});
			data3D.centerScale();

			data3D.setDrawMode(GLES20.GL_TRIANGLES);
			generateArrays(data3D);

			return data3D;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Object3DData generateArrays(Object3DData obj) throws IOException {

	    Log.i("Object3DBuilder","Generating arrays for "+obj.getId());

		Faces faces = obj.getFaces(); // model faces
		FaceMaterials faceMats = obj.getFaceMats();
		Materials materials = obj.getMaterials();

		if (faces == null)  {
			Log.i("Object3DBuilder", "No faces. Not generating arrays");
			return obj;
		}

		Log.i("Object3DBuilder", "Allocating vertex array buffer... Vertices ("+faces.getVerticesReferencesCount()+")");
		final FloatBuffer vertexArrayBuffer = createNativeByteBuffer(faces.getVerticesReferencesCount() * 3 * 4).asFloatBuffer();
		obj.setVertexArrayBuffer(vertexArrayBuffer);
		obj.setDrawUsingArrays(true);

		Log.i("Object3DBuilder", "Populating vertex array...");
		final FloatBuffer vertexBuffer = obj.getVerts();
		final IntBuffer indexBuffer = faces.getIndexBuffer();
		for (int i = 0; i < faces.getVerticesReferencesCount(); i++) {
			vertexArrayBuffer.put(i*3,vertexBuffer.get(indexBuffer.get(i) * 3));
			vertexArrayBuffer.put(i*3+1,vertexBuffer.get(indexBuffer.get(i) * 3 + 1));
			vertexArrayBuffer.put(i*3+2,vertexBuffer.get(indexBuffer.get(i) * 3 + 2));
		}

		Log.i("Object3DBuilder", "Allocating vertex normals buffer... Total normals ("+faces.facesNormIdxs.size()+")");
		// Normals buffer size = Number_of_faces X 3 (vertices_per_face) X 3 (coords_per_normal) X 4 (bytes_per_float)
		final FloatBuffer vertexNormalsArrayBuffer = createNativeByteBuffer(faces.getSize() * 3 * 3 * 4).asFloatBuffer();;
		obj.setVertexNormalsArrayBuffer(vertexNormalsArrayBuffer);

		// build file normals
		final FloatBuffer vertexNormalsBuffer = obj.getNormals();
		if (vertexNormalsBuffer != null && vertexNormalsBuffer.capacity() > 0) {
			Log.i("Object3DBuilder", "Populating normals buffer...");
			for (int n=0; n<faces.facesNormIdxs.size(); n++) {
				int[] normal = faces.facesNormIdxs.get(n);
				for (int i = 0; i < normal.length; i++) {
					vertexNormalsArrayBuffer.put(n*9+i*3,vertexNormalsBuffer.get(normal[i] * 3));
					vertexNormalsArrayBuffer.put(n*9+i*3+1,vertexNormalsBuffer.get(normal[i] * 3 + 1));
					vertexNormalsArrayBuffer.put(n*9+i*3+2,vertexNormalsBuffer.get(normal[i] * 3 + 2));
				}
			}
		} else {
			// calculate normals for all triangles
			Log.i("Object3DBuilder", "Model without normals. Calculating [" + faces.getIndexBuffer().capacity() / 3 + "] normals...");

			final float[] v0 = new float[3], v1 = new float[3], v2 = new float[3];
			for (int i = 0; i < faces.getIndexBuffer().capacity(); i += 3) {
				try {
					v0[0] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3);
					v0[1] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3 + 1);
					v0[2] = vertexBuffer.get(faces.getIndexBuffer().get(i) * 3 + 2);

					v1[0] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3);
					v1[1] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3 + 1);
					v1[2] = vertexBuffer.get(faces.getIndexBuffer().get(i + 1) * 3 + 2);

					v2[0] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3);
					v2[1] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3 + 1);
					v2[2] = vertexBuffer.get(faces.getIndexBuffer().get(i + 2) * 3 + 2);

					float[] normal = Math3DUtils.calculateFaceNormal2(v0, v1, v2);

					vertexNormalsArrayBuffer.put(i*3,normal[0]);
					vertexNormalsArrayBuffer.put(i*3+1,normal[1]);
					vertexNormalsArrayBuffer.put(i*3+2,normal[2]);
					vertexNormalsArrayBuffer.put(i*3+3,normal[0]);
					vertexNormalsArrayBuffer.put(i*3+4,normal[1]);
					vertexNormalsArrayBuffer.put(i*3+5,normal[2]);
					vertexNormalsArrayBuffer.put(i*3+6,normal[0]);
					vertexNormalsArrayBuffer.put(i*3+7,normal[1]);
					vertexNormalsArrayBuffer.put(i*3+8,normal[2]);
				} catch (BufferOverflowException ex) {
					throw new RuntimeException("Error calculating normal for face ["+i/3+"]");
				}
			}
		}


		FloatBuffer colorArrayBuffer = null;
		if (materials != null) {
			Log.i("Object3DBuilder", "Reading materials...");
			try(InputStream inputStream = ContentUtils.getInputStream(materials.mfnm)) {
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				materials.readMaterials(br);
				materials.showMaterials();
				br.close();
			} catch (Exception ex){
			    Log.e("Object3DBuilder","Couldn't load material file "+materials.mfnm+". "+ex.getMessage(), ex);
			    obj.addError(materials.mfnm+":"+ex.getMessage());
            }
		}

		if (materials != null && !faceMats.isEmpty()) {
			Log.i("Object3DBuilder", "Processing face materials...");
			colorArrayBuffer = createNativeByteBuffer(4 * faces.getVerticesReferencesCount() * 4)
					.asFloatBuffer();
			boolean anyOk = false;
			float[] currentColor = DEFAULT_COLOR;
			for (int i = 0; i < faces.getSize(); i++) {
				if (faceMats.findMaterial(i) != null) {
					Material mat = materials.getMaterial(faceMats.findMaterial(i));
					if (mat != null) {
						currentColor = mat.getKdColor() != null ? mat.getKdColor() : currentColor;
						anyOk = anyOk || mat.getKdColor() != null;
					}
				}
				colorArrayBuffer.put(currentColor);
				colorArrayBuffer.put(currentColor);
				colorArrayBuffer.put(currentColor);
			}
			if (!anyOk) {
				Log.i("Object3DBuilder", "Using single color.");
				colorArrayBuffer = null;
			}
		}
		obj.setVertexColorsArrayBuffer(colorArrayBuffer);


		String texture = null;
		byte[] textureData = null;
		if (materials != null && !materials.materials.isEmpty()) {

			// TODO: process all textures
			for (Material mat : materials.materials.values()) {
				if (mat.getTexture() != null) {
					texture = mat.getTexture();
					break;
				}
			}
			if (texture != null) {
				obj.setTextureFile(texture);
			    Log.i("Object3DBuilder","Texture "+texture);
			} else {
				Log.i("Object3DBuilder", "Found material(s) but no texture");
			}
		} else{
			Log.i("Object3DBuilder", "No materials -> No texture");
		}


		//if (textureData != null) {
			ArrayList<Tuple3> texCoords = obj.getTexCoords();
			if (texCoords != null && texCoords.size() > 0) {

				Log.i("Object3DBuilder", "Allocating/populating texture buffer (flipTexCoord:"+obj.isFlipTextCoords()+")...");
				FloatBuffer textureCoordsBuffer = createNativeByteBuffer(texCoords.size() * 2 * 4).asFloatBuffer();
				for (Tuple3 texCor : texCoords) {
					textureCoordsBuffer.put(texCor.getX());
					textureCoordsBuffer.put(obj.isFlipTextCoords() ? 1 - texCor.getY() : texCor.getY());
				}

				Log.i("Object3DBuilder", "Populating texture array buffer...");
				FloatBuffer textureCoordsArraysBuffer = createNativeByteBuffer(2 * faces.getVerticesReferencesCount() * 4).asFloatBuffer();
				obj.setTextureCoordsArrayBuffer(textureCoordsArraysBuffer);

				try {

					boolean anyTextureOk = false;
					String currentTexture = null;

					Log.i("Object3DBuilder", "Populating texture array buffer...");
					int counter = 0;
					for (int i = 0; i < faces.facesTexIdxs.size(); i++) {

						// get current texture
						if (!faceMats.isEmpty() && faceMats.findMaterial(i) != null) {
							Material mat = materials.getMaterial(faceMats.findMaterial(i));
							if (mat != null && mat.getTexture() != null) {
								currentTexture = mat.getTexture();
							}
						}

						// check if texture is ok (Because we only support 1 texture currently)
						boolean textureOk = false;
						if (currentTexture != null && currentTexture.equals(texture)) {
							textureOk = true;
						}

						// populate texture coords if ok (in case we have more than 1 texture and 1 is missing. see face.obj example)
						int[] text = faces.facesTexIdxs.get(i);
						for (int j = 0; j < text.length; j++) {
							if (textureData == null || textureOk) {
								if (text[j] * 2 >= 0 && text[j] * 2 < textureCoordsBuffer.limit()) {
									anyTextureOk = true;
									textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[j] * 2));
									textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[j] * 2 + 1));
								} else{
									Log.v("Object3DBuilder","Wrong texture for face "+i);
									textureCoordsArraysBuffer.put(counter++, 0f);
									textureCoordsArraysBuffer.put(counter++, 0f);
								}
							} else {
								textureCoordsArraysBuffer.put(counter++, 0f);
								textureCoordsArraysBuffer.put(counter++, 0f);
							}
						}
					}

					if (!anyTextureOk) {
						Log.i("Object3DBuilder", "Texture is wrong. Applying global texture");
						counter = 0;
						for (int j=0; j<faces.facesTexIdxs.size(); j++) {
							int[] text = faces.facesTexIdxs.get(j);
							for (int i = 0; i < text.length; i++) {
								textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[i] * 2));
								textureCoordsArraysBuffer.put(counter++, textureCoordsBuffer.get(text[i] * 2 + 1));
							}
						}
					}
				} catch (Exception ex) {
					Log.e("Object3DBuilder", "Failure to load texture coordinates", ex);
				}
			}
		//}
		obj.setTextureData(textureData);

		return obj;
	}

	public static Object3DData buildBoundingBox(Object3DData obj) {
		BoundingBoxBuilder boundingBox = new BoundingBoxBuilder(obj.getBoundingBox(),
				obj.getColor());
		return new Object3DData(boundingBox.getVertices()).setDrawModeList(boundingBox.getDrawModeList())
				.setDrawOrder(boundingBox.getDrawOrder())
				.setDrawMode(boundingBox.getDrawMode())
				.setColor(obj.getColor()).setId(obj.getId() + "_boundingBox");
	}

	/**
	 * Builds a wireframe of the model by drawing all lines (3) of the triangles. This method uses
	 * the drawOrder buffer.
	 * @param objData the 3d model
	 * @return the 3d wireframe
	 */
	public static Object3DData buildWireframe(Object3DData objData) {

		if (objData.getDrawOrder() != null) {

			try {
				Log.i("Object3DBuilder", "Building wireframe...");
				IntBuffer drawBuffer = objData.getDrawOrder();
				IntBuffer wireframeDrawOrder = createNativeByteBuffer(drawBuffer.capacity() * 2 * 4).asIntBuffer();
				for (int i = 0; i < drawBuffer.capacity(); i += 3) {
					int v0 = drawBuffer.get(i);
					int v1 = drawBuffer.get(i + 1);
					int v2 = drawBuffer.get(i + 2);
					if (objData.isDrawUsingArrays()) {
						v0 = i;
						v1 = i + 1;
						v2 = i + 2;
					}
					wireframeDrawOrder.put(v0);
					wireframeDrawOrder.put(v1);
					wireframeDrawOrder.put(v1);
					wireframeDrawOrder.put(v2);
					wireframeDrawOrder.put(v2);
					wireframeDrawOrder.put(v0);
				}
				if (objData instanceof AnimatedModel){
					AnimatedModel object3DData = new AnimatedModel(objData.getVertexArrayBuffer());
					object3DData.setVertexBuffer(objData.getVertexBuffer()).setDrawOrder(wireframeDrawOrder).
							setVertexNormalsArrayBuffer(objData.getVertexNormalsArrayBuffer()).setColor(objData.getColor())
							.setVertexColorsArrayBuffer(objData.getVertexColorsArrayBuffer()).setTextureCoordsArrayBuffer(objData.getTextureCoordsArrayBuffer())
							.setPosition(objData.getPosition()).setRotation(objData.getRotation()).setScale(objData.getScale())
							.setDrawMode(GLES20.GL_LINES).setDrawUsingArrays(false);
					object3DData.setVertexWeights(((AnimatedModel) objData).getVertexWeights());
					object3DData.setJointIds(((AnimatedModel) objData).getJointIds());
					object3DData.setRootJoint(((AnimatedModel) objData).getRootJoint(), ((AnimatedModel) objData)
							.getJointCount(), ((AnimatedModel) objData).getBoneCount(), false);
					object3DData.doAnimation(((AnimatedModel) objData).getAnimation());
					return object3DData;
				}
				else {
					return new Object3DData(objData.getVertexArrayBuffer()).setVertexBuffer(objData.getVertexBuffer()).setDrawOrder(wireframeDrawOrder).
							setVertexNormalsArrayBuffer(objData.getVertexNormalsArrayBuffer()).setColor(objData.getColor())
							.setVertexColorsArrayBuffer(objData.getVertexColorsArrayBuffer()).setTextureCoordsArrayBuffer(objData.getTextureCoordsArrayBuffer())
							.setPosition(objData.getPosition()).setRotation(objData.getRotation()).setScale(objData.getScale())
							.setDrawMode(GLES20.GL_LINES).setDrawUsingArrays(false);
				}
			} catch (Exception ex) {
				Log.e("Object3DBuilder", ex.getMessage(), ex);
			}
		}
		else if (objData.getVertexArrayBuffer() != null){
			Log.i("Object3DBuilder", "Building wireframe...");
			FloatBuffer vertexBuffer = objData.getVertexArrayBuffer();
			IntBuffer wireframeDrawOrder = createNativeByteBuffer(vertexBuffer.capacity()/3 * 2 * 4).asIntBuffer();
			for (int i = 0; i < vertexBuffer.capacity()/3; i += 3) {
				wireframeDrawOrder.put(i);
				wireframeDrawOrder.put(i+1);
				wireframeDrawOrder.put(i+1);
				wireframeDrawOrder.put(i+2);
				wireframeDrawOrder.put(i+2);
				wireframeDrawOrder.put(i);
			}
			return new Object3DData(objData.getVertexArrayBuffer()).setVertexBuffer(objData.getVertexBuffer()).setDrawOrder(wireframeDrawOrder).
					setVertexNormalsArrayBuffer(objData.getVertexNormalsArrayBuffer()).setColor(objData.getColor())
					.setVertexColorsArrayBuffer(objData.getVertexColorsArrayBuffer()).setTextureCoordsArrayBuffer(objData.getTextureCoordsArrayBuffer())
					.setPosition(objData.getPosition()).setRotation(objData.getRotation()).setScale(objData.getScale())
					.setDrawMode(GLES20.GL_LINES).setDrawUsingArrays(false);
		}
		return objData;
	}

	/**
	 * Build a wireframe from obj vertices and faces.  This method uses less memory that {@link #buildWireframe(Object3DData)}
	 * --The problem-- in using this method  is that we are reshaping the object (scaling) after
	 * it is loaded, so this wireframe wont match current state of the shape
	 * @param objData the 3d model
	 * @return the 3d wireframe
	 */
	public static Object3DData buildWireframe_from_original(Object3DData objData) {
		try {
			IntBuffer drawOrder = createNativeByteBuffer(objData.getFaces().getIndexBuffer().capacity() * 2 * 4).asIntBuffer();
			for (int i = 0; i < objData.getFaces().getIndexBuffer().capacity(); i+=3) {
					drawOrder.put(objData.getFaces().getIndexBuffer().get(i));
					drawOrder.put((objData.getFaces().getIndexBuffer().get(i+1)));
					drawOrder.put((objData.getFaces().getIndexBuffer().get(i+1)));
					drawOrder.put((objData.getFaces().getIndexBuffer().get(i+2)));
					drawOrder.put((objData.getFaces().getIndexBuffer().get(i+2)));
					drawOrder.put((objData.getFaces().getIndexBuffer().get(i)));
			}
			return new Object3DData(objData.getVertexBuffer()).setDrawOrder(drawOrder).
					setVertexNormalsArrayBuffer(objData.getVertexNormalsBuffer()).setColor(objData.getColor())
					.setDrawMode(GLES20.GL_LINES);
		} catch (Exception ex) {
			Log.e("Object3DBuilder", ex.getMessage(), ex);
		}
		return objData;
	}

	/**
	 * Generate a new object that contains all the line normals for all the faces for the specified object
	 * <p>
	 * TODO: This only works for objects made of triangles. Make it useful for any kind of polygonal face
	 *
	 * @param obj the object to which we calculate the normals.
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
		IntBuffer drawBuffer = obj.getDrawOrder();
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
						new float[]{vertexBuffer.get(v1), vertexBuffer.get(v1 + 1), vertexBuffer.get(v1 + 2)},
						new float[]{vertexBuffer.get(v2), vertexBuffer.get(v2 + 1), vertexBuffer.get(v2 + 2)},
						new float[]{vertexBuffer.get(v3), vertexBuffer.get(v3 + 1), vertexBuffer.get(v3 + 2)});
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
						new float[]{vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get()},
						new float[]{vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get()},
						new float[]{vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get()});
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

	public static AnimatedModel buildSkeleton(AnimatedModel animatedModel){
        float[] identity = new float[16];
        Matrix.setIdentityM(identity,0);

        AnimatedModel skeleton = new AnimatedModel(createNativeByteBuffer(animatedModel.getJointCount()*3*3*4)
                .asFloatBuffer());
        skeleton.setVertexNormalsArrayBuffer(createNativeByteBuffer(animatedModel.getJointCount()*3*3*4)
                .asFloatBuffer());
        skeleton.setDrawMode(GLES20.GL_TRIANGLES);
        skeleton.setRootJoint(animatedModel.getRootJoint().clone(), animatedModel.getJointCount(), animatedModel
                .getBoneCount(), true);
        skeleton.setJointIds(createNativeByteBuffer(skeleton.getJointCount()*3*3*4).asFloatBuffer());
        skeleton.doAnimation(animatedModel.getAnimation());
        skeleton.setVertexWeights(createNativeByteBuffer(skeleton.getJointCount()*3*3*4).asFloatBuffer());
        skeleton.setPosition(animatedModel.getPosition());
        skeleton.setScale(animatedModel.getScale());

        Log.i("Object3DBuilder","Building "+skeleton.getJointCount()+" bones...");
        buildBones(skeleton, skeleton.getRootJoint(), identity, new float[]{0,0,0}, -1, animatedModel.getVertexBuffer());

        skeleton.setId(animatedModel.getId()+"-skeleton");

        return skeleton;
    }

    private static void buildBones(AnimatedModel animatedModel, Joint joint, float[] parentTransform, float[]
            parentPoint, int parentJoinIndex, FloatBuffer vertexBuffer){

        float[] point = new float[4];
        float[] transform = new float[16];
        Matrix.multiplyMM(transform,0,parentTransform,0,joint.getBindLocalTransform(),0);
        Matrix.multiplyMV(point,0,transform,0,new float[]{0,0,0,1},0);

        float[] v = Math3DUtils.substract(point,parentPoint);
        float[] point1 = new float[]{point[0],point[1],point[2]-Matrix.length(v[0],v[1],v[2])*0.05f};
        float[] point2 = new float[]{point[0],point[1],point[2]+Matrix.length(v[0],v[1],v[2])*0.05f};

        float[] normal = Math3DUtils.calculateFaceNormal2(parentPoint, point1, point2);

        // TODO: remove this
        /*parentPoint = new float[]{vertexBuffer.get((int)(100* Math.random())),vertexBuffer.get((int)(100* Math.random
                ())),vertexBuffer.get((int)(100* Math.random()))};*/

        animatedModel.getVertexArrayBuffer().put(parentPoint[0]);
        animatedModel.getVertexArrayBuffer().put(parentPoint[1]);
        animatedModel.getVertexArrayBuffer().put(parentPoint[2]);
        animatedModel.getVertexArrayBuffer().put(point1[0]);
        animatedModel.getVertexArrayBuffer().put(point1[1]);
        animatedModel.getVertexArrayBuffer().put(point1[2]);
        animatedModel.getVertexArrayBuffer().put(point2[0]);
        animatedModel.getVertexArrayBuffer().put(point2[1]);
        animatedModel.getVertexArrayBuffer().put(point2[2]);

        animatedModel.getVertexNormalsArrayBuffer().put(normal);
        animatedModel.getVertexNormalsArrayBuffer().put(normal);
        animatedModel.getVertexNormalsArrayBuffer().put(normal);

        animatedModel.getJointIds().put(parentJoinIndex);
        animatedModel.getJointIds().put(parentJoinIndex);
        animatedModel.getJointIds().put(parentJoinIndex);
        for (int i=3; i<9; i++) {
            animatedModel.getJointIds().put(joint.getIndex());
        }
        for (int i=0; i<9; i+=3) {
            animatedModel.getVertexWeights().put(parentJoinIndex >= 0?1:0);
            animatedModel.getVertexWeights().put(0);
            animatedModel.getVertexWeights().put(0);
        }

        for (Joint child : joint.getChildren()){
            buildBones(animatedModel,child,transform, point, joint.getIndex(), vertexBuffer);
        }
    }

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}
}


