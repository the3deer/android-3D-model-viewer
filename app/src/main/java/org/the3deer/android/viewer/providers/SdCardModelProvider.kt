package org.the3deer.android.viewer.providers

import android.Manifest
import android.app.Activity
import org.the3deer.android.viewer.providers.ModelProvider
import org.the3deer.android.viewer.util.AndroidUtils
import org.the3deer.android.viewer.util.ContentUtils
import org.the3deer.android.viewer.util.FileUtils
import java.net.URI

class SdCardModelProvider : ModelProvider {

    companion object {
        const val SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)"
        const val REQUEST_READ_EXTERNAL_STORAGE = 1000
    }

    override fun list(): Any {
        return emptyList<URI>() // Not easily listable recursively
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            FileUtils.createChooserDialog(activity, "Select file", null, null, SUPPORTED_FILE_TYPES_REGEX) { file ->
                if (file != null) {
                    ContentUtils.setCurrentDir(file.parentFile)
                    callback.onModelSelected(URI.create("file://" + file.absolutePath))
                } else {
                    callback.onModelSelected(null)
                }
            }
        }
    }

    override fun resolve(id: String): URI? {
        return try {
            URI.create("file://$id")
        } catch (e: Exception) {
            null
        }
    }
}
