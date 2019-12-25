package org.andresoviedo.android_3d_model_engine.services.collada;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.animation.JointTransform;
import org.andresoviedo.android_3d_model_engine.animation.KeyFrame;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.AnimationData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Joint;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointTransformData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.AnimationLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.GeometryLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.SkeletonLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.SkinLoader;
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

public class ColladaLoaderTask extends LoaderTask {

    public ColladaLoaderTask(Activity parent, Uri uri, Callback callback) {
        super(parent, uri, callback);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<Object3DData> build() throws IOException {

        List<Object3DData> ret = new ArrayList<>();
        try (InputStream is = new URL(uri.toString()).openStream()){

            Log.i("ColladaLoaderTask","Parsing file... "+uri.toString());
            super.publishProgress("Parsing file...");
            XmlNode node = XmlParser.parse(is);

            Animation animation = null;
            try {
                Log.i("ColladaLoaderTask", "Loading animation...");
                super.publishProgress("Loading animation...");
                XmlNode animNode = node.getChild("library_animations");
                if (animNode != null) {
                    XmlNode jointsNode = node.getChild("library_visual_scenes");
                    AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
                    AnimationData animationData = loader.extractAnimation();
                    if (animationData != null) {
                        KeyFrame[] frames = new KeyFrame[animationData.keyFrames.length];
                        for (int i = 0; i < frames.length; i++) {
                            Map<String, JointTransform> map = new HashMap<>();
                            for (JointTransformData jointData : animationData.keyFrames[i].jointTransforms) {
                                JointTransform jointTransform = new JointTransform(jointData.jointLocalTransform);
                                map.put(jointData.jointNameId, jointTransform);
                            }
                            frames[i] = new KeyFrame(animationData.keyFrames[i].time, map);
                        }
                        animation = new Animation(animationData.lengthSeconds, frames);
                        Log.i("ColladaLoaderTask", "Loaded animation: " + animation);
                    }
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading animation",ex);
            }

            Map<String,SkinningData> skinningData = null;
            try {
                Log.i("ColladaLoaderTask", "Loading skinning data...");
                super.publishProgress("Loading skinning data...");
                XmlNode library_controllers = node.getChild("library_controllers");
                if (library_controllers != null) {
                    SkinLoader skinLoader = new SkinLoader(library_controllers, 3);
                    skinningData = skinLoader.extractSkinData();
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading skinning data",ex);
            }

            Joint rootJoint = null;
            SkeletonData jointsData = null;
            try {
                Log.i("ColladaLoaderTask", "Loading skeleton data...");
                super.publishProgress("Loading skeleton data...");
                SkeletonLoader jointsLoader = new SkeletonLoader(node, skinningData);
                jointsData = jointsLoader.extractBoneData();
                if (jointsData != null){
                    Log.i("ColladaLoaderTask", "Building joints... nodes: "+jointsData.getJointCount());
                    rootJoint = jointsData.buildJoints();
                } else {
                    Log.d("ColladaLoaderTask", "No skeleton data");
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading skeleton data",ex);
            }

            Log.i("ColladaLoaderTask", "Loading geometries...");
            super.publishProgress("Loading geometries...");
            GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), node.getChild("library_materials"),
                    node.getChild("library_effects"), node.getChild("library_images"), skinningData, jointsData,
                    animation != null);
            List<XmlNode> geometries = node.getChild("library_geometries").getChildren("geometry");
            List<MeshData> meshDatas = new ArrayList<>();
            for (int i=0; i<geometries.size(); i++) {
                if (geometries.size() > 1) {
                    super.publishProgress("Loading geometries... " + (i+1) + " / " + geometries.size());
                }
                XmlNode geometry = geometries.get(i);
                MeshData meshData = g.loadGeometry(geometry);
                if (meshData == null) continue;

                meshDatas.add(meshData);
                int totalVertex = meshData.getVertexCount();

                // Allocate data
                FloatBuffer normalsBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
                FloatBuffer vertexBuffer = createNativeByteBuffer(totalVertex * 3 * 4).asFloatBuffer();
                int[] indices = meshData.getIndices();
                IntBuffer indexBuffer = createNativeByteBuffer(indices.length * 4).asIntBuffer();

                // Initialize model dimensions (needed by the Object3DData#scaleCenter()

                // notify succeded!
                AnimatedModel data3D = new AnimatedModel(vertexBuffer, indexBuffer);
                data3D.setVertexBuffer(vertexBuffer);
                data3D.setVertexNormalsBuffer(normalsBuffer);
                // data3D.setVertexNormalsArrayBuffer(normalsBuffer);
                data3D.setVertexColorsBuffer(meshData.getColorsBuffer());
                data3D.setTextureFile(meshData.getTexture());
                if (meshData.getTextureCoords() != null) {
                    int totalTextures = meshData.getTextureCoords().length;
                    FloatBuffer textureBuffer = createNativeByteBuffer(totalTextures * 4).asFloatBuffer();
                    textureBuffer.put(meshData.getTextureCoords());
                    data3D.setTextureCoordsArrayBuffer(textureBuffer);
                }
                data3D.setColor(meshData.getColor());
                data3D.setVertexColorsBuffer(meshData.getColorsBuffer());
                data3D.setDimensions(new WavefrontLoader.ModelDimensions());
                data3D.setDrawOrder(indexBuffer);
                data3D.setDrawMode(GLES20.GL_TRIANGLES);

                if (meshData.getJointIds() != null) {
                    //Log.v("ColladaLoaderTask","joint: "+ Arrays.toString(meshData.getJointIds()));
                    FloatBuffer intBuffer = createNativeByteBuffer(meshData.getJointIds().length * 4).asFloatBuffer();
                    for (int j : meshData.getJointIds()) {
                        intBuffer.put(j);
                    }
                    data3D.setJointIds(intBuffer);
                }
                if (meshData.getVertexWeights() != null) {
                    //Log.v("ColladaLoaderTask","weights: "+ Arrays.toString(meshData.getVertexWeights()));
                    FloatBuffer floatBuffer = createNativeByteBuffer(meshData.getVertexWeights().length * 4).asFloatBuffer();
                    floatBuffer.put(meshData.getVertexWeights());
                    data3D.setVertexWeights(floatBuffer);
                }

                WavefrontLoader.ModelDimensions modelDimensions = data3D.getDimensions();
                boolean first = true;
                float[] vertices = meshData.getVertices();
                for (int counter = 0; counter < indices.length; counter ++) {
                    int index = indices[counter];
                    int offset = index * 3;
                    if (index < 0 || offset > vertices.length){
                        Log.e("ColladaLoaderTask","Index out of range. "+index);
                        continue;
                    }
                    if (first) {
                        modelDimensions.set(vertices[offset], vertices[offset +1], vertices[offset +2]);
                        first = false;
                    }
                    modelDimensions.update(vertices[offset], vertices[offset +1], vertices[offset+2]);
                }

                Log.v("ColladaLoaderTask", "Loading buffers...'");
                data3D.setId(meshData.getId());
                data3D.getVertexBuffer().put(vertices);
                data3D.getVertexNormalsBuffer().put(meshData.getNormalsArray());
                indexBuffer.put(indices);
                if (data3D.getVertexArrayBuffer() != null) {
                    data3D.setFaces(new WavefrontLoader.Faces(data3D.getVertexArrayBuffer().capacity() / 3));
                } else {
                    data3D.setFaces(new WavefrontLoader.Faces(data3D.getDrawOrder().capacity() / 3));
                }
                data3D.setDrawOrder(indexBuffer);
                // FIXME: this does work?
                data3D.setDrawUsingArrays(false);

                if (rootJoint != null){
                    data3D.setRootJoint(rootJoint, jointsData.getJointCount(), jointsData.getBoneCount());
                    JointData jointData = rootJoint.find(meshData.getId());
                    if (jointData != null) {
                        // we must set bind shape matrix only for joints
                        // as we don't want to disturb Animator when querying for...
                        data3D.setBindShapeMatrix(jointData.getBindTransform());
                    }

                    // only animate if there is are joints
                    data3D.doAnimation(animation);
                } else {
                    Log.d("ColladaLoaderTask", "No skeleton data for "  + meshData.getId());
                }

                super.onLoad(data3D);
                ret.add(data3D);
            }

            if (meshDatas.isEmpty()){
                Log.i("ColladaLoaderTask","Mesh data list empty. Did you exclude any model in GeometryLoader.java?");
            }
            Log.i("ColladaLoaderTask","Loading model finished. Objects: "+meshDatas.size());

            // rescale
            if (ret.size() == 1) {
                //ret.get(0).centerAndScale(5, new float[]{0, 0, 0});
            } else {
               // Object3DData.centerAndScale(ret, 5, new float[]{0, 0, 0});
            }

        }catch(Exception ex){
            Log.e("ColladaLoaderTask","Problem loading skinning/skeleton data",ex);
        }
        return ret;
    }

    private static ByteBuffer createNativeByteBuffer(int length) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(length);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        return bb;
    }
}