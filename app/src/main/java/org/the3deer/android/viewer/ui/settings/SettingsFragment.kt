package org.the3deer.android.viewer.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import org.the3deer.android.viewer.R
import org.the3deer.engine.ModelEngine
import org.the3deer.engine.android.ModelEngineViewModel
import org.the3deer.util.bean.Bean
import org.the3deer.util.bean.BeanFactory
import org.the3deer.util.bean.BeanPropertyInfo
import org.the3deer.util.bean.BeanUtils
import org.the3deer.util.bean.Feature
import java.util.Locale

/**
 * Fragment for displaying and managing application settings.
 *
 * @author andresoviedo
 * @author Gemini AI
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    val TAG: String = SettingsFragment::class.java.simpleName

    private val modelEngineViewModel: ModelEngineViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        setPreferenceScreen(screen)

        val engine = modelEngineViewModel.activeEngine.value ?: return
        val beanFactory = engine.beanFactory
        val beans = beanFactory.beans

        val beansWithProperties = beans.filter { (_, bean) ->
            beanFactory.getProperties(bean).isNotEmpty()
        }

        // Group beans by their Category using the priority rules
        val groupedBeans = beansWithProperties.values.groupBy { bean ->
            getCategory(context, bean.javaClass)
        }

        groupedBeans.forEach { (categoryName, categoryBeans) ->
            val category = PreferenceCategory(context).apply {
                title = categoryName
                layoutResource = R.layout.preference_category
            }
            screen.addPreference(category)

            categoryBeans.forEach { bean ->
                val beanId = bean.javaClass.name
                val propertiesMap = beanFactory.getProperties(bean)
                
                val isBeanExperimental = isExperimental(bean.javaClass)
                val beanDescription = getDescription(context, bean.javaClass)

                val componentName = resolveBeanLabel(context, bean.javaClass)
                    .let { if (isBeanExperimental) "$it (Experimental)" else it }

                val propertyInfos = propertiesMap.values.toList()
                val enabledProp = propertyInfos.find { it.fieldName == "enabled" }
                val otherProps = propertyInfos.filter { it != enabledProp }

                var masterDependencyKey: String? = null

                if (enabledProp != null) {
                    val toggleTitle = enabledProp.resolveLabel(context)?.takeIf { it.isNotEmpty() && it != "enabled" } ?: componentName
                    val summaryText = enabledProp.resolveDescription(context)?.takeIf { it.isNotEmpty() } ?: beanDescription
                    
                    val masterSwitch = createSwitchPreference(context, beanId, bean, enabledProp, toggleTitle, summaryText)
                    category.addPreference(masterSwitch)
                    masterDependencyKey = masterSwitch.key
                }

                otherProps.forEachIndexed { index, prop ->
                    // Fallback to bean description only if no enabled toggle exists and this is the first property
                    val fallbackDescription = if (enabledProp == null && index == 0) beanDescription else null
                    // Use componentName as fallback title for list preferences (dynamic values) or if it's the primary property
                    val fallbackTitle = if (prop.valuesMethod != null || (prop.values != null && prop.values.isNotEmpty())) componentName else null
                    
                    createPreference(context, beanId, bean, prop, fallbackTitle, fallbackDescription)?.let { pref ->
                        category.addPreference(pref)
                        if (masterDependencyKey != null) {
                            pref.dependency = masterDependencyKey
                        }
                    }
                }
            }
        }
    }

    private fun resolveBeanLabel(context: Context, beanClass: Class<*>): String {
        // Step 1: Check @Bean label
        beanClass.getAnnotation(Bean::class.java)?.let { bean ->
            val beanName = if (bean.name.isNotEmpty()) bean.name else BeanUtils.getSnakeCase(beanClass)
            val resId = context.resources.getIdentifier("bean_" + beanName + "_label", "string", context.packageName)
            if (resId != 0) return context.getString(resId)
            return beanName
        }
        
        // Step 2: Check @Feature label on class
        beanClass.getAnnotation(Feature::class.java)?.let { feature ->
            val featureName = if (feature.name.isNotEmpty()) feature.name else BeanUtils.getSnakeCase(beanClass)
            val resId = context.resources.getIdentifier("feature_" + featureName + "_label", "string", context.packageName)
            if (resId != 0) return context.getString(resId)
            return featureName
        }

        // Step 3: Walk up package hierarchy for @Feature label
        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            getFeatureFromPackage(pkgName)?.let { feature ->
                val featureName = if (feature.name.isNotEmpty()) feature.name else pkgName.substringAfterLast('.')
                val resId = context.resources.getIdentifier("feature_" + featureName + "_label", "string", context.packageName)
                if (resId != 0) return context.getString(resId)
                return featureName
            }
            if (!pkgName.contains(".")) break
            pkgName = pkgName.substringBeforeLast('.')
        }

        // return snake case version of class name
        return BeanUtils.getSnakeCase(beanClass)
    }

    /**
     * Resolves the category for a bean class based on @Bean, @Feature or parent package metadata.
     */
    private fun getCategory(context: Context, beanClass: Class<*>): String {
        // Step 1: Check @Bean category
        beanClass.getAnnotation(Bean::class.java)?.let { bean ->
            if (bean.category.isNotEmpty()) {
                val resId = context.resources.getIdentifier("category_" + bean.category + "_label", "string", context.packageName)
                if (resId != 0) return context.getString(resId)
                return bean.category
            }
        }

        // Step 2: Check @Feature category on class
        beanClass.getAnnotation(Feature::class.java)?.let { feature ->
            if (feature.category.isNotEmpty()) {
                val resId = context.resources.getIdentifier("category_" + feature.category + "_label", "string", context.packageName)
                if (resId != 0) return context.getString(resId)
                return feature.category
            }
        }

        // Step 3: Walk up package hierarchy
        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            getFeatureFromPackage(pkgName)?.let { feature ->
                if (feature.category.isNotEmpty()) {
                    val resId = context.resources.getIdentifier("category_" + feature.category + "_label", "string", context.packageName)
                    if (resId != 0) return context.getString(resId)
                    return feature.category
                }
            }
            if (!pkgName.contains(".")) break
            pkgName = pkgName.substringBeforeLast('.')
        }

        return "General"
    }

    /**
     * Resolves the description for a bean class based on @Bean, @Feature or parent package metadata.
     */
    private fun getDescription(context: Context, beanClass: Class<*>): String? {
        // Step 1: Check @Bean description
        beanClass.getAnnotation(Bean::class.java)?.let { bean ->
            val beanName = if (bean.name.isNotEmpty()) bean.name else BeanUtils.getSnakeCase(beanClass)
            val resId = context.resources.getIdentifier("bean_" + beanName + "_description", "string", context.packageName)
            if (resId != 0) return context.getString(resId)
        }

        // Step 2: Check @Feature description on class
        beanClass.getAnnotation(Feature::class.java)?.let { feature ->
            val featureName = if (feature.name.isNotEmpty()) feature.name else BeanUtils.getSnakeCase(beanClass)
            val resId = context.resources.getIdentifier("feature_" + featureName + "_description", "string", context.packageName)
            if (resId != 0) return context.getString(resId)
        }

        // Step 3: Walk up package hierarchy
        var pkgName = beanClass.name.substringBeforeLast('.', "")
        while (pkgName.isNotEmpty()) {
            getFeatureFromPackage(pkgName)?.let { feature ->
                val featureName = if (feature.name.isNotEmpty()) feature.name else pkgName.substringAfterLast('.')
                val resId = context.resources.getIdentifier("feature_" + featureName + "_description", "string", context.packageName)
                if (resId != 0) return context.getString(resId)
            }
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
            key = prop.id
            title = titleText
            summary = summaryText
            try {
                val value = prop.getValue(bean)
                if (value is Boolean) setDefaultValue(value)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error getting value for ${prop.fieldName}", e)
            }
            isIconSpaceReserved = false
        }
    }

    private fun createPreference(context: Context, beanId: String, bean: Any, prop: BeanPropertyInfo, fallbackTitle: String?, fallbackDescription: String?): Preference? {
        val preferenceKey = prop.id

        val label = prop.resolveLabel(context) ?: fallbackTitle ?: prop.name
        val titleText = label.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        val summaryText = prop.resolveDescription(context)?.takeIf { it.isNotEmpty() } ?: fallbackDescription

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
                        Log.e("SettingsFragment", "Error getting value for ${prop.fieldName}", e)
                    }
                    isIconSpaceReserved = false
                }
            }
            else -> {
                val staticValues = prop.values
                val valuesMethod = prop.valuesMethod
                val hasValues = valuesMethod != null || (staticValues != null && staticValues.isNotEmpty()) || !prop.resolveValues(context).isNullOrEmpty()
                
                if (hasValues) {
                    ListPreference(context).apply {
                        key = preferenceKey
                        title = titleText
                        summary = summaryText ?: "%s"
                        isIconSpaceReserved = false
                        setupListPreference(context, this, bean, prop)
                    }
                } else null
            }
        }
    }

    /**
     * Setup a list preference with i18n support for values.
     */
    private fun setupListPreference(context: Context, pref: ListPreference, bean: Any, prop: BeanPropertyInfo) {
        val values = getPropertyValues(context, bean, prop)
        val names = getPropertyNames(context, prop, values)

        // check
        if (values.size != names.size) throw IllegalStateException("Values and names must have the same size. Property: $prop.  Values: $values. Names: $names")

        // arrays
        var resolveValues = prop.resolveValues(context)?.map { it as String }

        // check
        if (resolveValues != null){
            if (!resolveValues.containsAll(values) || !values.containsAll(resolveValues)) {
                throw IllegalStateException("Values and resolveValues must be equal. Property: $prop.  Values: $values. ResolveValues: $resolveValues")
            }
        } else {
            resolveValues = values
        }

        pref.entries = names.toTypedArray()
        pref.entryValues = resolveValues.toTypedArray()

        val currentValue = try { prop.getValue(bean) } catch (e: Exception) { null }
        val currentIndex = resolveValues.indexOfFirst { areEqual(it, currentValue) }
        if (currentIndex != -1) {
            pref.value = resolveValues[currentIndex].toString()
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
        val engine = modelEngineViewModel.activeEngine.value ?: return
        
        applyPreferenceToEngine(requireContext(), engine.beanFactory, sharedPreferences, key)
    }

    companion object {

        fun applySavedPreferences(engine: ModelEngine, context: Context) {
            Log.d("SettingsFragment", "Restoring preferences...")

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val beanFactory = engine.beanFactory
            sharedPreferences.all.keys.forEach { key ->
                if (key.contains(".")) applyPreferenceToEngine(context, beanFactory, sharedPreferences, key)
            }

            Log.i("SettingsFragment", "Finished restoring preferences.")
        }

        private fun applyPreferenceToEngine(context: Context, beanFactory: BeanFactory, sharedPreferences: SharedPreferences, key: String) {
            val beanId = key.substringBeforeLast(".")
            val propertyName = key.substringAfterLast(".")
            
            val beanClass = try { Class.forName(beanId) } catch (e: Exception) { null } ?: return
            val bean = beanFactory.find(beanClass) ?: return

            try {
                val properties = beanFactory.getProperties(bean)
                val info = properties[key] ?: return

                if (info.type == Boolean::class.java || info.type == java.lang.Boolean.TYPE) {
                    if (sharedPreferences.contains(key)) {
                        info.setValue(bean, sharedPreferences.getBoolean(key, false))
                    }
                    return
                }

                val valueStr = sharedPreferences.getString(key, null) ?: return
                
                val values = getPropertyValues(context, bean, info)

                var index = values.indexOf(valueStr)
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

        private fun getPropertyValues(context: Context, bean: Any, info: BeanPropertyInfo): List<String> {
            return try {
                // Priority 1: Method provider
                info.valuesMethod?.invoke(bean)?.let {
                    return (it as List<*>).filterNotNull() as List<String>
                }
                
                // Priority 2: Static values in annotation
                if (info.values != null && info.values.isNotEmpty()) {
                    return info.values.toList()
                }

                // Priority 3: arrays.xml convention
                info.resolveValues(context)?.let {
                    return it.toList()
                }

                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

        /**
         * Resolves the localized names for property values.
         * Falls back to string representation of the value if no localized labels are found.
         */
        private fun getPropertyNames(context: Context, info: BeanPropertyInfo, values: List<Any>): List<CharSequence> {
            // Priority 1: Check for _values_descriptions array in arrays.xml
            val descriptions = info.resolveDescriptions(context)
            if (!descriptions.isNullOrEmpty()) {
                return descriptions.map { it as CharSequence }.toList()
            }

            // Priority 2: Check for _values array in arrays.xml (use them as labels if no descriptions found)
            val resolvedLabels = info.resolveValues(context)
            if (!resolvedLabels.isNullOrEmpty()) {
                return resolvedLabels.map { it as CharSequence }.toList()
            }
            
            // Priority 3: Lookup individual strings or fallback to ID
            return values.map { value ->
                info.resolveValueLabel(context, value.toString()) as CharSequence
            }.toList()
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
