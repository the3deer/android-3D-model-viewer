package org.the3deer.android.viewer.ui.settings;

import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

@Bean(name = "settings", category = "general")
public class SettingsOptions {

    @BeanProperty(values = {"en", "es"})
    private String language = "en";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) throws Exception {

        // check
        if (language == null) throw new Exception("Language can't be null");

        this.language = language;

        // call android bridge
        setAndroidLanguage();
    }

    public void setAndroidLanguage(){

        final String languageCode = language.equals("en") ? "en-US" : "es-ES";

        Log.i("SettingsOptions", "System bridge: Switching to "+languageCode);

        LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocales);

        Log.i("SettingsOptions", "System bridge: Switching to "+languageCode);
    }
}
