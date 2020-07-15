package org.andresoviedo.android_3d_model_engine.services.collada;

import android.opengl.GLES20;
import androidx.annotation.NonNull;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoadListener;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.AnimationLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.GeometryLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.MaterialLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.SkeletonLoader;
import org.andresoviedo.android_3d_model_engine.services.collada.loader.SkinLoader;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.xml.XmlNode;
import org.andresoviedo.util.xml.XmlParser;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ColladaLoader {

    /**
     * @param is file stream
     * @return all the texture files found in the file
     */
    public static List<String> getImages(InputStream is){
        try{
            final XmlNode xml = XmlParser.parse(is);
            return new MaterialLoader(xml.getChild("library_materials"),
                    xml.getChild("library_effects"),xml.getChild("library_images")).getImages();
        }catch(Exception ex){
            Log.e("ColladaLoaderTask", "Error loading materials",ex);
            return null;
        }
    }

    @NonNull
    public List<Object3DData> load(URI uri, LoadListener callback) {
        final List<Object3DData> ret = new ArrayList<>();
        final List<MeshData> allMeshes = new ArrayList<>();

        try (InputStream is = ContentUtils.getInputStream(uri)){

            Log.i("ColladaLoaderTask","Parsing file... "+uri.toString());
            callback.onProgress("Loading file...");
            final XmlNode xml = XmlParser.parse(is);


            // load visual scene
            // we need this first in order to progressively load geometries with it's binded transform
            Log.i("ColladaLoaderTask", "--------------------------------------------------");
            Log.i("ColladaLoaderTask", "Loading visual scene...");
            Log.i("ColladaLoaderTask", "--------------------------------------------------");
            callback.onProgress("Loading visual scene...");
            SkeletonData jointsData = null;
            try {
                // load joints
                SkeletonLoader jointsLoader = new SkeletonLoader(xml);
                jointsData = jointsLoader.loadJoints();

            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading visual scene", ex);
            }


            // load geometries
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            Log.i("ColladaLoaderTask", "Loading geometries...");
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            callback.onProgress("Loading geometries...");
            List<MeshData> meshDatas = null;
            try {
                GeometryLoader g = new GeometryLoader(xml.getChild("library_geometries"));
                List<XmlNode> geometries = xml.getChild("library_geometries").getChildren("geometry");
                meshDatas = new ArrayList<>();
                for (int i=0; i<geometries.size(); i++) {

                    // alert user if loading several meshes
                    if (geometries.size() > 1) {
                        callback.onProgress("Loading geometries... " + (i+1) + " / " + geometries.size());
                    }

                    // load next mesh
                    XmlNode geometry = geometries.get(i);
                    MeshData meshData = g.loadGeometry(geometry);
                    if (meshData == null) continue;
                    meshDatas.add(meshData);
                    allMeshes.add(meshData);

                    // create 3D Model
                    AnimatedModel data3D = new AnimatedModel(meshData.getVertexBuffer(), null);
                    data3D.setId(meshData.getId());
                    data3D.setVertexBuffer(meshData.getVertexBuffer());
                    data3D.setNormalsBuffer(meshData.getNormalsBuffer());
                    data3D.setColorsBuffer(meshData.getColorsBuffer());
                    data3D.setElements(meshData.getElements());
                    //data3D.setDimensions(meshData.getDimension());
                    data3D.setDrawMode(GLES20.GL_TRIANGLES);
                    data3D.setDrawUsingArrays(false);

                    // bind transform
                    if (jointsData != null){
                        // TODO: we may have several instances - should be clone all of them here?
                        final JointData jointData = jointsData.find(meshData.getId());
                        if (jointData != null) {
                            data3D.setName(jointData.getName());
                            data3D.setBindTransform(jointData.getBindTransform());
                        }
                    }

                    callback.onLoad(data3D);
                    ret.add(data3D);
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading geometries",ex);
                return Collections.emptyList();
            }

            // load materials
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            Log.i("ColladaLoaderTask", "Loading materials...");
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            callback.onProgress("Loading materials...");
            try{
                final MaterialLoader materialLoader = new MaterialLoader(xml.getChild("library_materials"),
                        xml.getChild("library_effects"),xml.getChild("library_images"));
                for (int i=0; i<meshDatas.size(); i++){
                    final MeshData meshData = meshDatas.get(i);
                    final Object3DData data3D = ret.get(i);

                    // load material for mesh
                    materialLoader.loadMaterial(meshData);
                    data3D.setTextureBuffer(meshData.getTextureBuffer());
                }
            }catch(Exception ex){
                Log.e("ColladaLoaderTask", "Error loading materials",ex);
            }


            // load visual scene
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            Log.i("ColladaLoaderTask", "Loading visual scene...");
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            callback.onProgress("Loading visual scene...");
            //SkeletonData jointsData = null;
            try {
                // load joints
                //SkeletonLoader jointsLoader = new SkeletonLoader(xml);
                //jointsData = jointsLoader.loadJoints();

                // bind instance materials
                final MaterialLoader materialLoader = new MaterialLoader(xml.getChild("library_materials"),
                        xml.getChild("library_effects"),xml.getChild("library_images"));
                for (int i=0; i<meshDatas.size(); i++) {
                    final MeshData meshData = meshDatas.get(i);
                    final Object3DData data3D = ret.get(i);
                    materialLoader.loadMaterialFromVisualScene(meshData, jointsData);
                }

                // bind instance geometries
                if (jointsData != null) {

                    // log event
                    Log.d("ColladaLoaderTask", "Loading instance geometries...");

                    // clone & bind meshes
                    for (int i = 0; i < meshDatas.size(); i++) {
                        final MeshData meshData = meshDatas.get(i);
                        final AnimatedModel data3D = (AnimatedModel) ret.get(i);

                        // no joint linked to geometry - just draw as it is
                        List<JointData> allJointData = jointsData.getHeadJoint().findAll(meshData.getId());
                        if (allJointData.isEmpty()) {
                            Log.d("ColladaLoaderTask", "No joint linked to mesh: " + meshData.getId());
                            continue;
                        }

                        // found 1 joint linked to geometry - update matrix
                        if (allJointData.size() == 1) {
                            // Log.d("ColladaLoaderTask", "Found 1 single instance for mesh: " + meshData.getId());
                            final JointData jointData = allJointData.get(0);
                            // FIXME: set this only if not animated
                            data3D.setBindTransform(jointData.getBindTransform());
                            continue;
                        }

                        // found several mesh instances - draw them all
                        Log.i("ColladaLoaderTask", "Found multiple instances for mesh: " + meshData.getId()+". Total: "+allJointData.size());
                        boolean isOriginalMeshConfigured = false;
                        for (JointData jd : allJointData) {

                            // update matrix for original mesh
                            if (!isOriginalMeshConfigured){
                                data3D.setBindTransform(jd.getBindTransform());
                                isOriginalMeshConfigured = true;
                                continue;
                            }

                            Log.i("ColladaLoaderTask", "Cloning mesh for joint: " + jd.getName());
                            final AnimatedModel instance_geometry = data3D.clone();
                            instance_geometry.setId(data3D.getId() + "_instance_" + jd.getName());
                            // FIXME: set this only if not animated
                            instance_geometry.setBindTransform(jd.getBindTransform());

                            callback.onLoad(instance_geometry);
                            ret.add(instance_geometry);
                            allMeshes.add(meshData.clone());
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading visual scene",ex);
            }


            // load materials
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            Log.i("ColladaLoaderTask", "Loading textures...");
            Log.i("ColladaLoaderTask","--------------------------------------------------");
            callback.onProgress("Loading textures...");
            try{
                for (int i=0; i<meshDatas.size(); i++){
                    final MeshData meshData = meshDatas.get(i);
                    for (int e=0; e<meshData.getElements().size(); e++){
                        final Element element = meshData.getElements().get(e);
                        if (element.getMaterial() != null && element.getMaterial().getTextureFile() != null){
                            final String textureFile = element.getMaterial().getTextureFile();
                            // log event
                            Log.i("ColladaLoaderTask", "Reading texture file... " + textureFile);

                            // read texture data
                            try (InputStream stream = ContentUtils.getInputStream(textureFile)) {

                                // read data
                                element.getMaterial().setTextureData(IOUtils.read(stream));

                                // log event
                                Log.i("ColladaLoaderTask", "Texture linked... " + element.getMaterial().getTextureData().length+" (bytes)");

                            } catch (Exception ex) {
                                Log.e("ColladaLoaderTask", String.format("Error reading texture file: %s", ex.getMessage()));
                            }
                        }
                    }
                }
            }catch(Exception ex){
                Log.e("ColladaLoaderTask", "Error loading materials",ex);
            }


            // load skinning data
            Map<String, SkinningData> skinningData = null;
            try {
                XmlNode library_controllers = xml.getChild("library_controllers");
                if (library_controllers != null && !library_controllers.getChildren("controller").isEmpty()) {

                    // log event
                    Log.i("ColladaLoaderTask","--------------------------------------------------");
                    Log.i("ColladaLoaderTask", "Loading skinning data...");
                    Log.i("ColladaLoaderTask","--------------------------------------------------");

                    // notify user
                    callback.onProgress("Loading skinning data...");

                    // load skin data
                    SkinLoader skinLoader = new SkinLoader(library_controllers, 3);
                    skinningData = skinLoader.loadSkinData();
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading skinning data",ex);
            }

            final AnimationLoader loader = new AnimationLoader(xml);

            // finish skinning + joint update

            try {
                if (loader.isAnimated()) {

                    // log event
                    Log.i("ColladaLoaderTask", "--------------------------------------------------");
                    Log.i("ColladaLoaderTask", "Loading joints...");
                    Log.i("ColladaLoaderTask", "--------------------------------------------------");

                    // notify user
                    callback.onProgress("Loading joints...");

                    // update joint indices
                    // - skinning needs joint indices because auto-generated skinning relay on joint indices
                    // - skeleton needs skinning because joint index depend on bone order specified in skinning data
                    SkeletonLoader skeletonLoader = new SkeletonLoader(xml);
                    skeletonLoader.updateJointData(jointsData.getHeadJoint(), skinningData, jointsData);

                    for (int i = 0; i < allMeshes.size(); i++) {
                        final MeshData meshData = allMeshes.get(i);
                        final AnimatedModel data3D = (AnimatedModel) ret.get(i);

                        // initialize jointIds and vertex weights array
                        SkinLoader.loadSkin(meshData, skinningData, jointsData);

                        data3D.setBindShapeMatrix(meshData.getBindShapeMatrix());

                        data3D.setJointIds(meshData.getJointsBuffer());
                        data3D.setVertexWeights(meshData.getWeightsBuffer());
                    }
                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error updating joint data",ex);
            }

            //if (true) return ret;

            // parse animation
            try {
                if (loader.isAnimated()) {

                    // log event
                    Log.i("ColladaLoaderTask","--------------------------------------------------");
                    Log.i("ColladaLoaderTask","Loading animation... ");
                    Log.i("ColladaLoaderTask","--------------------------------------------------");

                    // notify user
                    callback.onProgress("Loading animation...");

                    // load animation
                    final Animation animation = loader.load();

                    for (int i = 0; i < allMeshes.size(); i++) {
                        final MeshData meshData = allMeshes.get(i);
                        final AnimatedModel data3D = (AnimatedModel) ret.get(i);

                        data3D.setJointsData(jointsData);
                        data3D.doAnimation(animation);

                        // FIXME: this should be handled differently
                        data3D.setBindTransform(null);
                    }

                }
            } catch (Exception ex) {
                Log.e("ColladaLoaderTask", "Error loading animation",ex);
            }

            // log event
            if (ret.isEmpty()){
                Log.e("ColladaLoaderTask","Mesh data list empty. Did you exclude any model in GeometryLoader.java?");
            }
            Log.i("ColladaLoaderTask","Loading model finished. Objects: "+ret.size());

        }catch(Exception ex){
            Log.e("ColladaLoaderTask","Problem loading model",ex);
        }
        return ret;
    }

}
