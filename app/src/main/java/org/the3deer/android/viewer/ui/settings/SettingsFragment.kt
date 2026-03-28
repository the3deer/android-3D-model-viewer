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
import java.util.Locale

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        setPreferenceScreen(screen)

        val engine = sharedViewModel.activeEngine.value ?: return
        val beanFactory = engine.beanFactory
        val beans = beanFactory.beans

        val beansWithProperties = beans.filter { (_, bean) ->
            beanFactory.getProperties(bean).isNotEmpty()
        }

        // Group beans by their Category using the priority rules
        val groupedBeans = beansWithProperties.values.groupBy { bean ->
            getCategory(bean.javaClass)
        }

        groupedBeans.forEach { (categoryName, categoryBeans) ->
            val category = PreferenceCategory(context).apply {
                title = categoryName
                layoutResource = R.layout.preference_category
            }
            screen.addPreference(category)

            categoryBeans.forEach { bean ->
                val id = bean.javaClass.name
                val propertiesMap = beanFactory.getProperties(bean)
                
                val beanAnnotation = bean.javaClass.getAnnotation(Bean::class.java)
                val isBeanExperimental = isExperimental(bean.javaClass)
                val beanDescription = getDescription(bean.javaClass)

                val componentName = (beanAnnotation?.name?.takeIf { it.isNotEmpty() }
                    ?: bean.javaClass.simpleName.replace("Drawer", "").replace("Renderer", "").replace("Default", ""))
                    .let { if (isBeanExperimental) "$it (Experimental)" else it }

                val propertyInfos = propertiesMap.values.toList()
                val enabledProp = propertyInfos.find { it.name == "enabled" }
                val otherProps = propertyInfos.filter { it != enabledProp }

                var masterDependencyKey: String? = null

                if (enabledProp != null) {
                    val toggleTitle = enabledProp.name.takeIf { it.isNotEmpty() && it != "enabled" } ?: componentName
                    val summaryText = enabledProp.description?.takeIf { it.isNotEmpty() } ?: beanDescription
                    
                    val masterSwitch = createSwitchPreference(context, id, bean, enabledProp, toggleTitle, summaryText)
                    category.addPreference(masterSwitch)
                    masterDependencyKey = masterSwitch.key
                }

                otherProps.forEachIndexed { index, prop ->
                    // Fallback to bean description only if no enabled toggle exists and this is the first property
                    val fallbackDescription = if (enabledProp == null && index == 0) beanDescription else null
                    createPreference(context, id, bean, prop, fallbackDescription)?.let { pref ->
                        category.addPreference(pref)
                        if (masterDependencyKey != null) {
                            pref.dependency = masterDependencyKey
                        }
                    }
                }
            }
        }
    }

    /**
     * Resolves the category for a bean class based on @Bean, @Feature or parent package metadata.
     */
    private fun getCategory(beanClass: Class<*>): String {
        // Step 1: Check @Bean category
        beanClass.getAnnotation(Bean::class.java)?.category?.takeIf { it.isNotEmpty() }?.let { return it }

        // Step 2: Check @Feature category on class
        beanClass.getAnnotation(Feature::class.java)?.category?.takeIf { it.isNotEmpty() }?.let { return it }

        // Step 3: Walk up package hierarchy
        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            getFeatureFromPackage(pkgName)?.category?.takeIf { it.isNotEmpty() }?.let { return it }
            if (!pkgName.contains(".")) break
            pkgName = pkgName.substringBeforeLast('.')
        }

        return "General"
    }

    /**
     * Resolves the description for a bean class based on @Bean, @Feature or parent package metadata.
     */
    private fun getDescription(beanClass: Class<*>): String? {
        // Step 1: Check @Bean description
        beanClass.getAnnotation(Bean::class.java)?.description?.takeIf { it.isNotEmpty() }?.let { return it }

        // Step 2: Check @Feature description on class
        beanClass.getAnnotation(Feature::class.java)?.description?.takeIf { it.isNotEmpty() }?.let { return it }

        // Step 3: Walk up package hierarchy
        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            getFeatureFromPackage(pkgName)?.description?.takeIf { it.isNotEmpty() }?.let { return it }
            if (!pkgName.contains(".")) break
            pkgName = pkgName.substringBeforeLast('.')
        }

        return null
    }

    /**
     * Checks if a bean or its parent context is marked as experimental.
     */
    private fun isExperimental(beanClass: Class<*>): Boolean {
        if (beanClass.getAnnotation(Bean::class.java)?.experimental == true) return true
        if (beanClass.getAnnotation(Feature::class.java)?.experimental == true) return true

        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            if (getFeatureFromPackage(pkgName)?.experimental == true) return true
            if (!pkgName.contains(".")) break
            pkgName = pkgName.substringBeforeLast('.')
        }

        return false
    }

    /**
     * Helper to load @Feature annotation from package metadata or synthetic package-info class.
     */
    private fun getFeatureFromPackage(pkgName: String): Feature? {
        return try {
            Class.forName("$pkgName.package-info").getAnnotation(Feature::class.java)
        } catch (e: Exception) {
            null
        }
    }

    private fun createSwitchPreference(context: Context, id: String, bean: Any, prop: BeanPropertyInfo, titleText: String, summaryText: String?): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = "$id.${prop.name}"
            title = titleText
            summary = summaryText
            try {
                val value = prop.getValue(bean)
                if (value is Boolean) setDefaultValue(value)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error getting value for ${prop.name}", e)
            }
            isIconSpaceReserved = false
        }
    }

    private fun createPreference(context: Context, id: String, bean: Any, prop: BeanPropertyInfo, fallbackDescription: String?): Preference? {
        val propertyName = prop.name
        val preferenceKey = "$id.$propertyName"

        val titleText = prop.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val summaryText = prop.description?.takeIf { it.isNotEmpty() } ?: fallbackDescription

        return when (prop.type) {
            Boolean::class.java, java.lang.Boolean.TYPE -> {
                SwitchPreferenceCompat(context).apply {
                    key = preferenceKey
                    title = titleText
                    summary = summaryText
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
                        summary = summaryText ?: "%s"
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
        
        applyPreferenceToEngine(requireContext(), engine.beanFactory, sharedPreferences, key)
    }

    companion object {

        fun applySavedPreferences(engine: ModelEngine, context: Context) {
            Log.d("SettingsFragment", "Restoring preferences...")

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val beanFactory = engine.beanFactory
            sharedPreferences.all.keys.forEach { key ->
                if (key.contains(".")) applyPreferenceToEngine(beanFactory, sharedPreferences, key)
            }

            Log.d("SettingsFragment", "Finished restoring preferences.")
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
                Log.e("SettingsFragment", "Error applying preference $key", e)
            }
        }

        private fun getPropertyValues(bean: Any, info: BeanPropertyInfo): List<Any> {
            return try {
                val values = info.valuesMethod?.invoke(bean) as? List<*> ?: info.values.toList()
                values.filterNotNull()
            } catch (e: Exception) {
                emptyList()
            }
        }

        private fun getPropertyNames(info: BeanPropertyInfo, values: List<Any>): Array<CharSequence> {
            return if (info.valueNames.isNotEmpty()) {
                info.valueNames.map { it as CharSequence }.toTypedArray()
            } else {
                values.map { it.toString() as CharSequence }.toTypedArray()
            }
        }

        private fun getPropertyIds(values: List<Any>, names: Array<CharSequence>): Array<CharSequence> {
            return values.indices.map { i ->
                if (i < names.size) names[i] else values[i].toString()
            }.map { it as CharSequence }.toTypedArray()
        }

        private fun areEqual(v1: Any?, v2: Any?): Boolean {
            if (v1 == v2) return true
            if (v1 is FloatArray && v2 is FloatArray) return v1.contentEquals(v2)
            return false
        }
    }
}
