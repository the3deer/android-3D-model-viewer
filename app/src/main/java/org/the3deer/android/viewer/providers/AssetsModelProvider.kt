package org.the3deer.android.viewer.providers

import android.app.Activity
import android.app.Application
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.util.AssetUtils
import org.the3deer.android.viewer.util.ContentUtils
import java.net.URI

internal class AssetsModelProvider(private val application: Application) : ModelProvider {

    companion object {
        const val SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)"
    }

    override fun list(): List<URI> {
        val uris = mutableListOf<URI>()
        try {
            val list = application.assets.list("models")
            if (list != null) {
                for (file in list) {
                    uris.add(URI.create("android://" + application.packageName + "/assets/models/" + file.replace(" ", "+")))
                }
            }
        } catch (e: Exception) {
            // ignore
        }
        return uris
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        AssetUtils.createChooserDialog(activity, "Select file", null, "models", SUPPORTED_FILE_TYPES_REGEX) { file ->
            if (file != null) {
                ContentUtils.provideAssets(activity)
                callback.onModelSelected(resolve(URI.create("android://" + application.packageName + "/assets/" + file.replace(" ", "+"))))
            } else {
                callback.onModelSelected(null)
            }
        }
    }

    override fun resolve(uri: URI): Model {
        val model = Model(uri)
        val path = uri.path
        model.name = path.substringAfterLast("/")
        model.type = path.substringAfterLast(".", "gltf")
        model.provider = "Assets"
        model.license = "Community / Open Source"
        return model
    }
}
