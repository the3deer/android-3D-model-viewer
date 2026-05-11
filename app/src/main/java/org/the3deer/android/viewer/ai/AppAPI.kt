package org.the3deer.android.viewer.ai

import org.the3deer.android.engine.Model

/**
 * Agnostic contract for interacting with the Android Application.
 * Allows the AI to navigate fragments, change settings, and manage models.
 */
interface AppAPI {

    /**
     * Navigates to a specific screen (Home, Settings, About, Load).
     */
    fun navigate(screenId: String)

    /**
     * Loads a specific 3D model with technical hints.
     */
    fun loadModel(model: Model)

    /**
     * Changes application-wide settings (language, theme).
     */
    fun setAppSetting(key: String, value: Any)

    /**
     * Closes the application.
     */
    fun exitApp()

    /**
     * Return the list of available model providers.
     */
    fun getProviders(): List<String>

    /**
     * Return the list or hierarchy of available models for a specific provider.
     */
    fun listModels(provider: String): Any

    /**
     * Resolves a model URI to its metadata for a specific provider.
     */
    fun resolveModel(provider: String, uri: java.net.URI): Model?
}
