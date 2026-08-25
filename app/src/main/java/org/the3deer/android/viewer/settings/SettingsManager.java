package org.the3deer.android.viewer.settings;

import android.content.Context;
import android.util.Log;

import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.api.EngineAPI;
import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanInfo;
import org.the3deer.util.bean.BeanManager;
import org.the3deer.util.bean.BeanPropertyInfo;
import org.the3deer.util.bean.BeanUtils;
import org.the3deer.util.bean.Feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager that holds application settings, decorates technical engine metadata 
 * with localized UI strings, and handles application preference persistence.
 */
public class SettingsManager {

    private static final String TAG = SettingsManager.class.getSimpleName();

    private final AppSettings appSettings;
    private final Map<String, BeanPropertyInfo> appProperties;
    private final SharedViewModel model;

    public SettingsManager(SharedViewModel model, AppSettings appSettings) {
        this.model = model;
        this.appSettings = appSettings;

        // Pre-discover app settings using BeanManager
        this.appProperties = BeanManager.getProperties(appSettings);

        // Restore saved settings
        restore(model.getApplication());
    }

    private void restore(Context context) {
        final android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        for (BeanPropertyInfo prop : appProperties.values()) {
            try {
                final String key = prop.getId();
                if (prefs.contains(key)) {
                    Object value = prefs.getAll().get(key);
                    if (value != null) {
                        prop.setValue(appSettings, value);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error restoring setting: " + prop.getId(), e);
            }
        }
    }

    // --- Management & UI Hydration ---

    /**
     * Returns the beans grouped by category for the settings screen.
     */
    public Map<String, List<BeanInfo>> getSettings(Context context, List<BeanInfo> allBeans) {
        final List<BeanInfo> hydratedBeans = hydrate(context, allBeans);
        final Map<String, List<BeanInfo>> grouped = new LinkedHashMap<>();
        for (BeanInfo bean : hydratedBeans) {
            if (bean.getProperties().isEmpty()) continue;
            
            String category = bean.getCategory();
            if (category == null || category.isEmpty()) category = "General";
            
            if (!grouped.containsKey(category)) {
                grouped.put(category, new ArrayList<>());
            }
            grouped.get(category).add(bean);
        }
        return grouped;
    }

    /**
     * Sets a preference value and persists it to SharedPreferences.
     */
    public void setPreference(Context context, String key, Object value) {
        Log.d(TAG, "Setting preference: " + key + " = " + value);

        // 1. Type Correction: If value is a String, attempt to convert to the intended type
        if (value instanceof String) {
            BeanPropertyInfo prop = findProperty(key);
            if (prop != null) {
                try {
                    final Class<?> type = prop.getType();
                    if (type == Boolean.class || type == boolean.class) {
                        value = Boolean.parseBoolean((String) value);
                    } else if (type == Integer.class || type == int.class) {
                        value = Integer.parseInt((String) value);
                    } else if (type == Float.class || type == float.class) {
                        value = Float.parseFloat((String) value);
                    } else if (type == Long.class || type == long.class) {
                        value = Long.parseLong((String) value);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Failed to convert string value for key: " + key, e);
                }
            }
        }

        // 2. Persist to SharedPreferences
        final android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        final android.content.SharedPreferences.Editor editor = prefs.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();

        // 3. Update in-memory state if it's an app setting
        final BeanPropertyInfo prop = appProperties.get(key);
        if (prop != null) {
            try {
                Log.d(TAG, "Setting value: " + key + " = " + value);
                prop.setValue(appSettings, value);
            } catch (Exception e) {
                Log.e(TAG, "Error updating app setting: " + key, e);
            }
        }

        // 4. Propagate to engine if active
        final EngineAPI engineApi = ModelEngineViewModel.INSTANCE != null ? ModelEngineViewModel.INSTANCE.getApi() : null;
        if (engineApi != null) {
            engineApi.setConfig(key, value);
        }

        // 5. Special cases
        if (key.endsWith(".openGLVersion") && model != null) {
            model.requestRestart();
        }
    }

    private BeanPropertyInfo findProperty(String key) {
        if (appProperties.containsKey(key)) return appProperties.get(key);
        
        final EngineAPI engineApi = ModelEngineViewModel.INSTANCE != null ? ModelEngineViewModel.INSTANCE.getApi() : null;
        if (engineApi != null) {
            for (BeanInfo bean : engineApi.getManagedBeans()) {
                if (bean.getProperties().containsKey(key)) {
                    return bean.getProperties().get(key);
                }
            }
        }
        return null;
    }

    /**
     * Hydrates the given list of beans with localized resources.
     * This process is idempotent because it derives technical names from stable IDs.
     */
    public List<BeanInfo> hydrate(Context context, List<BeanInfo> beans) {
        for (BeanInfo bean : beans) {
            try {
                final Class<?> beanClass = Class.forName(bean.getId());
                final Bean beanAnn = beanClass.getAnnotation(Bean.class);
                
                // Technical bean name is derived from class/annotation, NOT from the mutable name field
                final String technicalBeanName = (beanAnn != null && !beanAnn.name().isEmpty()) 
                        ? beanAnn.name() : BeanUtils.getSnakeCase(beanClass);

                // 1. Hydrate bean metadata
                final String localizedBeanLabel = resolveBeanLabel(context, technicalBeanName);
                bean.setName(isExperimental(beanClass) ? localizedBeanLabel + " (Experimental)" : localizedBeanLabel);
                bean.setDescription(getDescription(context, technicalBeanName));
                bean.setCategory(getCategory(context, beanClass));

                // 2. Hydrate all properties
                for (Object entryValue : bean.getProperties().values()) {
                    final BeanPropertyInfo prop = (BeanPropertyInfo) entryValue;
                    
                    // Technical property name is the original name before hydration
                    final String technicalPropertyName = prop.getName();
                    
                    // Hydrate labels
                    String localizedName = resolvePropertyLabel(context, technicalBeanName, technicalPropertyName);
                    String localizedDescription = resolvePropertyDescription(context, technicalBeanName, technicalPropertyName);
                    
                    // Fallback for 'enabled' toggle
                    if ("enabled".equals(technicalPropertyName)) {
                        if (localizedName == null || localizedName.isEmpty() || "enabled".equals(localizedName)) {
                            localizedName = bean.getName();
                        }
                        if (localizedDescription == null || localizedDescription.isEmpty()) {
                            localizedDescription = bean.getDescription();
                        }
                    }

                    if (localizedName != null) prop.setLabel(localizedName);
                    if (localizedDescription != null) prop.setDescription(localizedDescription);

                    // Hydrate List selection options (Technical Value -> UI Label)
                    prop.setOptions(getPropertyValues(context, technicalBeanName, technicalPropertyName, prop));
                    prop.setOptionLabels(getPropertyNames(context, technicalBeanName, technicalPropertyName, prop.getOptions()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error hydrating bean: " + bean.getId(), e);
            }
        }
        return beans;
    }

    /**
     * Returns the app-level beans (e.g. this manager itself).
     */
    public List<BeanInfo> getAppBeans(Context context) {
        final List<BeanInfo> beans = new ArrayList<>();
        
        final Bean beanAnn = appSettings.getClass().getAnnotation(Bean.class);
        final String beanName = (beanAnn != null && !beanAnn.name().isEmpty()) ? beanAnn.name() : "settings";
        final String category = (beanAnn != null && !beanAnn.category().isEmpty()) ? beanAnn.category() : "general";

        final BeanInfo info = new BeanInfo(appSettings.getClass().getName(), beanName, null);
        info.setCategory(category);
        
        // Add discovered properties
        for (BeanPropertyInfo prop : appProperties.values()) {
            try {
                // Always sync current value to info before UI uses it
                prop.setValue(prop.getValue(appSettings));

                // Populate dynamic options if any
                final String[] values = prop.getValues(appSettings);
                if (values != null) {
                    prop.setOptions(java.util.Arrays.asList(values));
                }
            } catch (Exception ignored) {}
            info.addProperty(prop.getId(), prop);
        }

        beans.add(info);
        return hydrate(context, beans);
    }

    // --- UI Helpers & Localization ---

    public static String resolveBeanLabel(Context context, String beanName) {
        int resId = context.getResources().getIdentifier("bean_" + beanName + "_label", "string", context.getPackageName());
        if (resId != 0) return context.getString(resId);
        return beanName;
    }

    public static String getDescription(Context context, String beanName) {
        int resId = context.getResources().getIdentifier("bean_" + beanName + "_description", "string", context.getPackageName());
        if (resId != 0) return context.getString(resId);
        return null;
    }

    public static String resolvePropertyLabel(Context context, String beanName, String propertyName) {
        int resId = context.getResources().getIdentifier("property_" + beanName + "_" + propertyName + "_label", "string", context.getPackageName());
        if (resId != 0) return context.getString(resId);
        
        // fallback to bean convention
        resId = context.getResources().getIdentifier("bean_" + beanName + "_" + propertyName + "_label", "string", context.getPackageName());
        if (resId != 0) return context.getString(resId);
        
        return propertyName;
    }

    public static String resolvePropertyDescription(Context context, String beanName, String propertyName) {
        int resId = context.getResources().getIdentifier("property_" + beanName + "_" + propertyName + "_description", "string", context.getPackageName());
        if (resId != 0) return context.getString(resId);
        return null;
    }

    public static String getCategory(Context context, Class<?> beanClass) {
        String category = null;

        // 1. Check @Bean
        Bean bean = beanClass.getAnnotation(Bean.class);
        if (bean != null && !bean.category().isEmpty()) {
            category = bean.category();
        }

        // 2. Check @Feature on Class
        if (category == null) {
            Feature feature = beanClass.getAnnotation(Feature.class);
            if (feature != null && !feature.category().isEmpty()) {
                category = feature.category();
            }
        }

        // 3. Check @Feature on Package
        if (category == null && beanClass.getPackage() != null) {
            Feature pkgFeature = beanClass.getPackage().getAnnotation(Feature.class);
            if (pkgFeature != null && !pkgFeature.category().isEmpty()) {
                category = pkgFeature.category();
            }
        }

        if (category != null) {
            int resId = context.getResources().getIdentifier("category_" + category + "_label", "string", context.getPackageName());
            if (resId != 0) return context.getString(resId);
            return category;
        }

        return "General";
    }

    public static boolean isExperimental(Class<?> beanClass) {
        Bean bean = beanClass.getAnnotation(Bean.class);
        if (bean != null && bean.experimental()) return true;

        Feature feature = beanClass.getAnnotation(Feature.class);
        if (feature != null && feature.experimental()) return true;

        if (beanClass.getPackage() != null) {
            Feature pkgFeature = beanClass.getPackage().getAnnotation(Feature.class);
            if (pkgFeature != null && pkgFeature.experimental()) return true;
        }

        return false;
    }

    public static List<String> getPropertyValues(Context context, String beanName, String propertyName, BeanPropertyInfo info) {
        if (beanName != null && !beanName.isEmpty()) {
            int resId = context.getResources().getIdentifier("property_" + beanName + "_" + propertyName + "_values", "array", context.getPackageName());
            if (resId != 0) {
                return Arrays.asList(context.getResources().getStringArray(resId));
            }
        }

        if (info.getValues() != null && info.getValues().length > 0) {
            return Arrays.asList(info.getValues());
        }
        
        return Collections.emptyList();
    }

    public static List<CharSequence> getPropertyNames(Context context, String beanName, String propertyName, List<?> values) {
        if (beanName != null && !beanName.isEmpty()) {
            int resId = context.getResources().getIdentifier("property_" + beanName + "_" + propertyName + "_values_descriptions", "array", context.getPackageName());
            if (resId != 0) {
                return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(resId)));
            }
            
            resId = context.getResources().getIdentifier("property_" + beanName + "_" + propertyName + "_values", "array", context.getPackageName());
            if (resId != 0) {
                return new ArrayList<>(Arrays.asList(context.getResources().getStringArray(resId)));
            }
        }
        
        List<CharSequence> ret = new ArrayList<>();
        if (values != null) {
            for (Object value : values) {
                ret.add(resolveValueLabel(context, beanName, propertyName, value.toString()));
            }
        }
        return ret;
    }

    public static String resolveValueLabel(Context context, String beanName, String propertyName, String valueId) {
        if (beanName != null && !beanName.isEmpty()) {
            String sanitizedValueId = valueId.replace(".", "_");
            int resId = context.getResources().getIdentifier("value_" + beanName + "_" + propertyName + "_" + sanitizedValueId, "string", context.getPackageName());
            if (resId != 0) return context.getString(resId);
        }
        return valueId;
    }

    public static boolean areEqual(Object v1, Object v2) {
        if (v1 == v2) return true;
        if (v1 instanceof float[] && v2 instanceof float[]) return Arrays.equals((float[]) v1, (float[]) v2);
        if (v1 != null && v2 != null) return v1.toString().equals(v2.toString());
        return false;
    }
}
