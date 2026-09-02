package org.the3deer.android.viewer.providers

import android.app.Application
import org.the3deer.android.engine.Model
import java.net.URI
import java.util.Locale

class ProviderManager(private val application: Application) {

    private val providers: Map<String, ModelProvider> = mapOf(
        "assets" to AssetsModelProvider(application),
        "khronos" to KhronosModelProvider(),
        "poly haven" to PolyHavenModelProvider(),
        "open-source-3d-assets" to OpenSource3DAssetsModelProvider(),
        "the3deer" to RepositoryModelProvider()
    )

    fun getProviders(): List<String> {
        return providers.keys.map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } }
    }

    fun getProvider(name: String?): ModelProvider? {
        if (name == null) return null
        val key = name.lowercase(Locale.getDefault())
        return providers[key] ?: if (key == "polyhaven") providers["poly haven"] else null
    }

    fun listModels(providerName: String): Any {
        return getProvider(providerName)?.list() ?: emptyList<URI>()
    }

    fun resolveModel(providerName: String, uri: URI): Model? {
        return getProvider(providerName)?.resolve(uri)
    }
}
