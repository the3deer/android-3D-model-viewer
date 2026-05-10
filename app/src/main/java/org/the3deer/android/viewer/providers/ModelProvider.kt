package org.the3deer.android.viewer.providers

import android.app.Activity
import java.net.URI

/**
 * Interface for 3D model repositories.
 */
interface ModelProvider {

    interface Callback {
        fun onModelSelected(uri: URI?)
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
     * Resolves a model ID to a loadable URI.
     */
    fun resolve(id: String): URI?
}
