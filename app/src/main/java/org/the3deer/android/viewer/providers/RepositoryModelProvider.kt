package org.the3deer.android.viewer.providers

import android.app.Activity
import android.widget.Toast
import org.the3deer.android.viewer.MainActivity
import org.the3deer.android.viewer.providers.ModelProvider
import org.the3deer.android.viewer.ui.DialogUtils
import org.the3deer.android.viewer.util.ContentUtils
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

class RepositoryModelProvider(private val repoUrl: URI = REPO_URL) : ModelProvider {
    private val TAG = "RepositoryModelProvider"

    companion object {
        val REPO_URL = URI.create("https://raw.githubusercontent.com/the3deer/android-3D-model-viewer/main/models/index")
        const val SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)"
    }

    override fun list(): Any {
        return ContentUtils.readLines(repoUrl.toString())
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        if (activity is MainActivity) {
            activity.setLoading(true, "Loading Repository...")
        }

        Thread {
            try {
                val files = ContentUtils.readLines(repoUrl.toString())
                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX
                    ) { file ->
                        if (file != null) {
                            if (file.endsWith(".index")) {
                                RepositoryModelProvider(URI.create(file)).load(activity, callback)
                            } else {
                                callback.onModelSelected(URI.create(file))
                            }
                        } else {
                            callback.onModelSelected(null)
                        }
                    }.create().show()
                }
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Error loading Repository", e)
                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }
                    Toast.makeText(activity, "Error: " + e.message, Toast.LENGTH_LONG).show()
                    callback.onModelSelected(null)
                }
            }
        }.start()
    }

    override fun resolve(id: String): URI? {
        return try {
            URI.create(id)
        } catch (e: Exception) {
            null
        }
    }
}
