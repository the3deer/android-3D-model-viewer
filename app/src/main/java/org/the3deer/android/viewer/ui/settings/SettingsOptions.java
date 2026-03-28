package org.the3deer.android.viewer.ui.settings;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

@Bean(category = "General", name = "General Settings")
public class SettingsOptions {

    @BeanProperty(name = "language", description = "Application Language", values = {"en", "es"}, valueNames = {"English", "Español"})
    private String language = "en";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) throws Exception {

        // check
        if (language == null) throw new Exception("Invalid language: $language");

        this.language = language;

        // call android bridge
        setAndroidLanguage();
    }

    public void setAndroidLanguage(){

        final String languageCode = language.equals("en") ? "en-US" : "es-ES";

        Log.i("SettingsOptions", "System bridge: Switching to $languageCode");

        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocales);

        Log.i("SettingsOptions", "System bridge: Switching to $languageCode");
    }
}
