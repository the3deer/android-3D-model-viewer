package org.the3deer.android.viewer.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.fragment.app.activityViewModels
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.engine.shader.ShaderManager
import org.the3deer.android.viewer.R
import org.the3deer.android.viewer.SharedViewModel
import org.the3deer.util.bean.BeanInfo
import org.the3deer.util.bean.BeanPropertyInfo

/**
 * Fragment for displaying and managing application settings.
 *
 * @author andresoviedo
 * @author Gemini AI
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    val TAG: String = SettingsFragment::class.java.simpleName

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        setPreferenceScreen(screen)

        // Get hydrated and grouped settings from the manager
        val engineBeans = ModelEngineViewModel.INSTANCE?.api?.managedBeans ?: emptyList()
        val appBeans = sharedViewModel.settings.getAppBeans(context)
        val settings = sharedViewModel.settings.getSettings(context, appBeans + engineBeans)

        settings.forEach { (categoryName, categoryBeans) ->
            val category = PreferenceCategory(context).apply {
                title = categoryName
                summary = categoryBeans.firstOrNull()?.categoryDescription
                layoutResource = R.layout.preference_category
                isIconSpaceReserved = false
            }
            screen.addPreference(category)

            categoryBeans.forEach { bean ->
                val enabledProp = getEnabledProperty(bean)
                val otherProps = getOtherProperties(bean)

                var masterDependencyKey: String? = null

                // Always add a header for the bean if it has a description to ensure it's visible.
                // This header acts as the component title and description.
                if (bean.description != null && !bean.description.isEmpty()) {
                    category.addPreference(Preference(context).apply {
                        layoutResource = R.layout.preference_category
                        title = bean.name
                        summary = bean.description
                        isSelectable = false
                        isIconSpaceReserved = false
                    })
                }

                if (enabledProp != null) {
                    val masterSwitch = createSwitchPreference(context, enabledProp)
                    
                    // If we already added a header with the same name, rename the switch to "Enabled"
                    if (bean.description != null && !bean.description.isEmpty()) {
                        masterSwitch.title = context.getString(R.string.enabled)
                    }
                    
                    category.addPreference(masterSwitch)
                    masterDependencyKey = masterSwitch.key
                } else if (bean.description == null || bean.description.isEmpty()) {
                    // If no enabled prop AND no description, we still need a header to show the bean name
                    category.addPreference(Preference(context).apply {
                        layoutResource = R.layout.preference_category
                        title = bean.name
                        isSelectable = false
                        isIconSpaceReserved = false
                    })
                }

                otherProps.forEach { prop ->
                    createPreference(context, prop)?.let { pref ->
                        category.addPreference(pref)
                        if (masterDependencyKey != null) {
                            pref.dependency = masterDependencyKey
                        }
                    }
                }
            }
        }
    }

    private fun getEnabledProperty(bean: BeanInfo): BeanPropertyInfo? {
        return bean.properties.values.find { "enabled" == it.fieldName }
    }

    private fun getOtherProperties(bean: BeanInfo): List<BeanPropertyInfo> {
        return bean.properties.values.filter { "enabled" != it.fieldName }
    }

    private fun createSwitchPreference(context: Context, prop: BeanPropertyInfo): SwitchPreferenceCompat {
        return SwitchPreferenceCompat(context).apply {
            key = prop.id
            title = prop.label
            summary = prop.description
            
            // Safety Check: If data is corrupted in SharedPreferences (String instead of Boolean),
            // migrate it to Boolean before the UI attempts to read it.
            val sharedPrefs = this@SettingsFragment.preferenceManager.sharedPreferences
            if (sharedPrefs != null && sharedPrefs.all[prop.id] is String) {
                val corruptedValue = sharedPrefs.getString(prop.id, "false")
                Log.w("SettingsFragment", "Migrating corrupted Boolean preference for key: ${prop.id}")
                sharedPrefs.edit().remove(prop.id).putBoolean(prop.id, corruptedValue.toBoolean()).apply()
            }

            try {
                val value = prop.value
                if (value is Boolean) setDefaultValue(value)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error getting value for ${prop.fieldName}", e)
            }
            isIconSpaceReserved = false
        }
    }

    private fun createPreference(context: Context, prop: BeanPropertyInfo): Preference? {
        val preferenceKey = prop.id
        val titleText = prop.label
        val summaryText = prop.description

        return when (prop.type) {
            Boolean::class.java, java.lang.Boolean.TYPE -> {
                createSwitchPreference(context, prop)
            }
            else -> {
                val hasValues = !prop.options.isNullOrEmpty() || prop.valuesMethod != null
                
                if (hasValues) {
                    ListPreference(context).apply {
                        key = preferenceKey
                        title = titleText
                        summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
                        isIconSpaceReserved = false
                        setupListPreference(this, prop)
                    }
                } else {
                    // Fallback to text entry for strings/numbers with no static values
                    EditTextPreference(context).apply {
                        key = preferenceKey
                        title = titleText
                        summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
                        isIconSpaceReserved = false
                        try {
                            val value = prop.value
                            if (value != null) setDefaultValue(value.toString())
                        } catch (e: Exception) {
                            Log.e("SettingsFragment", "Error getting value for ${prop.fieldName}", e)
                        }
                    }
                }
            }
        }
    }

    /**
     * Setup a list preference with i18n support for values.
     */
    private fun setupListPreference(pref: ListPreference, prop: BeanPropertyInfo) {
        val values = prop.options ?: emptyList()
        val names = if (prop.optionLabels.isNullOrEmpty()) values else prop.optionLabels

        // check
        if (values.size != names.size) throw IllegalStateException("Values and names must have the same size. Property: $prop.  Values: $values. Names: $names")

        pref.entries = names.map { it.toString() }.toTypedArray()
        pref.entryValues = values.toTypedArray()

        val currentValue = prop.value?.toString()
        val currentIndex = values.indexOfFirst { it == currentValue }
        if (currentIndex != -1) {
            pref.value = values[currentIndex]
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

        val value = sharedPreferences.all[key]
        if (value != null) {
            sharedViewModel.api.updateSetting(key, value)
        }

        // Special case: OpenGL Version change requires activity restart
        if (key == ShaderManager::class.java.name + ".openGLVersion") {
            val bundle = Bundle()
            bundle.putString("action", "restart")
            requireActivity().supportFragmentManager.setFragmentResult("app", bundle)
        }
    }

    companion object {

        fun applySavedPreferences(engine: ModelEngine, context: Context) {
            Log.d("SettingsFragment", "Restoring preferences...")

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.all.keys.forEach { key ->
                if (key.contains(".")) {
                    val value = sharedPreferences.all[key]
                    if (value != null) engine.setConfig(key, value)
                }
            }

            Log.i("SettingsFragment", "Finished restoring preferences.")
        }

        /**
         * Applies global preferences like Theme and Language at the Activity level.
         * Should be called in MainActivity.onCreate.
         */
        fun applyGlobalPreferences(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            // Apply language
            val languageKey = AppSettings::class.java.name + ".language"
            val language = sharedPreferences.getString(languageKey, null)
            if (language != null) {
                val appLocales = LocaleListCompat.forLanguageTags(language)
                if (appLocales != AppCompatDelegate.getApplicationLocales()) {
                    AppCompatDelegate.setApplicationLocales(appLocales)
                }
            }

            // Apply theme
            val themeKey = AppSettings::class.java.name + ".theme"
            val theme = sharedPreferences.getString(themeKey, null)
            if (theme != null) {
                val mode = when (theme) {
                    "light" -> AppCompatDelegate.MODE_NIGHT_NO
                    "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                if (AppCompatDelegate.getDefaultNightMode() != mode) {
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }
    }
}
