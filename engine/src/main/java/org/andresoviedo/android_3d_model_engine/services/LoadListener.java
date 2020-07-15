package org.andresoviedo.android_3d_model_engine.services;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;

public interface LoadListener {

    void onStart();

    void onProgress(String progress);

    void onLoadError(Exception ex);

    void onLoad(Object3DData data);

    void onLoadComplete();
}
