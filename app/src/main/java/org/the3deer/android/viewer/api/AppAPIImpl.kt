package org.the3deer.android.viewer.api

import android.app.Application
import org.the3deer.android.engine.Model
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.viewer.providers.ProviderManager
import org.the3deer.android.viewer.settings.SettingsManager
import org.the3deer.util.bean.BeanInfo
import org.the3deer.util.bean.BeanPropertyInfo
import java.net.URI

class AppAPIImpl(
    private val application: Application,
    private val settings: SettingsManager,
    private val providerManager: ProviderManager,
    private val onNavigate: (String) -> Unit,
    private val onLoadModel: (Model) -> Unit,
    private val onExit: () -> Unit
) : AppAPI {

    override fun navigate(screenId: String) {
        onNavigate(screenId)
    }

    override fun loadModel(model: Model) {
        if (model.uriModel == null && !model.provider.isNullOrEmpty()) {
            // Re-hydrate model to get absolute URLs and included files
            val hydrated = resolveModel(model.provider!!, model.uri)
            if (hydrated != null) {
                onLoadModel(hydrated)
                return
            }
        }
        onLoadModel(model)
    }

    override fun updateSetting(key: String, value: Any) {
        settings.setPreference(application, key, value)
        
        // Apply to Engine if active
        ModelEngineViewModel.INSTANCE?.api?.setConfig(key, value)
    }

    override fun exitApp() {
        onExit()
    }

    override fun getProviders(): List<String> {
        return providerManager.getProviders()
    }

    override fun listModels(provider: String): Any {
        return providerManager.listModels(provider)
    }

    override fun resolveModel(provider: String, uri: URI): Model? {
        return providerManager.resolveModel(provider, uri)
    }

    override fun getBeans(): List<BeanInfo> {
        val appBeans = settings.getAppBeans(application)
        val engineBeans = ModelEngineViewModel.INSTANCE?.api?.managedBeans ?: emptyList()
        return settings.hydrate(application, appBeans + engineBeans)
    }

    override fun listSettings(): List<BeanPropertyInfo> {
        val beans = getBeans()
        return beans.flatMap { it.properties.values }
    }
}
