package org.the3deer.android.viewer.ui.settings;

import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

@Bean(name = "settings", category = "general")
public class SettingsOptions {

    @BeanProperty
    private String language = "en";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) throws Exception {

        // check
        if (language == null) throw new Exception("Language can't be null");

        this.language = language;

        // call android bridge
        setAndroidLanguage();
    }

    public void setAndroidLanguage(){

        Log.i("SettingsOptions", "System bridge: Switching to " + language);

        final LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(language);
        AppCompatDelegate.setApplicationLocales(appLocales);

        Log.i("SettingsOptions", "System bridge: Switched to " + language);
    }
}
