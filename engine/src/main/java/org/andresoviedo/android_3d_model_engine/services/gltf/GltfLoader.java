package org.andresoviedo.android_3d_model_engine.services.gltf;

import android.util.Log;

import androidx.annotation.NonNull;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoadListener;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.util.android.ContentUtils;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class GltfLoader {

    @NonNull
    public List<Object3DData> load(URI uri, LoadListener callback) {

        final List<Object3DData> ret = new ArrayList<>();
        final List<MeshData> allMeshes = new ArrayList<>();

        try (InputStream is = ContentUtils.getInputStream(uri)) {

            Log.i("GltfLoaderTask", "Parsing file... " + uri.toString());
            callback.onProgress("Loading file...");

            // gltf ...

            /*GltfAssetReader gltfAssetReader = new GltfAssetReader();
            GltfAsset gltfAsset = gltfAssetReader.readWithoutReferences(is);
            URI baseUri = IO.getParent(uri);
            GltfReferenceResolver.resolveAll(
                    gltfAsset.getReferences(), baseUri);
            GltfModel gltfModel = GltfModels.create(gltfAsset);
            BufferModel bufferModel = gltfModel.getBufferModels().get(0);
            <gltfModel.getMeshModels()>

            callback.onProgress("Loading Buffers...");*/

        } catch (Exception ex) {
            Log.e("ColladaLoaderTask", "Problem loading model", ex);
        }
        return ret;
    }

}
