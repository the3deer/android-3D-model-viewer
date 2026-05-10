package org.the3deer.android.viewer.providers

import android.app.Activity
import android.widget.Toast
import org.json.JSONArray
import org.the3deer.android.viewer.MainActivity
import org.the3deer.android.viewer.providers.ModelProvider
import org.the3deer.android.viewer.ui.DialogUtils
import org.the3deer.android.viewer.util.ContentUtils
import java.net.URI
import java.net.URLEncoder
import java.util.logging.Level
import java.util.logging.Logger

class KhronosModelProvider : ModelProvider {
    private val TAG = "KhronosModelProvider"

    companion object {
        const val REPO_KHRONOS_URL = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/model-index.json"
        const val SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)"
    }

    override fun list(): List<URI> {
        val samples = mutableListOf<URI>()
        try {
            val url = URI.create(REPO_KHRONOS_URL)
            val json = ContentUtils.read(url)
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                try {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val name = jsonObject.getString("name")
                    val variants = jsonObject.getJSONObject("variants")
                    
                    var filename: String? = null
                    var type: String? = null
                    if (variants.has("glTF-Binary")) {
                        filename = variants.getString("glTF-Binary")
                        type = "glTF-Binary"
                    } else if (variants.has("glTF")) {
                        filename = variants.getString("glTF")
                        type = "glTF"
                    }

                    if (filename != null) {
                        val nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20")
                        val filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20")
                        val uriString = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/$nameEncoded/$type/$filenameEncoded"
                        samples.add(URI.create(uriString))
                    }
                } catch (e: Exception) {
                    Logger.getLogger(TAG).log(Level.SEVERE, "Error parsing Khronos item", e)
                }
            }
        } catch (e: Exception) {
            Logger.getLogger(TAG).log(Level.SEVERE, "Error loading Khronos samples", e)
        }
        return samples
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        val url = URI.create(REPO_KHRONOS_URL)

        if (activity is MainActivity) {
            activity.setLoading(true, "Loading Khronos Repository...")
        }

        Thread {
            try {
                // read JSON
                val json = ContentUtils.read(url)
                val jsonArray = JSONArray(json)
                val files = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    try {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val name = jsonObject.getString("name")
                        val variants = jsonObject.getJSONObject("variants")
                        if (variants.has("glTF-Binary")) {
                            val filename = variants.getString("glTF-Binary")
                            val baseUri = URI.create(REPO_KHRONOS_URL).resolve(".")
                            val nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20")
                            val filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20")
                            val uri = baseUri.toString() + nameEncoded + "/glTF-Binary/" + filenameEncoded
                            files.add(uri)
                        } else if (variants.has("glTF")) {
                            val filename = variants.getString("glTF")
                            val baseUri = URI.create(REPO_KHRONOS_URL).resolve(".")
                            val nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20")
                            val filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20")
                            val uri = baseUri.toString() + nameEncoded + "/glTF/" + filenameEncoded
                            files.add(uri)
                        }
                    } catch (e: Exception) {
                        Logger.getLogger(TAG).log(Level.SEVERE, "Error parsing item", e)
                    }
                }
                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX
                    ) { file ->
                        if (file != null) {
                            callback.onModelSelected(URI.create(file))
                        } else {
                            callback.onModelSelected(null)
                        }
                    }.create().show()
                }
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Error loading Khronos", e)
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
