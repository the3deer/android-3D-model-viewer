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

    public void setLanguage(String language) {
        this.language = language;
    }
}
