package org.the3deer.android.viewer.api

import org.the3deer.android.engine.Model
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.util.bean.BeanInfo
import org.the3deer.util.bean.BeanPropertyInfo
import java.net.URI

class AppAPIImpl(
    private val model: SharedViewModel
) : AppAPI {

    override fun navigate(screenId: String) {
        model.navigate(screenId)
    }

    override fun loadModel(model: Model) {
        this.model.loadModel(model)
    }

    override fun updateSetting(key: String, value: Any) {
        model.settings.setPreference(model.getApplication(), key, value)
        
        // Apply to Engine if active
        ModelEngineViewModel.INSTANCE?.api?.setConfig(key, value)
    }

    override fun exitApp() {
        model.exitApp()
    }

    override fun getProviders(): List<String> {
        return model.providerManager.getProviders()
    }

    override fun listModels(provider: String): Any {
        return model.providerManager.listModels(provider)
    }

    override fun resolveModel(provider: String, uri: URI): Model? {
        return model.providerManager.resolveModel(provider, uri)
    }

    override fun getBeans(): List<BeanInfo> {
        val appBeans = model.settings.getAppBeans(model.getApplication())
        val engineBeans = ModelEngineViewModel.INSTANCE?.api?.managedBeans ?: emptyList()
        return model.settings.hydrate(model.getApplication(), appBeans + engineBeans)
    }

    override fun listSettings(): List<BeanPropertyInfo> {
        val beans = getBeans()
        return beans.flatMap { it.properties.values }
    }
}
