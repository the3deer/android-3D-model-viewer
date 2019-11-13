package org.andresoviedo.android_3d_model_engine.services.collada.loader;


import android.opengl.GLES20;
import android.opengl.Matrix;
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
		Log.i("ColladaLoader","Loading model... "+url.toString());
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

			// notify succeded!
			AnimatedModel data3D = new AnimatedModel(vertexBuffer);
			data3D.setVertexBuffer(vertexBuffer);
			data3D.setVertexNormalsBuffer(meshData.getNormalsBuffer());
			data3D.setVertexNormalsArrayBuffer(normalsBuffer);
			data3D.setTextureFile(meshData.getTexture());
			if (meshData.getTextureCoords() != null) {
				int totalTextures = meshData.getTextureCoords().length;
				FloatBuffer textureBuffer = createNativeByteBuffer(totalTextures * 4).asFloatBuffer();
				textureBuffer.put(meshData.getTextureCoords());
				data3D.setTextureCoordsArrayBuffer(textureBuffer);
			}
			data3D.setColor(meshData.getColor());
			data3D.setVertexColorsArrayBuffer(meshData.getColorsBuffer());
			data3D.setDimensions(new WavefrontLoader.ModelDimensions());
			data3D.setDrawOrder(indexBuffer);
			data3D.setDrawMode(GLES20.GL_TRIANGLES);

			if (meshData.getJointIds() != null) {
				//Log.v("ColladaLoader","joint: "+ Arrays.toString(meshData.getJointIds()));
				FloatBuffer intBuffer = createNativeByteBuffer(meshData.getJointIds().length * 4).asFloatBuffer();
				for (int i : meshData.getJointIds()) {
					intBuffer.put(i);
				}
				data3D.setJointIds(intBuffer);
			}
			if (meshData.getVertexWeights() != null) {
				//Log.v("ColladaLoader","weights: "+ Arrays.toString(meshData.getVertexWeights()));
				FloatBuffer floatBuffer = createNativeByteBuffer(meshData.getVertexWeights().length * 4).asFloatBuffer();
				floatBuffer.put(meshData.getVertexWeights());
				data3D.setVertexWeights(floatBuffer);
			}
			ret.add(data3D);
		}

		if (meshDataList.isEmpty()){
            Log.w("ColladaLoader","Mesh data list empty. Did you exclude any model in GeometryLoader.java?");
        }
		Log.i("ColladaLoader","Loading model finished. Objects: "+meshDataList.size());
		return new Object[]{modelData,ret};
	}

	public static void populateAnimatedModel(URL url, List<Object3DData> datas, AnimatedModelData modelData){

        Log.i("ColladaLoader", "Loading animation...");
        Animation animation = null;
        try (InputStream animationIS = url.openStream()) {
            animation = loadAnimation(animationIS);
			Log.i("ColladaLoader", "Loaded animation: "+animation);
        } catch (Exception e) {
            Log.e("ColladaLoader","Error loading animation", e);
        }

		SkeletonData skeletonData = modelData.getJointsData();
		Joint rootJoint = null;
		if (skeletonData != null){
			Log.i("ColladaLoader", "Building joints... nodes: "+skeletonData.getJointCount());
			rootJoint = skeletonData.buildJoints();
			float[] parentTransform = new float[16];
			Matrix.setIdentityM(parentTransform,0);
			rootJoint.calcInverseBindTransform(parentTransform, false);
		} else {
			Log.d("ColladaLoader", "No skeleton data");
		}

		Log.i("ColladaLoader","Loading objects... "+datas.size());
		for (int i=0; i<datas.size(); i++) {
			Object3DData data = datas.get(i);

			MeshData meshData = modelData.getMeshData().get(i);
            Log.v("ColladaLoader","Loading data... "+meshData.getId());


            // FIXME: this is overwritten when setFaces called
			IntBuffer indexBuffer = data.getDrawOrder();

			WavefrontLoader.ModelDimensions modelDimensions = data.getDimensions();
			boolean first = true;
            float[] vertices = meshData.getVertices();
            for (int counter = 0; counter < vertices.length - 3; counter += 3) {
				if (first) {
					modelDimensions.set(vertices[counter], vertices[counter + 1], vertices[counter + 2]);
					first = false;
				}
				modelDimensions.update(vertices[counter], vertices[counter + 1], vertices[counter + 2]);
			}

			Log.v("ColladaLoaderTask", "Loading buffers...'");
			data.setId(meshData.getId());
			data.getVertexArrayBuffer().put(vertices);
			data.getVertexNormalsArrayBuffer().put(meshData.getNormalsArray());
			data.setVertexColorsArrayBuffer(meshData.getColorsBuffer());
			indexBuffer.put(meshData.getIndices());
			data.setFaces(new WavefrontLoader.Faces(data.getVertexArrayBuffer().capacity() / 3));
			data.setDrawOrder(indexBuffer);

			// Load skeleton and animation
			AnimatedModel data3D = (AnimatedModel) data;
			try {

				// load skeleton
				if (rootJoint != null){
					data3D.setRootJoint(rootJoint, skeletonData.getJointCount(), skeletonData.getBoneCount());
					JointData jointData = rootJoint.find(meshData.getId());
					if (jointData != null) {
						// we must set bind shape matrix only for joints
						// as we don't want to disturb Animator when querying for...
						data3D.setBindShapeMatrix(jointData.getBindTransform());
					}

					// only animate if there is are joints
					data3D.doAnimation(animation);
				} else {
					Log.d("ColladaLoader", "No skeleton data for "  + meshData.getId());
				}
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

			XmlNode library_controllers = node.getChild("library_controllers");
			if (library_controllers != null) {
				SkinLoader skinLoader = new SkinLoader(library_controllers, maxWeights);
				skinningData = skinLoader.extractSkinData();
			}

			SkeletonLoader jointsLoader = new SkeletonLoader(node, skinningData);
			jointsData = jointsLoader.extractBoneData();

		}catch(Exception ex){
			Log.e("ColladaLoader","Problem loading skinning/skeleton data",ex);
		}

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), node.getChild("library_materials"),
				node.getChild("library_effects"), node.getChild("library_images"), skinningData, jointsData);
		List<MeshData> meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData, skinningData);
	}


	static AnimationData loadColladaAnimation(InputStream colladaFile) {
		XmlNode node = XmlParser.parse(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		if (animNode == null) return null;
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
		if (animationData == null) return null;
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
