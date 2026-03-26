package org.the3deer.android.viewer

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.the3deer.android.util.ContentUtils
import org.the3deer.android.viewer.ui.load.LoadContentDialog
import org.the3deer.dddmodel2.R
import org.the3deer.dddmodel2.databinding.ActivityMainBinding
import androidx.core.net.toUri
import org.the3deer.android.engine.model.ModelEvent
import org.the3deer.android.viewer.ui.dialogs.SceneDialogFragment
import org.the3deer.android.viewer.ui.dialogs.CameraDialogFragment
import org.the3deer.android.viewer.ui.dialogs.AnimationDialogFragment
import org.the3deer.util.event.EventListener
import java.util.EventObject

class MainActivity : AppCompatActivity(), EventListener {

    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isImmersiveMode = false
    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var loadContentDialog: LoadContentDialog
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d(TAG, "Picked URI: $uri")
        uri?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    loadContentDialog.load(it)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.e(TAG, "Error loading uri: $it", e)
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ContentUtils with context for file operations
        ContentUtils.setContext(this)
        loadContentDialog = LoadContentDialog(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the window draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.immersive.setOnClickListener {
            toggleImmersiveMode()
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(MainActivity::class.java.name+".immersive", isImmersiveMode)
                .apply()
        }

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_settings, R.id.nav_about),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navView?.let { navigationView ->
            navigationView.setNavigationItemSelectedListener { item ->
                when (item.groupId) {
                    R.id.group_recent -> {
                        val uriString = MenuItemCompat.getTooltipText(item)?.toString() ?: item.title.toString().lowercase()
                        when (uriString) {
                            "triangle" -> sharedViewModel.loadTriangle()
                            "cube" -> sharedViewModel.loadCube()
                            "square" -> sharedViewModel.loadSquare()
                            else -> {
                                sharedViewModel.setActiveFragment(uriString)
                                val arguments = Bundle()
                                arguments.putString("uri", uriString)
                                navController.navigate(R.id.nav_home, arguments)
                            }
                        }
                        binding.drawerLayout?.closeDrawers()
                        true
                    }
                    else -> {
                        when (item.itemId) {
                            R.id.nav_load, R.id.nav_settings -> {
                                navController.navigate(item.itemId)
                                binding.drawerLayout?.closeDrawers()
                                true
                            }
                            else -> {
                                val handled = NavigationUI.onNavDestinationSelected(item, navController)
                                if (handled) {
                                    binding.drawerLayout?.closeDrawers()
                                }
                                handled
                            }
                        }
                    }
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.nav_home) {
                    sharedViewModel.activeFragment.value?.let {
                        supportActionBar?.title = shortenUri(it)
                    }
                }
                if (destination.id == R.id.nav_home) {
                    navigationView.setCheckedItem(destination.id)
                }
            }

            sharedViewModel.history.observe(this) { history ->
                updateRecentModels(navigationView, history)
            }

            sharedViewModel.activeFragment.observe(this) { uriString ->
                if (navController.currentDestination?.id == R.id.nav_home) {
                    supportActionBar?.title = shortenUri(uriString)
                }
            }
        }

        // Action stack buttons setup
        binding.appBarMain.btnScene.setOnClickListener {
            SceneDialogFragment().show(supportFragmentManager, "scene_dialog")
        }
        binding.appBarMain.btnCamera.setOnClickListener {
            CameraDialogFragment().show(supportFragmentManager, "camera_dialog")
        }
        binding.appBarMain.btnAnimation.setOnClickListener {
            AnimationDialogFragment().show(supportFragmentManager, "animation_dialog")
        }

        // Note: visibility of individual buttons is managed by sharedViewModel observer
        sharedViewModel.activeEngine.observe(this) { engine ->
            Log.i(TAG, "Updating overlay buttons visibility for engine: $engine")
            refreshOverlayButtons()
        }

        // Handle fragment results
        supportFragmentManager.setFragmentResultListener("app", this) { _, bundle ->
            val action = bundle.getString("action")
            when (action) {
                "pick" -> loadContentDialog.start()
                "load" -> {
                    val uri = bundle.getString("uri")
                    if (uri != null) {
                        sharedViewModel.setActiveFragment(uri)
                        navController.navigate(R.id.nav_home, bundle)
                    }
                }
                "navigate" -> {
                    val viewId = bundle.getInt("view")
                    navController.navigate(viewId, bundle)
                }
            }
        }

        applyInitialImmersiveMode()
    }

    private fun refreshOverlayButtons() {
        if (isImmersiveMode) return

        val engine = sharedViewModel.activeEngine.value
        val model = engine?.beanFactory?.get("model", org.the3deer.android.engine.model.Model::class.java)
        val scene = model?.activeScene

        runOnUiThread {
            binding.appBarMain.btnScene.isEnabled = (model?.scenes?.size ?: 0) > 1
            binding.appBarMain.btnCamera.isEnabled = (scene?.cameras?.size ?: 0) > 1
            binding.appBarMain.btnAnimation.isEnabled = (scene?.animations?.size ?: 0) > 1
            
            binding.appBarMain.btnScene.alpha = if (binding.appBarMain.btnScene.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnCamera.alpha = if (binding.appBarMain.btnCamera.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnAnimation.alpha = if (binding.appBarMain.btnAnimation.isEnabled) 1.0f else 0.5f

            Log.i(TAG, "Overlay buttons refreshed. Scenes: ${model?.scenes?.size}, Cameras: ${scene?.cameras?.size}, Animations: ${scene?.animations?.size}")
        }
    }

    override fun onEvent(event: EventObject?): Boolean {
        if (event is ModelEvent && event.code == ModelEvent.Code.LOADED) {
            Log.i(TAG, "Model loaded event received. Refreshing overlay buttons.")
            refreshOverlayButtons()
            return true
        }
        return false
    }

    fun pick(mimeType: String) {
        getContent.launch(mimeType)
    }

    private fun updateRecentModels(navigationView: NavigationView, history: List<String>) {
        val menu = navigationView.menu
        menu.removeGroup(R.id.group_recent)

        history.forEachIndexed { index, uriString ->
            val title = shortenUri(uriString)
            menu.add(R.id.group_recent, Menu.NONE, index, title).apply {
                icon = ContextCompat.getDrawable(this@MainActivity, getIconResForModel(uriString))
                MenuItemCompat.setTooltipText(this, uriString)
            }
        }
    }

    private fun shortenUri(uriString: String): String {
        if (uriString == "triangle" || uriString == "cube" || uriString == "square") {
            return uriString.replaceFirstChar { it.uppercase() }
        }
        try {
            val uri = uriString.toUri()
            val path = uri.lastPathSegment
            if (path != null) {
                return if (path.length > 20) "..." + path.substring(path.length - 17) else path
            }
        } catch (e: Exception) { /* ignore */ }
        return if (uriString.length > 25) "..." + uriString.substring(uriString.length - 22) else uriString
    }

    private fun getIconResForModel(modelName: String): Int {
        return when (modelName) {
            "cube" -> android.R.drawable.ic_menu_help
            "square" -> android.R.drawable.ic_menu_crop
            else -> android.R.drawable.ic_menu_compass
        }
    }

    private fun applyInitialImmersiveMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean(MainActivity::class.java.name+".immersive", false)) {
            isImmersiveMode = false
            toggleImmersiveMode()
        }
    }

    private fun toggleImmersiveMode() {
        isImmersiveMode = !isImmersiveMode
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isImmersiveMode) {
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            supportActionBar?.hide()
            //binding.appBarMain.uiActionsStack.visibility = View.GONE
            binding.appBarMain.immersive.setImageResource(android.R.drawable.ic_menu_revert)
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            supportActionBar?.show()
            //binding.appBarMain.uiActionsStack.visibility = View.VISIBLE
            binding.appBarMain.immersive.setImageResource(android.R.drawable.ic_menu_add)
            refreshOverlayButtons()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        if (findViewById<View>(R.id.nav_view) == null) {
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_settings) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}