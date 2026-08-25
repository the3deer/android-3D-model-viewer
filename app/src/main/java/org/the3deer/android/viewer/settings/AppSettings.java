package org.the3deer.android.viewer.settings;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import org.the3deer.util.bean.Bean;
import org.the3deer.util.bean.BeanProperty;

/**
 * Application-level settings state and Android OS bridge. It holds the following settings:
 * <ul>
 *     <li>Language</li>
 *     <li>Theme</li>
 *     <li>AI key</li>
 *     <li>AI model</li>
 * </ul>
 */
@Bean(name = "settings", category = "general")
public class AppSettings {

    private static final String TAG = AppSettings.class.getSimpleName();

    @BeanProperty(values = {"en", "es"})
    private String language = "en";

    @BeanProperty(values = {"auto", "light", "dark"})
    private String theme = "auto";

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        if (language == null) throw new IllegalArgumentException("Language can't be null");
        this.language = language;
        setAndroidLanguage();
    }

    public void setAndroidLanguage() {
        final LocaleListCompat appLocales = LocaleListCompat.forLanguageTags(language);
        if (appLocales.equals(AppCompatDelegate.getApplicationLocales())) {
            return;
        }

        Log.i(TAG, "System bridge: Switching to " + language);
        new Handler(Looper.getMainLooper()).post(() -> {
            AppCompatDelegate.setApplicationLocales(appLocales);
            Log.i(TAG, "System bridge: Switched to " + language);
        });
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(final String theme) {
        if (theme == null) throw new IllegalArgumentException("Theme can't be null");
        this.theme = theme;
        setAndroidTheme();
    }

    public void setAndroidTheme() {
        final int mode = getMode(theme);
        if (AppCompatDelegate.getDefaultNightMode() == mode) {
            return;
        }

        Log.i(TAG, "System bridge: Switching theme to " + theme);
        new Handler(Looper.getMainLooper()).post(() -> {
            AppCompatDelegate.setDefaultNightMode(mode);
            Log.i(TAG, "System bridge: Switched theme to " + theme);
        });
    }

    private static int getMode(final String theme) {
        if ("light".equals(theme)) return AppCompatDelegate.MODE_NIGHT_NO;
        if ("dark".equals(theme)) return AppCompatDelegate.MODE_NIGHT_YES;
        return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    }
}
