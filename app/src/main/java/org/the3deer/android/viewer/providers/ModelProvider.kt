package org.the3deer.android.viewer.providers

import android.app.Activity
import org.the3deer.android.engine.Model
import java.net.URI

/**
 * Interface for 3D model repositories.
 */
interface ModelProvider {

    interface Callback {
        fun onModelSelected(model: Model?)
    }

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
     * Resolves a model URI to its fully populated entity.
     */
    fun resolve(uri: URI): Model?
}
