package org.the3deer.android.viewer

import android.content.res.ColorStateList
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
import androidx.core.graphics.Insets
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
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
import androidx.core.net.toUri
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.engine.model.Constants
import org.the3deer.android.engine.model.ModelEvent
import org.the3deer.android.viewer.databinding.ActivityMainBinding
import org.the3deer.android.viewer.ui.dialogs.SceneDialogFragment
import org.the3deer.android.viewer.ui.dialogs.CameraDialogFragment
import org.the3deer.android.viewer.ui.dialogs.AnimationDialogFragment
import org.the3deer.android.viewer.ui.dialogs.ModelInfoDialogFragment
import org.the3deer.util.event.EventListener
import org.the3deer.util.event.EventManager
import java.util.EventObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), EventListener, ContentUtils.ContentResolver {

    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var immersiveMode = false
    
    private val sharedViewModel: SharedViewModel by viewModels()
    private val modelEngineViewModel: ModelEngineViewModel by viewModels()

    private lateinit var loadContentDialog: LoadContentDialog

    // Future to handle synchronous-like URI resolution from background threads
    private var pendingResolution: CompletableFuture<Uri?>? = null

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

    private val resolveContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        Log.d(TAG, "Resolved URI: $uri")
        pendingResolution?.complete(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ContentUtils with context and resolver for file operations
        ContentUtils.setContext(this)
        ContentUtils.setContentResolver(this)
        loadContentDialog = LoadContentDialog(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the window draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Listen for system bar insets to update the engine's safe area
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // Update the shared screen object in the engine view model
            updateScreenInsets(insets)
            
            windowInsets
        }

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.immersive.setOnClickListener {
            setImmersiveMode(!this.immersiveMode)
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(MainActivity::class.java.name+".immersive", immersiveMode)
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
                        
                        // Set URI and navigate
                        val arguments = Bundle()
                        arguments.putString("uri", uriString)
                        navController.navigate(R.id.nav_home, arguments)
                        
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
        binding.appBarMain.btnInfo.setOnClickListener {
            ModelInfoDialogFragment().show(supportFragmentManager, "model_info_dialog")
        }
        binding.appBarMain.btnScene.setOnClickListener {
            SceneDialogFragment().show(supportFragmentManager, "scene_dialog")
        }
        binding.appBarMain.btnCamera.setOnClickListener {
            CameraDialogFragment().show(supportFragmentManager, "camera_dialog")
        }
        binding.appBarMain.btnAnimation.setOnClickListener {
            AnimationDialogFragment().show(supportFragmentManager, "animation_dialog")
        }

        // Monitor active engine to refresh UI buttons
        var lastEngine: ModelEngine? = null
        modelEngineViewModel.activeEngine.observe(this) { engine ->
            Log.i(TAG, "Active engine changed. id: ${engine?.id}")

            supportActionBar?.title = shortenUri(engine.model.name)

            // Unregister from previous engine
            lastEngine?.remove(Constants.BEAN_ID_CONTEXT, this)

            // Register with new engine
            engine?.add(Constants.BEAN_ID_CONTEXT, this)

            lastEngine = engine
            refreshOverlayButtons()
            ViewCompat.requestApplyInsets(binding.root)
        }

        // Monitor memory status to update Info button color
        modelEngineViewModel.memoryStatus.observe(this) { status ->
            val color = when (status) {
                ModelEngineViewModel.MemoryStatus.OK -> ContextCompat.getColor(this, R.color.design_default_color_secondary)
                ModelEngineViewModel.MemoryStatus.WARNING -> ContextCompat.getColor(this, android.R.color.holo_orange_light)
                ModelEngineViewModel.MemoryStatus.CRITICAL -> ContextCompat.getColor(this, android.R.color.holo_red_light)
                else -> ContextCompat.getColor(this, R.color.design_default_color_secondary)
            }
            binding.appBarMain.btnInfo.backgroundTintList = ColorStateList.valueOf(color)
        }

        // Handle fragment results
        supportFragmentManager.setFragmentResultListener("app", this) { _, bundle ->
            val action = bundle.getString("action")
            when (action) {
                "pick" -> loadContentDialog.start()
                "load" -> {
                    val uri = bundle.getString("uri")
                    if (uri != null) {
                        val navBundle = Bundle(bundle)
                        navController.navigate(R.id.nav_home, navBundle)
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

    /**
     * Resolves a missing resource URI by prompting the user to select the file.
     * This method is called from background threads in the Engine (ContentUtils).
     */
    override fun resolveUri(uri: Uri): Uri? {
        Log.i(TAG, "Resolving missing resource: $uri")

        pendingResolution = CompletableFuture<Uri?>()
        
        runOnUiThread {
            Toast.makeText(this, "Please select missing file: ${ContentUtils.getFileName(this, uri)}", Toast.LENGTH_LONG).show()
            resolveContent.launch("*/*")
        }

        return try {
            // Block the background thread for up to 60 seconds waiting for user input
            pendingResolution?.get(60, TimeUnit.SECONDS)
        } catch (e: Exception) {
            Log.e(TAG, "Timeout or error resolving URI: $uri", e)
            null
        } finally {
            pendingResolution = null
        }
    }

    private fun updateScreenInsets(insets: Insets) {
        modelEngineViewModel.glScreen.value?.let { screen ->
            screen.setInsets(insets.left, insets.top, insets.right, insets.bottom)

            // Update toolbar height (including status bar if visible)
            val toolbarHeight = if (immersiveMode) 0 else binding.appBarMain.toolbar.height
            screen.setToolbarHeight(toolbarHeight)

            Log.d(TAG, "Shared Screen insets updated: $insets, toolbar=$toolbarHeight")

            // Notify the active engine that screen properties changed
            modelEngineViewModel.activeEngine.value?.let { engine ->
                engine.beanFactory.find(EventManager::class.java)
                    ?.propagate(ModelEvent(this, ModelEvent.Code.SCREEN_CHANGED))
            }
        }
    }

    private fun refreshOverlayButtons() {
        runOnUiThread {
            val engine = modelEngineViewModel.activeEngine.value
            val model = engine?.model
            val scene = model?.activeScene

            binding.appBarMain.btnScene.isEnabled = (model?.scenes?.size ?: 0) > 1
            binding.appBarMain.btnCamera.isEnabled = (scene?.cameras?.size ?: 0) > 1
            binding.appBarMain.btnAnimation.isEnabled = (scene?.animations?.size ?: 0) > 0

            binding.appBarMain.btnScene.alpha = if (binding.appBarMain.btnScene.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnCamera.alpha = if (binding.appBarMain.btnCamera.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnAnimation.alpha = if (binding.appBarMain.btnAnimation.isEnabled) 1.0f else 0.5f
        }
    }

    override fun onEvent(event: EventObject?): Boolean {
        if (event is ModelEvent && (event.code == ModelEvent.Code.LOADED || event.code == ModelEvent.Code.LOAD_ERROR)) {
            refreshOverlayButtons()
            return false
        }
        return false
    }

    fun pick(mimeType: String) {
        getContent.launch(mimeType)
    }

    /**
     * Update the recent models' menu. That is, it adds a menu item for each URI in the history with an Icon.
     */
    private fun updateRecentModels(navigationView: NavigationView, history: List<String>) {
        val menu = navigationView.menu
        val recentWrapper = menu.findItem(R.id.nav_recent_wrapper)
        val subMenu = recentWrapper?.subMenu

        if (subMenu != null) {
            subMenu.clear()
            history.forEachIndexed { index, uriString ->
                val title = shortenUri(uriString)
                subMenu.add(R.id.group_recent, Menu.NONE, index, title).apply {
                    icon = ContextCompat.getDrawable(this@MainActivity, getIconResForModel(uriString))
                    MenuItemCompat.setTooltipText(this, uriString)
                }
            }
        } else {
            // Fallback to old behavior if XML ID is missing or not a submenu
            menu.removeGroup(R.id.group_recent)
            history.forEachIndexed { index, uriString ->
                val title = shortenUri(uriString)
                menu.add(R.id.group_recent, Menu.NONE, index, title).apply {
                    icon = ContextCompat.getDrawable(this@MainActivity, getIconResForModel(uriString))
                    MenuItemCompat.setTooltipText(this, uriString)
                }
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
        val isImmersiveMode = prefs.getBoolean(MainActivity::class.java.name + ".immersive", false)
        setImmersiveMode(isImmersiveMode)
    }

    private fun setImmersiveMode(immersiveMode: Boolean) {
        this.immersiveMode = immersiveMode

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (immersiveMode) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            supportActionBar?.hide()
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            supportActionBar?.show()
        }
        
        // Update screen insets immediately
        ViewCompat.requestApplyInsets(binding.root)
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
