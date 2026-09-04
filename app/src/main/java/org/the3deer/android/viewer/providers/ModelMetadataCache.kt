package org.the3deer.android.viewer.providers

import org.json.JSONArray
import org.json.JSONObject
import org.the3deer.android.engine.Model
import java.io.File
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Thread-safe cache component that manages persistent model metadata
 * in a `.cache_model.json` file inside cacheDir.
 *
 * @author Gemini AI
 */
class ModelMetadataCache(private val cacheDir: File) {

    companion object {
        private const val TAG = "ModelMetadataCache"
        private const val CACHE_FILE_NAME = ".cache_model.json"
        private const val MAX_CACHE_ITEMS = 50
    }

    data class ModelMetadata(
        val uri: String,
        var uriModel: String? = null,
        var name: String? = null,
        var type: String? = null,
        var provider: String? = null,
        var author: String? = null,
        var license: String? = null,
        var description: String? = null,
        var comment: String? = null,
        var includes: Map<String, String>? = null,
        var lastAccessed: Long = System.currentTimeMillis()
    )

    private val cacheMap = ConcurrentHashMap<String, ModelMetadata>()
    private val executor = Executors.newSingleThreadExecutor()
    private val cacheFile = File(cacheDir, CACHE_FILE_NAME)

    init {
        loadCacheFromFile()
    }

    /**
     * Stores or updates the metadata for a Model instance and persists to .cache_model.json.
     */
    fun put(model: Model) {
        val uriKey = model.uri.toString()
        val meta = cacheMap.getOrPut(uriKey) { ModelMetadata(uriKey) }

        meta.lastAccessed = System.currentTimeMillis()

        if (!model.name.isNullOrBlank()) meta.name = model.name
        if (!model.type.isNullOrBlank()) meta.type = model.type
        if (!model.provider.isNullOrBlank()) meta.provider = model.provider
        if (!model.author.isNullOrBlank()) meta.author = model.author
        if (!model.license.isNullOrBlank()) meta.license = model.license
        if (!model.description.isNullOrBlank()) meta.description = model.description
        if (!model.comment.isNullOrBlank()) meta.comment = model.comment
        if (model.uriModel != null) meta.uriModel = model.uriModel.toString()
        if (model.includes.isNotEmpty()) {
            meta.includes = model.includes.mapValues { it.value.toString() }
        }

        saveCacheToFileAsync()
    }

    /**
     * Hydrates a Model object with cached metadata properties if present.
     */
    fun hydrate(model: Model) {
        val uriKey = model.uri.toString()
        val meta = cacheMap[uriKey] ?: return

        meta.lastAccessed = System.currentTimeMillis()

        if (model.name.isNullOrBlank() && !meta.name.isNullOrBlank()) {
            model.name = meta.name
        }
        if (model.type.isNullOrBlank() && !meta.type.isNullOrBlank()) {
            model.type = meta.type
        }
        if (model.provider.isNullOrBlank() && !meta.provider.isNullOrBlank()) {
            model.provider = meta.provider
        }
        if (model.author.isNullOrBlank() && !meta.author.isNullOrBlank()) {
            model.author = meta.author
        }
        if (model.license.isNullOrBlank() && !meta.license.isNullOrBlank()) {
            model.license = meta.license
        }
        if (model.description.isNullOrBlank() && !meta.description.isNullOrBlank()) {
            model.description = meta.description
        }
        if (model.comment.isNullOrBlank() && !meta.comment.isNullOrBlank()) {
            model.comment = meta.comment
        }
        if (model.uriModel == null && !meta.uriModel.isNullOrBlank()) {
            try {
                model.uriModel = URI.create(meta.uriModel)
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.WARNING, "Failed to parse uriModel: " + meta.uriModel, e)
            }
        }
        if (!meta.includes.isNullOrEmpty()) {
            meta.includes?.forEach { (key, uriStr) ->
                try {
                    model.addUri(key, URI.create(uriStr))
                } catch (e: Exception) {
                    Logger.getLogger(TAG).log(Level.WARNING, "Failed to parse included URI: " + uriStr, e)
                }
            }
        }
    }

    /**
     * Retrieves metadata for a specific URI if cached.
     */
    fun get(uri: URI): ModelMetadata? {
        return cacheMap[uri.toString()]
    }

    /**
     * Retrieves the list of recently accessed model metadata ordered by last accessed time.
     */
    fun getRecent(limit: Int = 10): List<ModelMetadata> {
        return cacheMap.values
            .sortedByDescending { it.lastAccessed }
            .take(limit)
    }

    /**
     * Retrieves the list of recently accessed Model objects ordered by last accessed time.
     */
    fun getRecentModels(limit: Int = 10): List<Model> {
        return getRecent(limit).map { meta ->
            val model = Model(URI.create(meta.uri))
            hydrate(model)
            model
        }
    }

    /**
     * Removes an entry from the cache and updates the cache file.
     */
    fun remove(uri: URI) {
        if (cacheMap.remove(uri.toString()) != null) {
            saveCacheToFileAsync()
        }
    }

    private fun loadCacheFromFile() {
        if (!cacheFile.exists()) return

        try {
            val content = cacheFile.readText()
            if (content.isBlank()) return

            val jsonArray = JSONArray(content)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val uri = obj.optString("uri", "")
                if (uri.isNotBlank()) {
                    val includesMap = mutableMapOf<String, String>()
                    if (obj.has("includes")) {
                        val includesObj = obj.getJSONObject("includes")
                        val keys = includesObj.keys()
                        while (keys.hasNext()) {
                            val k = keys.next()
                            includesMap[k] = includesObj.getString(k)
                        }
                    }

                    val meta = ModelMetadata(
                        uri = uri,
                        uriModel = if (obj.has("uriModel")) obj.getString("uriModel") else null,
                        name = if (obj.has("name")) obj.getString("name") else null,
                        type = if (obj.has("type")) obj.getString("type") else null,
                        provider = if (obj.has("provider")) obj.getString("provider") else null,
                        author = if (obj.has("author")) obj.getString("author") else null,
                        license = if (obj.has("license")) obj.getString("license") else null,
                        description = if (obj.has("description")) obj.getString("description") else null,
                        comment = if (obj.has("comment")) obj.getString("comment") else null,
                        includes = if (includesMap.isNotEmpty()) includesMap else null,
                        lastAccessed = obj.optLong("lastAccessed", System.currentTimeMillis())
                    )
                    cacheMap[uri] = meta
                }
            }
        } catch (e: Exception) {
            Logger.getLogger(TAG).log(Level.SEVERE, "Error loading .cache_model.json", e)
        }
    }

    private fun saveCacheToFileAsync() {
        executor.execute {
            try {
                val sortedItems = cacheMap.values
                    .sortedByDescending { it.lastAccessed }
                    .take(MAX_CACHE_ITEMS)

                val jsonArray = JSONArray()
                for (item in sortedItems) {
                    val obj = JSONObject()
                    obj.put("uri", item.uri)
                    if (item.uriModel != null) obj.put("uriModel", item.uriModel)
                    if (item.name != null) obj.put("name", item.name)
                    if (item.type != null) obj.put("type", item.type)
                    if (item.provider != null) obj.put("provider", item.provider)
                    if (item.author != null) obj.put("author", item.author)
                    if (item.license != null) obj.put("license", item.license)
                    if (item.description != null) obj.put("description", item.description)
                    if (item.comment != null) obj.put("comment", item.comment)
                    if (!item.includes.isNullOrEmpty()) {
                        val includesObj = JSONObject()
                        item.includes?.forEach { (k, v) -> includesObj.put(k, v) }
                        obj.put("includes", includesObj)
                    }
                    obj.put("lastAccessed", item.lastAccessed)
                    jsonArray.put(obj)
                }

                cacheFile.writeText(jsonArray.toString(2))
            } catch (e: Exception) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Error saving .cache_model.json", e)
            }
        }
    }
}
