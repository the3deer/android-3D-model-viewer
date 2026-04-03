package org.the3deer.android.viewer.ui.settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

import java.util.logging.Logger;

@Bean(name = "settings", category = "general")
public class SettingsOptions {

    private static final Logger logger = Logger.getLogger(SettingsOptions.class.getSimpleName());

    @BeanProperty
    private String language = "en";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {

        // check
        if (language == null) throw new IllegalArgumentException("Language can't be null");

        this.language = language;

        // call android bridge
        setAndroidLanguage();
    }

    public void setAndroidLanguage(){

        logger.info("System bridge: Switching to " + language);

        final LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(language);
        AppCompatDelegate.setApplicationLocales(appLocales);

        logger.info("System bridge: Switched to " + language);
    }
}
