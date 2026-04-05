package org.the3deer.android.viewer.ui.settings;

import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.bean.Bean;
import org.the3deer.bean.BeanProperty;

import java.util.logging.Logger;

@Bean(name = "settings", category = "general")
public class SettingsOptions {

    private static final Logger logger = Logger.getLogger(SettingsOptions.class.getSimpleName());

    @BeanProperty(values = {"en","es"})
    private String language = "en";

    @BeanProperty(values = {"auto", "light", "dark"})
    private String theme = "auto";

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

        final LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(language);

        // check
        if (appLocales.equals(AppCompatDelegate.getApplicationLocales())) {
            return;
        }

        logger.info("System bridge: Switching to " + language);

        new Handler(Looper.getMainLooper()).post(() -> {
            AppCompatDelegate.setApplicationLocales(appLocales);
            logger.info("System bridge: Switched to " + language);
        });
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(final String theme) {

        // check
        if (theme == null) throw new IllegalArgumentException("Theme can't be null");

        this.theme = theme;

        // call android bridge
        setAndroidTheme();
    }

    public void setAndroidTheme() {

        final int mode = getMode(theme);

        // check
        if (AppCompatDelegate.getDefaultNightMode() == mode) {
            return;
        }

        logger.info("System bridge: Switching theme to " + theme);

        new Handler(Looper.getMainLooper()).post(() -> {
            AppCompatDelegate.setDefaultNightMode(mode);
            logger.info("System bridge: Switched theme to " + theme);
        });
    }

    private static int getMode(final String theme) {
        if ("light".equals(theme)) return AppCompatDelegate.MODE_NIGHT_NO;
        if ("dark".equals(theme)) return AppCompatDelegate.MODE_NIGHT_YES;
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}
