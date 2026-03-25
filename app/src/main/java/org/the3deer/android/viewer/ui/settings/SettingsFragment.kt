package org.the3deer.android.viewer.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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

        // FIX: Set preference screen EARLY so that findPreferenceInHierarchy works 
        // during build, avoiding IllegalStateException: Dependency not found.
        setPreferenceScreen(screen)

        val engine = sharedViewModel.activeEngine.value ?: return
        val beanFactory = engine.beanFactory
        val beans = beanFactory.getBeans()

        // 1. Filter beans that have at least one @BeanProperty
        val beansWithProperties = beans.filter { (_, bean) ->
            beanFactory.getProperties(bean).isNotEmpty()
        }

        // 2. Group filtered beans by @Feature or Package
        val groupedBeans = beansWithProperties.values.groupBy { bean ->
            val beanClass = bean.javaClass
            val feature = beanClass.getAnnotation(Feature::class.java) 
                ?: beanClass.`package`?.getAnnotation(Feature::class.java)
            
            feature?.name?.takeIf { it.isNotEmpty() } 
                ?: beanClass.`package`?.name?.substringAfterLast('.')?.replaceFirstChar { it.uppercase() }
                ?: "General"
        }

        groupedBeans.forEach { (featureName, featureBeans) ->
            // Find feature metadata to extract the description and experimental flag
            val featureMetadata = featureBeans.firstNotNullOfOrNull { bean ->
                bean.javaClass.getAnnotation(Feature::class.java) 
                    ?: bean.javaClass.`package`?.getAnnotation(Feature::class.java)
            }

            // Create a section for each Feature (Blue title in legacy)
            val category = PreferenceCategory(context).apply {
                title = if (featureMetadata?.experimental == true) "$featureName (Experimental)" else featureName
                summary = featureMetadata?.description?.takeIf { it.isNotEmpty() }
                layoutResource = R.layout.preference_category
            }
            screen.addPreference(category)

            featureBeans.forEach { bean ->
                val id = bean.javaClass.name
                val propertiesMap = beanFactory.getProperties(bean)
                
                // Use the Bean name or Class name for the feature toggle
                val beanAnnotation = bean.javaClass.getAnnotation(Bean::class.java)
                val componentName = beanAnnotation?.name?.takeIf { it.isNotEmpty() }
                    ?: bean.javaClass.simpleName.replace("Drawer", "").replace("Renderer", "").replace("Default", "")

                // Filter inherited fields to avoid duplicates
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

                // Add sub-properties
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
                setDefaultValue(prop.getValue(bean))
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error getting value for ${prop.name}", e)
            }
            isIconSpaceReserved = false
        }
    }

    private fun createPreference(context: Context, id: String, bean: Any, prop: BeanPropertyInfo): Preference? {
        val propertyName = prop.name
        val preferenceKey = "$id.$propertyName"

        val titleText = if (prop.name.isNotEmpty()) {
            prop.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        } else {
            propertyName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }

        return when (prop.type) {
            Boolean::class.java, java.lang.Boolean.TYPE -> {
                SwitchPreferenceCompat(context).apply {
                    key = preferenceKey
                    title = titleText
                    summary = prop.description?.takeIf { it.isNotEmpty() }
                    try {
                        setDefaultValue(prop.getValue(bean))
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
            pref.value = ids[currentIndex]
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
                        info.setValue(bean, sharedPreferences.getBoolean(key, info.getValue(bean) as Boolean))
                    }
                    return
                }

                val valueStr = sharedPreferences.getString(key, null) ?: return
                
                // 1. Resolve values and stable IDs
                val values = getPropertyValues(bean, info)
                val names = getPropertyNames(info, values)
                val ids = getPropertyIds(values, names)

                // 2. Find selected index
                var index = ids.indexOf(valueStr)
                if (index == -1) index = valueStr.toIntOrNull() ?: -1 // Index fallback

                if (index != -1 && index in values.indices) {
                    val selectedValue = values[index]
                    
                    // 3. Apply value with type conversion for static annotation values
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
            
            // Fallback: If no values but names exist, the names index is the value
            if (prop.valueNames.isNotEmpty()) return prop.valueNames.indices.toList()
            
            return emptyList()
        }

        private fun getPropertyNames(prop: BeanPropertyInfo, values: List<Any?>): Array<String> {
            if (prop.valueNames.isNotEmpty()) return prop.valueNames
            
            // Fallback: check valuesMethod annotation (BeanFactory might not have merged it)
            val valuesMethod = prop.valuesMethod
            if (valuesMethod != null) {
                val ann = valuesMethod.getAnnotation(BeanProperty::class.java)
                if (ann != null && ann.valueNames.isNotEmpty()) return ann.valueNames
            }

            return values.map { formatValue(it) }.toTypedArray()
        }

        private fun getPropertyIds(values: List<Any?>, names: Array<String>): Array<String> {
            return values.mapIndexed { index, value ->
                when {
                    index < names.size -> names[index] // Prefer the Name as a stable String ID
                    value is String -> value
                    value is Number -> value.toString()
                    else -> index.toString()
                }
            }.toTypedArray()
        }

        private fun formatValue(value: Any?): String {
            if (value == null) return "None"
            if (value is FloatArray) return "Color (${value.joinToString { String.format("%.2f", it) }})"
            return value.toString()
        }

        private fun areEqual(a: Any?, b: Any?): Boolean {
            if (a === b) return true
            if (a == null || b == null) return false
            if (a is FloatArray && b is FloatArray) return a.contentEquals(b)
            if (a.javaClass.isArray && b.javaClass.isArray) {
                val length = java.lang.reflect.Array.getLength(a)
                if (length != java.lang.reflect.Array.getLength(b)) return false
                for (i in 0 until length) {
                    if (!areEqual(java.lang.reflect.Array.get(a, i), java.lang.reflect.Array.get(b, i))) return false
                }
                return true
            }
            if (a is Number && b is Number) return a.toDouble() == b.toDouble()
            return a == b
        }
    }
}
