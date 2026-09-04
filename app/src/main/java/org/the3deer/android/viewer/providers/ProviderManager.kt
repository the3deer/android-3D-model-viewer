package org.the3deer.android.viewer.providers

import android.app.Activity
import android.app.Application
import androidx.annotation.StringRes
import org.the3deer.android.engine.Model
import org.the3deer.android.viewer.R
import java.net.URI
import java.util.Locale

class ProviderInfo internal constructor(
    val id: String,
    @get:StringRes val titleResId: Int,
    val autoDismiss: Boolean = false,
    internal val provider: ModelProvider
)

class ProviderManager(private val application: Application) {

    val providerList: List<ProviderInfo> = listOf(
        ProviderInfo("assets", R.string.provider_assets, provider = AssetsModelProvider(application)),
        ProviderInfo("the3deer", R.string.provider_the3deer, provider = RepositoryModelProvider()),
        ProviderInfo("khronos", R.string.provider_khronos, provider = KhronosModelProvider()),
        ProviderInfo("open-source-3d-assets", R.string.provider_open_source_3d_assets, provider = OpenSource3DAssetsModelProvider()),
        ProviderInfo("poly haven", R.string.provider_polyhaven, provider = PolyHavenModelProvider()),
        ProviderInfo("android_explorer", R.string.provider_android_explorer, autoDismiss = true, provider = AndroidExplorerModelProvider()),
    )

    private val providersMap: Map<String, ProviderInfo> = providerList.associateBy { it.id }

    fun getProviders(): List<String> {
        return providerList.map { application.getString(it.titleResId) }
    }

    fun load(index: Int, activity: Activity, callback: ModelCallback) {
        val providerInfo = providerList.getOrNull(index)
        if (providerInfo != null) {
            load(providerInfo.id, activity, callback)
        } else {
            callback.onModelSelected(null)
        }
    }

    fun load(providerId: String, activity: Activity, callback: ModelCallback) {
        val info = providersMap[providerId] ?: providersMap[providerId.lowercase(Locale.getDefault())]
        if (info != null) {
            info.provider.load(activity, callback::onModelSelected)
        } else {
            callback.onModelSelected(null)
        }
    }

    fun listModels(providerId: String): Any {
        val info = providersMap[providerId] ?: providersMap[providerId.lowercase(Locale.getDefault())]
        return info?.provider?.list() ?: emptyList<URI>()
    }

    fun resolveModel(providerId: String, uri: URI): Model {
        val info = providersMap[providerId] ?: providersMap[providerId.lowercase(Locale.getDefault())]
        return info?.provider?.resolve(uri) ?: Model(uri)
    }
}
