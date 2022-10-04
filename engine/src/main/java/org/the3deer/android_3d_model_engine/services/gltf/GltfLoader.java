package org.the3deer.android_3d_model_engine.services.gltf;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import androidx.annotation.NonNull;

import org.the3deer.android_3d_model_engine.model.Material;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.services.LoadListener;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.GLUtil;
import org.the3deer.util.io.IOUtils;
import org.the3deer.util.math.Math3DUtils;

import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.GltfModels;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.io.ByteBufferInputStream;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.GltfReferenceResolver;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v2.MaterialModelV2;

public final class GltfLoader {

    @NonNull
    public List<Object3DData> load(URI uri, LoadListener callback) {

        callback.onProgress("Loading file...");

        final List<Object3DData> ret = new ArrayList<>();
        // final List<MeshData> allMeshes = new ArrayList<>();

        try (InputStream is = ContentUtils.getInputStream(uri)) {

            Log.i("Gltfloader", "Loading model file... " + uri);
            callback.onProgress("Loading " + uri);

            // gltf ...
            GltfAssetReader gltfAssetReader = new GltfAssetReader();
            GltfAsset gltfAsset = gltfAssetReader.readWithoutReferences(is);
            URI baseUri = IO.getParent(uri);
            GltfReferenceResolver.resolveAll(gltfAsset.getReferences(), baseUri);
            GltfModel gltfModel = GltfModels.create(gltfAsset);

            // load scene...
            Log.d("Gltfloader", "Loading scene...");
            for (SceneModel sceneModel : gltfModel.getSceneModels()) {

                // load nodes...
                Log.d("Gltfloader", "Loading scene...");
                for (NodeModel nodeModel : sceneModel.getNodeModels()) {

                    Log.d("Gltfloader", "Loading nodes...");
                    callback.onProgress("Loading nodes...");

                    loadNodeModel(callback, ret, Math3DUtils.IDENTITY_MATRIX, nodeModel);
                }
            }

        } catch (Exception ex) {
            Log.e("Gltfloader", "Problem loading model", ex);
        }
        return ret;
    }

    private void loadNodeModel(LoadListener callback, List<Object3DData> ret,
                               float[] bindTransform, NodeModel nodeModel) {

        for (MeshModel meshModel : nodeModel.getMeshModels()) {
            loadMeshModel(callback, ret, bindTransform, nodeModel, meshModel);
        }

        for (NodeModel childNode : nodeModel.getChildren()) {
            float[] bindLocalTransform = bindTransform;
            if (nodeModel.getMatrix() != null) {
                bindLocalTransform = new float[16];
                Matrix.multiplyMM(bindLocalTransform, 0, bindTransform, 0, nodeModel.getMatrix(), 0);
            }
            loadNodeModel(callback, ret, bindLocalTransform, childNode);
        }
    }

    private void loadMeshModel(LoadListener callback, List<Object3DData> ret,
                               float[] bindTransform, NodeModel nodeModel, MeshModel meshModel) {
        List<MeshPrimitiveModel> meshPrimitiveModels = meshModel.getMeshPrimitiveModels();

        Log.d("Gltfloader", "Loading mesh primitives...");
        callback.onProgress("Loading mesh primitives...");


        for (MeshPrimitiveModel meshPrimitiveModel : meshPrimitiveModels) {

            Object3DData model = loadMeshPrimitive(meshModel, meshPrimitiveModel);


            // calculate bind transform
            if (nodeModel.getMatrix() != null) {
                float[] bindLocalTransform = new float[16];
                Matrix.multiplyMM(bindLocalTransform, 0, bindTransform, 0, nodeModel.getMatrix(), 0);
                model.setBindTransform(bindLocalTransform);
            }

            callback.onLoad(model);

            ret.add(model);
        }
    }

    private Object3DData loadMeshPrimitive(MeshModel meshModel, MeshPrimitiveModel meshPrimitiveModel) {
        Log.d("Gltfloader", "Loading mesh primitive...");

        // build model
        final Object3DData model = new Object3DData();

        AccessorModel position = meshPrimitiveModel.getAttributes().get("POSITION");
        FloatBuffer vertexBuffer = position.getAccessorData().createByteBuffer().asFloatBuffer();

        AccessorModel normal = meshPrimitiveModel.getAttributes().get("NORMAL");
        FloatBuffer normalBuffer = normal.getAccessorData().createByteBuffer().asFloatBuffer();

        ShortBuffer drawBuffer_ = meshPrimitiveModel.getIndices().getAccessorData().createByteBuffer().asShortBuffer();
        IntBuffer drawBuffer = IOUtils.createIntBuffer(drawBuffer_.capacity());
        for (int i = 0; i < drawBuffer_.capacity(); i++) {
            drawBuffer.put(drawBuffer_.get(i));
        }

        // build 3d model

        model.setVertexBuffer(vertexBuffer);
        model.setDrawOrder(drawBuffer);
        model.setDrawUsingArrays(false);
        model.setNormalsBuffer(normalBuffer);
        model.setId(meshModel.getName());
        model.setDrawMode(GLES20.GL_TRIANGLES);

        //final Element.Builder elementBuilder = new Element.Builder();

        // parse material
        MaterialModelV2 materialModel = (MaterialModelV2) meshPrimitiveModel.getMaterialModel();
        if (materialModel != null) {
            final Material material = new Material(materialModel.getName());

            // map color
            material.setDiffuse(materialModel.getBaseColorFactor());

            // map texture
            if (materialModel.getBaseColorTexture() != null) {
                ByteBuffer imageData = materialModel.getBaseColorTexture().getImageModel().getImageData();

                Log.i("GltfLoader", "Decoding bitmap... " + materialModel.getBaseColorTexture().getName());
                try {
                    Bitmap bitmap = GLUtil.loadBitmap(new ByteBufferInputStream(imageData));
                    material.setColorTexture(bitmap);
                } catch (Exception e) {
                    Log.i("GltfLoader", "Issue decoding bitmap... " + materialModel.getBaseColorTexture().getName());
                }
            }

            // map normal map
            if (materialModel.getNormalTexture() != null) {
                ByteBuffer imageData = materialModel.getNormalTexture().getImageModel().getImageData();

                Log.i("GltfLoader", "Decoding bitmap... " + materialModel.getNormalTexture().getName());
                try {
                    Bitmap bitmap = GLUtil.loadBitmap(new ByteBufferInputStream(imageData));
                    material.setNormalTexture(bitmap);
                } catch (Exception e) {
                    Log.i("GltfLoader", "Issue decoding bitmap... " + materialModel.getBaseColorTexture().getName());
                }
            }

            // map emmissive map

            // map normal map
            if (materialModel.getEmissiveTexture() != null) {
                ByteBuffer imageData = materialModel.getEmissiveTexture().getImageModel().getImageData();

                Log.i("GltfLoader", "Decoding bitmap... " + materialModel.getEmissiveTexture().getName());
                try {
                    Bitmap bitmap = GLUtil.loadBitmap(new ByteBufferInputStream(imageData));
                    material.setEmissiveTexture(bitmap);
                } catch (Exception e) {
                    Log.i("GltfLoader", "Issue decoding bitmap... " + materialModel.getBaseColorTexture().getName());
                }
            }

            model.setMaterial(material);
        }

        FloatBuffer textureBuffer = null;
        if (meshPrimitiveModel.getAttributes().containsKey("TEXCOORD_0")) {
            AccessorData texture_0 = meshPrimitiveModel.getAttributes().get("TEXCOORD_0").getAccessorData();
            textureBuffer = texture_0.createByteBuffer().asFloatBuffer();
            model.setTextureBuffer(textureBuffer);
        }

        return model;
    }
}
