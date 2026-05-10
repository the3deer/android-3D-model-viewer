package org.the3deer.android.viewer

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.the3deer.android.viewer.util.AndroidURLStreamHandlerFactory
import java.net.URL

/**
 * Main Application class.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            URL.setURLStreamHandlerFactory(AndroidURLStreamHandlerFactory())
        } catch (ex: Error) {
            // Already set or error
        }
        
        // Initialize Firebase
        /*FirebaseApp.initializeApp(this)
        
        // Initialize App Check
        val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        
        // Safely check for debug mode without relying on problematic imports during indexing
        val isDebug = (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0

        if (isDebug) {
            firebaseAppCheck.installAppCheckProviderFactory(
                com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            firebaseAppCheck.installAppCheckProviderFactory(
                com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }

        // Initialize Remote Config
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // Fetch once per hour
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Set default values (gemini-2.0-flash-exp is the current stable for Live)
        val defaultValues = mapOf(
            "ai_model_name" to "gemini-2.0-flash-exp"
        )
        remoteConfig.setDefaultsAsync(defaultValues)
        
        // Fetch and activate
        remoteConfig.fetchAndActivate()*/
    }
}
