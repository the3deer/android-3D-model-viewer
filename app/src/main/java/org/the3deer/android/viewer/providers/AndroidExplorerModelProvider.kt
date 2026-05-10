package org.the3deer.android.viewer.providers

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import org.the3deer.android.viewer.providers.ModelProvider
import java.net.URI

class AndroidExplorerModelProvider : ModelProvider {

    override fun list(): Any {
        return emptyList<URI>()
    }

    override fun load(activity: Activity, callback: ModelProvider.Callback) {
        if (activity is FragmentActivity) {
            val result = Bundle()
            result.putString("action", "pick")
            activity.supportFragmentManager.setFragmentResult("app", result)
            // Note: The actual picking happens in MainActivity via the fragment result listener
        }
    }

    override fun resolve(id: String): URI? {
        return null
    }
}
