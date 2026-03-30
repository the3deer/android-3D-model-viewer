package org.the3deer.android.viewer.ui.settings;

import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

@Bean(name = "settings", category = "general")
public class SettingsOptions {

    @BeanProperty(values = {"en", "es", "ru", "zh", "de", "fr", "hi", "it", "ar", "pt", "ja", "tr", "id", "uk"})
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

        final String languageCode;
        switch (language) {
            case "es": languageCode = "es"; break;
            case "ru": languageCode = "ru"; break;
            case "zh": languageCode = "zh"; break;
            case "de": languageCode = "de"; break;
            case "fr": languageCode = "fr"; break;
            case "hi": languageCode = "hi"; break;
            case "it": languageCode = "it"; break;
            case "ar": languageCode = "ar"; break;
            case "pt": languageCode = "pt"; break;
            case "ja": languageCode = "ja"; break;
            case "tr": languageCode = "tr"; break;
            case "id": languageCode = "id"; break;
            case "uk": languageCode = "uk"; break;
            default: languageCode = "en";
        }

        Log.i("SettingsOptions", "System bridge: Switching to " + languageCode);

        final LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(languageCode);
        AppCompatDelegate.setApplicationLocales(appLocales);

        Log.i("SettingsOptions", "System bridge: Switched to " + languageCode);
    }
}
