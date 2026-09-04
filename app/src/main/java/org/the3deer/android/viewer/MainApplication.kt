package org.the3deer.android.viewer

import android.app.Application
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
    }
}
