package org.andresoviedo.android_3d_model_engine.services.collada.loader;


import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.animation.JointTransform;
import org.andresoviedo.android_3d_model_engine.animation.KeyFrame;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.AnimatedModelData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.AnimationData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Joint;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointTransformData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.KeyFrameData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.andresoviedo.util.xml.XmlNode;
import org.andresoviedo.util.xml.XmlParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColladaLoader {

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public static Object[] buildAnimatedModel(URL url) throws IOException {
		List<Object3DData> ret = new ArrayList<>();
		InputStream is = url.openStream();
		AnimatedModelData modelData = loadColladaModel(is,3);
		is.close();
		List<MeshData> meshDataList = modelData.getMeshData();
		for (MeshData meshData : meshDataList) {
			int totalVertex = meshData.getVertexCount();

			// Allocate data
			FloatBuffer normalsBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
			FloatBuffer vertexBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
			IntBuffer indexBuffer = createNativeByteBuffer(meshData.getIndices().length * 4).asIntBuffer();


			// Initialize model dimensions (needed by the Object3DData#scaleCenter()
			WavefrontLoader.ModelDimensions modelDimensions = new WavefrontLoader.ModelDimensions();

			// notify succeded!
			AnimatedModel data3D = new AnimatedModel(vertexBuffer);
			data3D.setVertexBuffer(vertexBuffer);
			data3D.setVertexNormalsArrayBuffer(normalsBuffer);
			data3D.setTextureFile(meshData.getTexture());
			if (meshData.getTextureCoords() != null) {
				int totalTextures = meshData.getTextureCoords().length;
				FloatBuffer textureBuffer = createNativeByteBuffer(totalTextures * 4).asFloatBuffer();
				textureBuffer.put(meshData.getTextureCoords());
				data3D.setTextureCoordsArrayBuffer(textureBuffer);
			}
			data3D.setDimensions(modelDimensions);
			data3D.setDrawOrder(indexBuffer);
			data3D.setDrawUsingArrays(false);
			data3D.setDrawMode(GLES20.GL_TRIANGLES);

			if (meshData.getJointIds() != null) {
				FloatBuffer intBuffer = createNativeByteBuffer(meshData.getJointIds().length * 4).asFloatBuffer();
				for (int i : meshData.getJointIds()) {
					intBuffer.put(i);
				}
				data3D.setJointIds(intBuffer);
			}
			if (meshData.getVertexWeights() != null) {
				FloatBuffer floatBuffer = createNativeByteBuffer(meshData.getVertexWeights().length * 4).asFloatBuffer();
				floatBuffer.put(meshData.getVertexWeights());
				data3D.setVertexWeights(floatBuffer);
			}
			ret.add(data3D);
		}

		return new Object[]{modelData,ret};
	}

	public static void populateAnimatedModel(URL url, List<Object3DData> datas, AnimatedModelData modelData){

		for (int i=0; i<datas.size(); i++) {
			Object3DData data = datas.get(i);

			// Parse all facets...
			double[] normal = new double[3];
			double[][] vertices = new double[3][3];
			int normalCounter = 0, vertexCounter = 0;

			FloatBuffer normalsBuffer = data.getVertexNormalsArrayBuffer();
			FloatBuffer vertexBuffer = data.getVertexArrayBuffer();
			IntBuffer indexBuffer = data.getDrawOrder();

			WavefrontLoader.ModelDimensions modelDimensions = data.getDimensions();

			MeshData meshData = modelData.getMeshData().get(i);

			boolean first = true;
			for (int counter = 0; counter < meshData.getVertices().length - 3; counter += 3) {

				// update model dimensions
				if (first) {
					modelDimensions.set(meshData.getVertices()[counter], meshData.getVertices()[counter + 1], meshData.getVertices()[counter + 2]);
					first = false;
				}
				modelDimensions.update(meshData.getVertices()[counter], meshData.getVertices()[counter + 1], meshData.getVertices()[counter + 2]);

			}

			Log.i("ColladaLoaderTask", "Building 3D object '"+meshData.getId()+"'...");
			data.setId(meshData.getId());
			vertexBuffer.put(meshData.getVertices());
			normalsBuffer.put(meshData.getNormals());
			data.setVertexColorsArrayBuffer(meshData.getColorsBuffer());
			indexBuffer.put(meshData.getIndices());
			data.setFaces(new WavefrontLoader.Faces(vertexBuffer.capacity() / 3));
			data.setDrawOrder(indexBuffer);

			// Load skeleton and animation
			AnimatedModel data3D = (AnimatedModel) data;
			try {

				// load skeleton
				SkeletonData skeletonData = modelData.getJointsData();
				Joint headJoint = createJoints(skeletonData.headJoint);
				data3D.setRootJoint(headJoint, skeletonData.jointCount, skeletonData.boneCount, false);

				// load animation
				Animation animation = loadAnimation(url.openStream());
				data3D.doAnimation(animation);

			} catch (Exception e) {
				Log.e("ColladaLoader", "Problem loading model animation' " + e.getMessage(), e);
				data3D.doAnimation(null);
			}
		}
	}

	public static AnimatedModelData loadColladaModel(InputStream colladaFile, int maxWeights) {
		XmlNode node = null;
		Map<String,SkinningData> skinningData = null;
		SkeletonData jointsData = null;
		try {
			node = XmlParser.parse(colladaFile);

			SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
			skinningData = skinLoader.extractSkinData();

			if (!skinningData.isEmpty()) {
				SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.values().iterator().next());
				jointsData = jointsLoader.extractBoneData();
			}

		}catch(Exception ex){
			Log.e("ColladaLoader","Problem loading skinning/skeleton data",ex);
		}

		Log.i("ColladaLoader","Extracting geometry...");
		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), node.getChild("library_materials"),
				node.getChild("library_effects"), node.getChild("library_images"), skinningData, jointsData);
		List<MeshData> meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 *
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform, data.inverseBindTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	static AnimationData loadColladaAnimation(InputStream colladaFile) {
		XmlNode node = XmlParser.parse(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

	/**
	 * Loads up a collada animation file, and returns and animation created from
	 * the extracted animation data from the file.
	 *
	 * @param colladaFile
	 *            - the collada file containing data about the desired
	 *            animation.
	 * @return The animation made from the data in the file.
	 */
	public static Animation loadAnimation(InputStream colladaFile) {
		AnimationData animationData = loadColladaAnimation(colladaFile);
		KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
		for (int i = 0; i < frames.length; i++) {
			frames[i] = createKeyFrame(animationData.keyFrames[i]);
		}
		return new Animation(animationData.lengthSeconds, frames);
	}

	/**
	 * Creates a keyframe from the data extracted from the collada file.
	 *
	 * @param data
	 *            - the data about the keyframe that was extracted from the
	 *            collada file.
	 * @return The keyframe.
	 */
	private static KeyFrame createKeyFrame(KeyFrameData data) {
		Map<String, JointTransform> map = new HashMap<>();
		for (JointTransformData jointData : data.jointTransforms) {
			JointTransform jointTransform = new JointTransform(jointData.jointLocalTransform);
			map.put(jointData.jointNameId, jointTransform);
		}
		return new KeyFrame(data.time, map);
	}
}
