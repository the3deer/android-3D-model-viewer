package org.the3deer.android.viewer.providers

import android.app.Activity
import android.widget.Toast
import org.json.JSONArray
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.MainActivity
import org.the3deer.android.viewer.util.ContentUtils
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Model provider for ToxSam/open-source-3D-assets repository.
 * Contains high quality CC0 3D asset collections.
 *
 * @author Gemini AI
 */
internal class OpenSource3DAssetsModelProvider : ModelProvider {

    companion object {
        private const val TAG = "OpenSource3DAssetsModelProvider"
        private const val BASE_DATA_URL = "https://raw.githubusercontent.com/ToxSam/open-source-3D-assets/main/data/"
        private const val PROJECTS_JSON_URL = BASE_DATA_URL + "projects.json"
    }

    private data class Project(
        val id: String,
        val name: String,
        val creatorId: String,
        val description: String,
        val license: String,
        val assetDataFile: String
    )

    private data class AssetItem(
        val id: String,
        val name: String,
        val description: String,
        val modelFileUrl: String,
        val format: String
    )

    override fun list(): Any {
        val samples = mutableListOf<URI>()
        try {
            val json = ContentUtils.read(URI.create(PROJECTS_JSON_URL))
            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val assetDataFile = obj.optString("asset_data_file", "")
                if (assetDataFile.isNotEmpty()) {
                    val assetJson = ContentUtils.read(URI.create(BASE_DATA_URL + assetDataFile))
                    val assetArray = JSONArray(assetJson)
                    for (j in 0 until assetArray.length()) {
                        val assetObj = assetArray.getJSONObject(j)
                        val modelUrl = assetObj.optString("model_file_url", "")
                        if (modelUrl.isNotEmpty()) {
                            samples.add(URI.create(modelUrl))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Logger.getLogger(TAG).log(Level.SEVERE, "Error listing Open Source 3D Assets", e)
        }
        return samples
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        if (activity is MainActivity) {
            activity.setLoading(true, "Loading Open Source 3D Assets Repository...")
        }

        Thread {
            try {
                val json = ContentUtils.read(URI.create(PROJECTS_JSON_URL))
                val jsonArray = JSONArray(json)
                val projects = mutableListOf<Project>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", "")
                    val name = obj.optString("name", id)
                    val creatorId = obj.optString("creator_id", "")
                    val description = obj.optString("description", "")
                    val license = obj.optString("license", "CC0")
                    val assetDataFile = obj.optString("asset_data_file", "")

                    if (assetDataFile.isNotEmpty()) {
                        projects.add(Project(id, name, creatorId, description, license, assetDataFile))
                    }
                }

                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }

                    if (projects.isEmpty()) {
                        Toast.makeText(activity, "No asset collections found", Toast.LENGTH_SHORT).show()
                        callback.onModelSelected(null)
                        return@runOnUiThread
                    }

                    val projectDisplayNames = projects.map { project ->
                        if (project.description.isNotEmpty()) {
                            "${project.name} - ${project.description}"
                        } else {
                            project.name
                        }
                    }.toTypedArray()

                    ContentUtils.showListDialog(
                        activity,
                        "Select Asset Pack",
                        projectDisplayNames
                    ) { _, which ->
                        val selectedProject = projects[which]
                        loadProjectAssets(activity, selectedProject, callback)
                    }
                }
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Error fetching projects.json", e)
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

    private fun loadProjectAssets(activity: Activity, project: Project, callback: ModelProvider.Callback) {
        if (activity is MainActivity) {
            activity.setLoading(true, "Loading ${project.name}...")
        }

        Thread {
            try {
                val assetJsonUrl = BASE_DATA_URL + project.assetDataFile
                val json = ContentUtils.read(URI.create(assetJsonUrl))
                val jsonArray = JSONArray(json)
                val items = mutableListOf<AssetItem>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", "")
                    val name = obj.optString("name", id)
                    val description = obj.optString("description", "")
                    val modelUrl = obj.optString("model_file_url", "")
                    val format = obj.optString("format", "GLB")

                    if (modelUrl.isNotEmpty()) {
                        items.add(AssetItem(id, name, description, modelUrl, format))
                    }
                }

                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }

                    if (items.isEmpty()) {
                        Toast.makeText(activity, "No assets found in ${project.name}", Toast.LENGTH_SHORT).show()
                        callback.onModelSelected(null)
                        return@runOnUiThread
                    }

                    val itemDisplayNames = items.map { it.name }.toTypedArray()

                    ContentUtils.showListDialog(
                        activity,
                        project.name,
                        itemDisplayNames
                    ) { _, which ->
                        val selectedItem = items[which]
                        val model = resolve(URI.create(selectedItem.modelFileUrl))
                        if (project.creatorId.isNotBlank()) {
                            model.author = project.creatorId
                        }
                        if (project.license.isNotBlank()) {
                            model.license = project.license
                        }
                        if (project.description.isNotBlank()) {
                            model.comment = project.description
                        }
                        if (selectedItem.description.isNotBlank()) {
                            model.description = selectedItem.description
                        } else if (project.description.isNotBlank()) {
                            model.description = project.description
                        }
                        callback.onModelSelected(model)
                    }
                }
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Error fetching assets for ${project.name}", e)
                activity.runOnUiThread {
                    if (activity is MainActivity) {
                        activity.setLoading(false, null)
                    }
                    Toast.makeText(activity, "Error loading pack: " + e.message, Toast.LENGTH_LONG).show()
                    callback.onModelSelected(null)
                }
            }
        }.start()
    }

    override fun resolve(uri: URI): Model {
        val model = Model(uri)
        val pathName = uri.path.substringAfterLast("/")
        model.name = if (pathName.contains(".")) pathName.substringBeforeLast(".") else pathName
        model.type = uri.path.substringAfterLast(".", "glb").lowercase()
        model.license = "CC0"
        model.provider = "open-source-3d-assets"
        return model
    }
}
