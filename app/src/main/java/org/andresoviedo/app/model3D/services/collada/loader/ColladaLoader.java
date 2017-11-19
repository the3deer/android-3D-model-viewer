package org.andresoviedo.app.model3D.services.collada.loader;


import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.app.model3D.controller.LoaderTask;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.services.WavefrontLoader;
import org.andresoviedo.app.model3D.services.collada.entities.AnimatedModelData;
import org.andresoviedo.app.model3D.services.collada.entities.AnimationData;
import org.andresoviedo.app.model3D.services.collada.entities.MeshData;
import org.andresoviedo.app.model3D.services.collada.entities.SkeletonData;
import org.andresoviedo.app.model3D.services.collada.entities.SkinningData;
import org.andresoviedo.app.util.xml.XmlNode;
import org.andresoviedo.app.util.xml.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ColladaLoader {

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}


	public static void loadAsync(final Activity parent, URL url, final Object3DBuilder.Callback callback) {
		new ColladaLoader.ColladaLoaderTask(parent,url,callback).execute();
	}

	private static class ColladaLoaderTask extends LoaderTask {

		AnimatedModelData modelData;

		ColladaLoaderTask(Activity parent, URL url, Object3DBuilder.Callback callback) {
			super(parent, url, null, null, null, callback);
		}

		@Override
		protected Object3DData build() throws IOException {
			// Parse STL
			modelData = loadColladaModel(url.openStream(),3);
			MeshData meshData = modelData.getMeshData();
			int totalVertex = meshData.getVertexCount();

			// Allocate data
			FloatBuffer normalsBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
			FloatBuffer vertexBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
			IntBuffer indexBuffer = createNativeByteBuffer(meshData.getIndices().length * 4).asIntBuffer();

			// Initialize model dimensions (needed by the Object3DData#scaleCenter()
			WavefrontLoader.ModelDimensions modelDimensions = new WavefrontLoader.ModelDimensions();

			// notify succeded!
			Object3DData data3D = new Object3DData(vertexBuffer).setVertexNormalsArrayBuffer(normalsBuffer);
			data3D.setDimensions(modelDimensions);
			data3D.setDrawOrder(indexBuffer);
			data3D.setDrawUsingArrays(false);
			data3D.setDrawMode(GLES20.GL_TRIANGLES);
			return data3D;
		}

		@Override
		protected void build(Object3DData data) throws Exception
		{
			int counter = 0;
			try {
				// Parse all facets...
				double[] normal = new double[3];
				double[][] vertices = new double[3][3];
				int normalCounter=0, vertexCounter=0;

				FloatBuffer normalsBuffer = data.getVertexNormalsArrayBuffer();
				FloatBuffer vertexBuffer = data.getVertexArrayBuffer();
				IntBuffer indexBuffer = data.getDrawOrder();

				WavefrontLoader.ModelDimensions modelDimensions = data.getDimensions();

				MeshData meshData = modelData.getMeshData();

				boolean first = true;
				for (counter=0; counter < meshData.getVertices().length-3;counter+=3) {
					Log.d("ColladaLoaderTask", "Loading vertex " + counter + "");

					// update model dimensions
					if (first){
						modelDimensions.set(meshData.getVertices()[counter],meshData.getVertices()[counter+1],meshData.getVertices()[counter+2]);
						first = false;
					}
					modelDimensions.update(meshData.getVertices()[counter],meshData.getVertices()[counter+1],meshData.getVertices()[counter+2]);

				}
				vertexBuffer.put(meshData.getVertices());
				normalsBuffer.put(meshData.getNormals());
				indexBuffer.put(meshData.getIndices());

				Log.i("ColladaLoaderTask", "Building 3D object...");
				data.centerScale();

			} catch (Exception e) {
				Log.e("ColladaLoaderTask", "Vertex '"+counter+"'"+e.getMessage(), e);
				throw e;
			}finally {

			}
		}
	}



	public static AnimatedModelData loadColladaModel(InputStream colladaFile, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(InputStream colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
