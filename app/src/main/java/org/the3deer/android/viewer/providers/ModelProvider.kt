package org.the3deer.android.viewer.providers

import android.app.Activity
import org.the3deer.android.engine.Model
import java.net.URI

fun interface ModelCallback {
    fun onModelSelected(model: Model?)
}

/**
 * Interface for 3D model repositories.
 */
internal interface ModelProvider {

    fun interface Callback : ModelCallback

    /**
     * Returns the list or hierarchy of available models.
     * @return Any (typically List<URI> or Map<String, Any>)
     */
    fun list(): Any

    /**
     * Launch the provider specific UI to select a model.
     */
    fun load(activity: Activity, callback: Callback)

    /**
     * Resolves a model URI to its fully populated entity (e.g.: textures).
     */
    fun resolve(uri: URI): Model?
}
