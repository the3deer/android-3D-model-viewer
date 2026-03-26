package org.the3deer.android.viewer.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import androidx.preference.*
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.util.bean.*
import org.the3deer.dddmodel2.R
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        setPreferenceScreen(screen)

        val engine = sharedViewModel.activeEngine.value ?: return
        val beanFactory = engine.beanFactory
        val beans = beanFactory.getBeans()

        val beansWithProperties = beans.filter { (_, bean) ->
            beanFactory.getProperties(bean).isNotEmpty()
        }

        val groupedBeans = beansWithProperties.values.groupBy { bean ->
            val beanClass = bean.javaClass
            val feature = beanClass.getAnnotation(Feature::class.java) 
                ?: beanClass.`package`?.getAnnotation(Feature::class.java)
            
            feature?.name?.takeIf { it.isNotEmpty() } 
                ?: beanClass.`package`?.name?.substringAfterLast('.')?.replaceFirstChar { it.uppercase() }
                ?: "General"
        }

        groupedBeans.forEach { (featureName, featureBeans) ->
            val featureMetadata = featureBeans.firstNotNullOfOrNull { bean ->
                bean.javaClass.getAnnotation(Feature::class.java) 
                    ?: bean.javaClass.`package`?.getAnnotation(Feature::class.java)
            }

            val category = PreferenceCategory(context).apply {
                title = if (featureMetadata?.experimental == true) "$featureName (Experimental)" else featureName
                summary = featureMetadata?.description?.takeIf { it.isNotEmpty() }
                layoutResource = R.layout.preference_category
            }
            screen.addPreference(category)

            featureBeans.forEach { bean ->
                val id = bean.javaClass.name
                val propertiesMap = beanFactory.getProperties(bean)
                
                val beanAnnotation = bean.javaClass.getAnnotation(Bean::class.java)
                val componentName = beanAnnotation?.name?.takeIf { it.isNotEmpty() }
                    ?: bean.javaClass.simpleName.replace("Drawer", "").replace("Renderer", "").replace("Default", "")

                val propertyInfos = propertiesMap.values.toList()
                val enabledProp = propertyInfos.find { it.name == "enabled" }
                val otherProps = propertyInfos.filter { it != enabledProp }

                var masterDependencyKey: String? = null

                if (enabledProp != null) {
                    val toggleTitle = enabledProp.name.takeIf { it.isNotEmpty() && it != "enabled" } ?: componentName
                    val masterSwitch = createSwitchPreference(context, id, bean, enabledProp, toggleTitle)
                    category.addPreference(masterSwitch)
                    masterDependencyKey = masterSwitch.key
                }

                otherProps.forEach { prop ->
                    createPreference(context, id, bean, prop)?.let { pref ->
                        category.addPreference(pref)
                        if (masterDependencyKey != null) {
                            pref.dependency = masterDependencyKey
                        }
                    }
                }
            }
        }
    }

    private fun createSwitchPreference(context: Context, id: String, bean: Any, prop: BeanPropertyInfo, titleText: String): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = "$id.${prop.name}"
            title = titleText
            summary = prop.description?.takeIf { it.isNotEmpty() }
            try {
                val value = prop.getValue(bean)
                if (value is Boolean) setDefaultValue(value)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error getting value for ${prop.name}", e)
            }
            isIconSpaceReserved = false
        }
    }

    private fun createPreference(context: Context, id: String, bean: Any, prop: BeanPropertyInfo): Preference? {
        val propertyName = prop.name
        val preferenceKey = "$id.$propertyName"

        val titleText = prop.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        return when (prop.type) {
            Boolean::class.java, java.lang.Boolean.TYPE -> {
                SwitchPreferenceCompat(context).apply {
                    key = preferenceKey
                    title = titleText
                    summary = prop.description?.takeIf { it.isNotEmpty() }
                    try {
                        val value = prop.getValue(bean)
                        if (value is Boolean) setDefaultValue(value)
                    } catch (e: Exception) {
                        Log.e("SettingsFragment", "Error getting value for ${prop.name}", e)
                    }
                    isIconSpaceReserved = false
                }
            }
            else -> {
                val staticValues = prop.values
                val valuesMethod = prop.valuesMethod
                if (valuesMethod != null || (staticValues != null && staticValues.isNotEmpty()) || prop.valueNames.isNotEmpty()) {
                    ListPreference(context).apply {
                        key = preferenceKey
                        title = titleText
                        summary = prop.description?.takeIf { it.isNotEmpty() } ?: "%s"
                        isIconSpaceReserved = false
                        setupListPreference(this, bean, prop)
                    }
                } else null
            }
        }
    }

    private fun setupListPreference(pref: ListPreference, bean: Any, prop: BeanPropertyInfo) {
        val values = getPropertyValues(bean, prop)
        val names = getPropertyNames(prop, values)
        val ids = getPropertyIds(values, names)

        pref.entries = names
        pref.entryValues = ids
        
        val currentValue = try { prop.getValue(bean) } catch (e: Exception) { null }
        val currentIndex = values.indexOfFirst { areEqual(it, currentValue) }
        if (currentIndex != -1) {
            pref.value = ids[currentIndex].toString()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == null || sharedPreferences == null) return
        val engine = sharedViewModel.activeEngine.value ?: return
        
        applyPreferenceToEngine(engine.beanFactory, sharedPreferences, key)

        if (key.endsWith(".language")) {
            val settingsOptions = engine.beanFactory.find(SettingsOptions::class.java)
            val languageCode = settingsOptions?.language ?: "en"
            
            Log.i("SettingsFragment", "System bridge: Switching to $languageCode")
            
            val appLocales = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(appLocales)
        }
    }

    companion object {
        fun applySavedPreferences(engine: ModelEngine, context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val beanFactory = engine.beanFactory
            sharedPreferences.all.keys.forEach { key ->
                if (key.contains(".")) applyPreferenceToEngine(beanFactory, sharedPreferences, key)
            }
        }

        private fun applyPreferenceToEngine(beanFactory: BeanFactory, sharedPreferences: SharedPreferences, key: String) {
            val beanId = key.substringBeforeLast(".")
            val propertyName = key.substringAfterLast(".")
            
            val beanClass = try { Class.forName(beanId) } catch (e: Exception) { null } ?: return
            val bean = beanFactory.find(beanClass) ?: return

            try {
                val properties = beanFactory.getProperties(bean)
                val info = properties[propertyName] ?: return

                if (info.type == Boolean::class.java || info.type == java.lang.Boolean.TYPE) {
                    if (sharedPreferences.contains(key)) {
                        info.setValue(bean, sharedPreferences.getBoolean(key, false))
                    }
                    return
                }

                val valueStr = sharedPreferences.getString(key, null) ?: return
                
                val values = getPropertyValues(bean, info)
                val names = getPropertyNames(info, values)
                val ids = getPropertyIds(values, names)

                var index = ids.indexOf(valueStr)
                if (index == -1) index = valueStr.toIntOrNull() ?: -1

                if (index != -1 && index in values.indices) {
                    val selectedValue = values[index]
                    
                    if (info.valuesMethod != null) {
                        info.setValue(bean, selectedValue)
                    } else {
                        val convertedValue = when (info.type) {
                            Int::class.java, java.lang.Integer.TYPE -> selectedValue.toString().toIntOrNull() ?: index
                            Float::class.java, java.lang.Float.TYPE -> selectedValue.toString().toFloatOrNull() ?: index.toFloat()
                            String::class.java -> selectedValue.toString()
                            else -> selectedValue
                        }
                        info.setValue(bean, convertedValue)
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error updating property $key", e)
            }
        }

        private fun getPropertyValues(bean: Any, prop: BeanPropertyInfo): List<Any?> {
            val valuesMethod = prop.valuesMethod
            if (valuesMethod != null) {
                val valuesObj = try { valuesMethod.invoke(bean) } catch (e: Exception) { null }
                return when (valuesObj) {
                    is List<*> -> valuesObj
                    else -> if (valuesObj?.javaClass?.isArray == true) {
                        (0 until java.lang.reflect.Array.getLength(valuesObj)).map { java.lang.reflect.Array.get(valuesObj, it) }
                    } else emptyList()
                }
            }
            val staticValues = prop.values
            if (staticValues != null && staticValues.isNotEmpty()) return staticValues.toList()
            if (prop.valueNames.isNotEmpty()) return prop.valueNames.indices.toList()
            return emptyList()
        }

        private fun getPropertyNames(info: BeanPropertyInfo, values: List<Any?>): Array<CharSequence> {
            if (info.valueNames.isNotEmpty()) return info.valueNames.map { it as CharSequence }.toTypedArray()
            return values.map { it?.toString() ?: "null" }.toTypedArray()
        }

        private fun getPropertyIds(values: List<Any?>, names: Array<CharSequence>): Array<CharSequence> {
            return names.indices.map { it.toString() }.toTypedArray()
        }

        private fun areEqual(v1: Any?, v2: Any?): Boolean {
            if (v1 == v2) return true
            if (v1 == null || v2 == null) return false
            if (v1.toString() == v2.toString()) return true
            return false
        }
    }
}
