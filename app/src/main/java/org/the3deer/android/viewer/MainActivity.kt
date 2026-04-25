package org.the3deer.android.viewer

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.core.net.toUri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.appcompat.widget.TooltipCompat
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
import org.the3deer.android.engine.ModelEngineViewModel
import org.the3deer.android.util.ContentUtils
import org.the3deer.android.viewer.databinding.ActivityMainBinding
import org.the3deer.android.viewer.ui.dialogs.AnimationDialogFragment
import org.the3deer.android.viewer.ui.dialogs.CameraDialogFragment
import org.the3deer.android.viewer.ui.dialogs.ModelInfoDialogFragment
import org.the3deer.android.viewer.ui.dialogs.SceneDialogFragment
import org.the3deer.android.viewer.ui.load.LoadContentDialog
import org.the3deer.android.viewer.ui.settings.SettingsFragment
import org.the3deer.android.engine.Model
import org.the3deer.android.engine.ModelEngine
import org.the3deer.android.engine.camera.CameraManager
import org.the3deer.android.engine.camera.FirstPersonCameraHandler
import org.the3deer.android.engine.event.CameraEvent
import org.the3deer.android.engine.event.EngineEvent
import org.the3deer.android.engine.event.FPSEvent
import org.the3deer.android.engine.event.SceneEvent
import org.the3deer.android.engine.model.ModelEvent
import org.the3deer.android.engine.model.Object3D
import org.the3deer.util.event.EventListener
import org.the3deer.util.event.EventManager
import org.the3deer.android.engine.services.LoaderRegistry
import org.the3deer.android.engine.services.collada.ColladaLoaderTask
import org.the3deer.android.engine.services.fbx.FbxLoaderTask
import org.the3deer.android.engine.services.gltf.GltfLoaderTask
import org.the3deer.android.engine.services.stl.STLLoaderTask
import org.the3deer.android.engine.services.wavefront.WavefrontLoaderTask
import java.net.URI
import java.util.EventObject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), EventListener, ContentUtils.ContentResolver {

    init {
        // Register only the formats your game uses
        LoaderRegistry.register("obj") { uri, listener -> WavefrontLoaderTask(uri, listener) }
        LoaderRegistry.register("gltf") { uri, listener -> GltfLoaderTask(uri, listener) }
        LoaderRegistry.register("glb") { uri, listener -> GltfLoaderTask(uri, listener) }
        LoaderRegistry.register("fbx") { uri, listener -> FbxLoaderTask(uri, listener) }
        LoaderRegistry.register("stl") { uri, listener -> STLLoaderTask(uri, listener) }
        LoaderRegistry.register("dae") { uri, listener -> ColladaLoaderTask(uri, listener) }
    }

    private val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var immersiveMode = false

    private val sharedViewModel: SharedViewModel by viewModels()
    private val modelEngineViewModel: ModelEngineViewModel by viewModels()

    // Future to handle synchronous-like URI resolution from background threads
    private var pendingResolution: CompletableFuture<URI?>? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            Log.d(TAG, "Picked URI: $uri")
            uri?.let {
                // [SAFE APPLY] Request persistent URI permission for content:// URIs immediately
                if (it.toString().startsWith("content://")) {
                    try {
                        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        contentResolver.takePersistableUriPermission(it, flags)
                        Log.i(TAG, "Successfully took persistable URI permission for: $it")
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to take persistable URI permission: $it", e)
                    }
                }

                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        LoadContentDialog(this@MainActivity).load(URI.create(it.toString()))
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e(TAG, "Error loading uri: $it", e)
                            Toast.makeText(
                                this@MainActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

    private val resolveContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "Resolved URI: $uri")
            pendingResolution?.complete(URI.create(uri.toString()))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply global preferences (Theme, Language) before layout inflation
        SettingsFragment.applyGlobalPreferences(this)

        super.onCreate(savedInstanceState)

        // Initialize ContentUtils with context and resolver for file operations
        ContentUtils.setContext(this)
        ContentUtils.setContentResolver(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Make the window draw edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Listen for system bar insets to update the engine's safe area
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply insets as padding to UI elements to avoid overlapping system bars
            binding.appBarMain.appBarLayout.setPadding(0, insets.top, 0, 0)
            binding.appBarMain.containerActionsRoot.setPadding(0, 0, insets.right, insets.bottom)
            binding.appBarMain.containerJoysticks.setPadding(insets.left, 0, insets.right, insets.bottom)

            // Update the shared screen object in the engine view model
            updateScreenInsets(insets)

            windowInsets
        }

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.immersive.setOnClickListener {
            setImmersiveMode(!this.immersiveMode)
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(MainActivity::class.java.name + ".immersive", immersiveMode)
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

            // Setup email link in navigation header
            val headerView = navigationView.getHeaderView(0)
            headerView?.findViewById<TextView>(R.id.nav_header_subtitle)?.setOnClickListener {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = ("mailto:" + getString(R.string.nav_header_subtitle)).toUri()
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request - Android 3D Model Viewer")
                }
                startActivity(Intent.createChooser(intent, "Send email..."))
            }

            navigationView.setNavigationItemSelectedListener { item ->
                when (item.groupId) {
                    R.id.group_recent -> {
                        val uriString =
                            MenuItemCompat.getTooltipText(item)?.toString() ?: item.title.toString()
                                .lowercase()
                        val title = item.title.toString()

                        // Set URI and navigate
                        val arguments = Bundle()
                        arguments.putString("uri", uriString)
                        arguments.putString("name", title)

                        // Try to find the type in history
                        sharedViewModel.history.value?.find { it.startsWith("$uriString|$title|") }?.let {
                            val parts = it.split("|")
                            if (parts.size > 2) {
                                arguments.putString("type", parts[2])
                            }
                        }

                        navController.navigate(R.id.nav_home, arguments)

                        binding.drawerLayout.closeDrawers()
                        true
                    }

                    else -> {
                        when (item.itemId) {
                            R.id.nav_load, R.id.nav_settings -> {
                                navController.navigate(item.itemId)
                                binding.drawerLayout.closeDrawers()
                                true
                            }

                            else -> {
                                val handled =
                                    NavigationUI.onNavDestinationSelected(item, navController)
                                if (handled) {
                                    binding.drawerLayout.closeDrawers()
                                }
                                handled
                            }
                        }
                    }
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.nav_home) {
                    modelEngineViewModel.activeEngine.value?.let {
                        supportActionBar?.title = shortenUri(it.model.name)
                    }
                    navigationView.setCheckedItem(destination.id)
                }

                // Show/hide UI actions stack based on destination. These are only for the Home fragment (3D viewer)
                binding.appBarMain.containerActionsRoot.visibility = if (destination.id == R.id.nav_home) View.VISIBLE else View.GONE
                
                // Refresh all overlay elements (including joysticks) based on new destination
                refreshOverlayButtons()
            }

            sharedViewModel.history.observe(this) { history ->
                updateRecentModels(navigationView, history)
            }
        }

        // Action stack buttons setup
        binding.appBarMain.btnScene.setOnClickListener {
            if (binding.appBarMain.btnScene.alpha < 1.0f) {
                Toast.makeText(this, R.string.tooltip_no_scenes, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SceneDialogFragment().show(supportFragmentManager, "scene_dialog")
        }
        binding.appBarMain.btnCamera.setOnClickListener {
            if (binding.appBarMain.btnCamera.alpha < 1.0f) {
                Toast.makeText(this, R.string.tooltip_no_cameras, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            CameraDialogFragment().show(supportFragmentManager, "camera_dialog")
        }
        binding.appBarMain.btnAnimation.setOnClickListener {
            if (binding.appBarMain.btnAnimation.alpha < 1.0f) {
                Toast.makeText(this, R.string.tooltip_no_animations, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AnimationDialogFragment().show(supportFragmentManager, "animation_dialog")
        }

        val gameModeToggle = {
            val engine = modelEngineViewModel.activeEngine.value
            if (engine != null) {
                val cameraManager = engine.beanFactory.find(CameraManager::class.java)
                cameraManager?.toggleController()
                // refreshOverlayButtons() is called automatically via CameraEvent/ModelEvent
            }
        }
        binding.appBarMain.btnGameMode.setOnClickListener { gameModeToggle() }

        binding.appBarMain.btnGravity.setOnClickListener {
            val engine = modelEngineViewModel.activeEngine.value ?: return@setOnClickListener
            val cameraManager = engine.beanFactory.find(CameraManager::class.java)
            val firstPersonHandler = cameraManager.activeController
            
            if (firstPersonHandler != null && firstPersonHandler is FirstPersonCameraHandler) {
                val newGravity = !firstPersonHandler.isGravity
                firstPersonHandler.isGravity = newGravity
                binding.appBarMain.btnGravity.setImageResource(
                    if (newGravity) android.R.drawable.ic_lock_idle_lock 
                    else android.R.drawable.ic_lock_lock
                )
                Toast.makeText(this, if (newGravity) "Gravity ON" else "Gravity OFF (Flying)", Toast.LENGTH_SHORT).show()
            }
        }

        binding.appBarMain.joystickLeft.setJoystickListener { dx, dy ->
            val camera = modelEngineViewModel.getActiveEngine()?.model?.activeScene?.activeCamera
            camera?.joystick(dx, dy)
        }
        binding.appBarMain.joystickRight.setJoystickListener { dx, dy ->
            val camera = modelEngineViewModel.getActiveEngine()?.model?.activeScene?.activeCamera
            camera?.joystickLook(dx, dy)
        }

        // Monitor active engine to refresh UI buttons
        modelEngineViewModel.activeEngine.observe(this) { engine ->

            if (engine == null) return@observe;

            Log.i(TAG, "Active engine changed. id: ${engine.id}")

            // [SAFE APPLY] If loading failed with an error, show a prompt to remove from history
            if (engine.status == ModelEngine.Status.ERROR || engine.model.status == Model.Status.ERROR) {
                val message = if (engine.status == ModelEngine.Status.ERROR) engine.message else engine.model.message
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                    .setTitle("Loading Error")
                    .setMessage("Failed to load model. Would you like to remove it from history?\n\nError: $message")
                    .setPositiveButton("Remove") { _, _ ->
                        sharedViewModel.removeFromHistory(engine.id)
                        Toast.makeText(this, "Removed from history", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Keep", null)
                    .show()
            }

            // Register this activity as a listener for the active engine
            engine.addOrReplace(this@MainActivity.javaClass.name, this@MainActivity)

            supportActionBar?.title = shortenUri(engine.model.name)

            refreshOverlayButtons()
            ViewCompat.requestApplyInsets(binding.root)
        }

        // Handle fragment results
        supportFragmentManager.setFragmentResultListener("app", this) { _, bundle ->
            val action = bundle.getString("action")
            when (action) {
                "restart" -> {
                    recreate()
                }
                "pick" -> {
                    LoadContentDialog(this@MainActivity).start()
                }
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
        checkNotification()

        // Handle back press to prompt user before exiting from Home screen
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 1. If drawer is open, close it
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return
                }

                val navController = findNavController(R.id.nav_host_fragment_content_main)

                // 2. If we are NOT on the Home screen, let the system handle it (go back to Home)
                if (navController.currentDestination?.id != R.id.nav_home) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                    return
                }

                // 3. We are on Home screen, show the prompt with the 4 drawer options + Exit
                val items = arrayOf(
                    getString(R.string.menu_home),
                    getString(R.string.menu_load),
                    getString(R.string.menu_settings),
                    getString(R.string.menu_about),
                    "Exit"
                )

                com.google.android.material.dialog.MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle(R.string.dialog_load_title) // "Menu"
                    .setItems(items) { _, which ->
                        when (which) {
                            0 -> {} // Home - Do nothing, stay here
                            1 -> navController.navigate(R.id.nav_load)
                            2 -> navController.navigate(R.id.nav_settings)
                            3 -> navController.navigate(R.id.nav_about)
                            4 -> finish() // Exit the app
                        }
                    }
                    .show()
            }
        })
    }

    /**
     * Sets the loading state of the activity.
     * This shows/hides a full-screen overlay with a progress bar and message.
     */
    fun setLoading(loading: Boolean, message: CharSequence? = null) {
        runOnUiThread {
            binding.loadingLayout.visibility = if (loading) View.VISIBLE else View.GONE
            if (loading && message != null) {
                binding.loadingText.text = message
            }
        }
    }

    /**
     * Resolves a missing resource URI by prompting the user to select the file.
     * This method is called from background threads in the Engine (ContentUtils).
     */
    override fun resolveUri(uri: URI): URI? {
        Log.i(TAG, "Resolving missing resource: $uri")

        pendingResolution = CompletableFuture<URI?>()

        runOnUiThread {
            Toast.makeText(
                this,
                "Please select missing file: ${ContentUtils.getFileName(this, uri)}",
                Toast.LENGTH_LONG
            ).show()
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

    private fun checkNotification() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val notified = prefs.getBoolean("notification.v5.1", true)
        if (!notified) {
            // TODO: Update this with your NEW package name
            val newPackageName = "org.the3deer.android.viewer" 
            val newAppName = getString(R.string.app_name)

            com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_new_version_title)
                .setMessage(getString(R.string.dialog_new_version_message, newAppName))
                .setPositiveButton(R.string.dialog_new_version_button) { _, _ ->
                    // Optional: Mark as notified so it doesn't show again
                    prefs.edit().putBoolean("notification.v5.1", true).apply()
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$newPackageName".toUri()))
                    } catch (e: Exception) {
                        startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$newPackageName".toUri()))
                    }
                }
                .setNegativeButton(R.string.dialog_new_version_later, null)
                .show()
        }
    }

    private fun updateScreenInsets(insets: Insets) {
        modelEngineViewModel.glScreen?.let { screen ->
            screen.setInsets(insets.left, insets.top, insets.right, insets.bottom)

            // Update toolbar height (including status bar if visible)
            val toolbarHeight = if (immersiveMode) 0 else (binding.appBarMain.toolbar.height + insets.top)
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

            try {
                val engine = modelEngineViewModel.activeEngine.value ?: return@runOnUiThread
                val model = engine.model
                val scene = model.activeScene

                val scenesEnabled = (model.scenes?.size ?: 0) > 1
                val camerasEnabled = (scene?.cameras?.size ?: 0) > 1
                val animationsEnabled = (scene?.animations?.size ?: 0) > 0

                binding.appBarMain.btnScene.alpha = if (scenesEnabled) 1.0f else 0.25f
                binding.appBarMain.btnCamera.alpha = if (camerasEnabled) 1.0f else 0.25f
                binding.appBarMain.btnAnimation.alpha = if (animationsEnabled) 1.0f else 0.25f

                // Set tooltips for long-press support
                TooltipCompat.setTooltipText(binding.appBarMain.btnScene, if (scenesEnabled) null else getString(R.string.tooltip_no_scenes))
                TooltipCompat.setTooltipText(binding.appBarMain.btnCamera, if (camerasEnabled) null else getString(R.string.tooltip_no_cameras))
                TooltipCompat.setTooltipText(binding.appBarMain.btnAnimation, if (animationsEnabled) null else getString(R.string.tooltip_no_animations))

                // Handle Traffic Light status for Info button
                val modelStatus = model.status ?: Model.Status.UNKNOWN
                val engineStatus = engine.status ?: ModelEngine.Status.UNKNOWN
                val color = when {
                    modelStatus == Model.Status.ERROR || engineStatus == ModelEngine.Status.ERROR -> ContextCompat.getColor(
                        this,
                        android.R.color.holo_red_light
                    )

                    modelStatus == Model.Status.WARNING || engineStatus == ModelEngine.Status.WARNING -> ContextCompat.getColor(
                        this,
                        android.R.color.holo_orange_light
                    )

                    modelStatus == Model.Status.OK && engineStatus == ModelEngine.Status.OK -> ContextCompat.getColor(
                        this,
                        R.color.design_default_color_secondary
                    )

                    else -> ContextCompat.getColor(this, R.color.design_default_color_secondary)
                }
                val infoItem = binding.appBarMain.toolbar.menu.findItem(R.id.nav_info)
                infoItem?.icon?.setTint(color)

                // Show joysticks and toggle action containers based on First Person Mode AND current destination
                val isHome = findNavController(R.id.nav_host_fragment_content_main).currentDestination?.id == R.id.nav_home
                val cameraManager = engine.beanFactory.find(CameraManager::class.java)
                val isFirstPerson = cameraManager?.activeController is FirstPersonCameraHandler

                if (isFirstPerson) {
                    binding.appBarMain.containerNormalActions.visibility = View.GONE
                    binding.appBarMain.containerGameActions.visibility = View.VISIBLE
                    binding.appBarMain.containerJoysticks.visibility = if (isHome) View.VISIBLE else View.GONE

                    binding.appBarMain.btnGameMode.setImageResource(android.R.drawable.ic_menu_edit)
                } else {
                    binding.appBarMain.containerNormalActions.visibility = View.VISIBLE
                    binding.appBarMain.containerGameActions.visibility = View.GONE
                    binding.appBarMain.containerJoysticks.visibility = View.GONE

                    binding.appBarMain.btnGameMode.setImageResource(android.R.drawable.ic_menu_compass)
                }

                if (modelStatus == Model.Status.LOADING || engineStatus == ModelEngine.Status.LOADING) {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.loadingText.text = model.message
                } else {
                    binding.loadingLayout.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("MainActivity","Error refreshing overlay buttons", e)
            }
        }
    }

    override fun onEvent(event: EventObject?): Boolean {
        if (event is FPSEvent) {
            runOnUiThread {
                val fpsItem = binding.appBarMain.toolbar.menu.findItem(R.id.nav_fps)
                val fpsTextView = fpsItem?.actionView?.findViewById<TextView>(R.id.tv_fps_menu)
                if (fpsTextView != null) {
                    fpsTextView.text = getString(R.string.fps_label, event.fps)
                    // Change color based on FPS
                    val color = when {
                        event.fps >= 50 -> ContextCompat.getColor(this, android.R.color.holo_green_light)
                        event.fps >= 30 -> ContextCompat.getColor(this, android.R.color.holo_orange_light)
                        else -> ContextCompat.getColor(this, android.R.color.holo_red_light)
                    }
                    fpsTextView.setTextColor(color)
                }
            }
        } else if (event is EngineEvent){
            refreshOverlayButtons(
            )
        }
        else if (event is ModelEvent) {
            refreshOverlayButtons()
        }
        else if (event is CameraEvent) {
            refreshOverlayButtons()

        } else if (event is SceneEvent) {
            if (event.code == SceneEvent.Code.OBJECT_SELECTED) {
                val selected = event.getData("object", Object3D::class.java)
                if (selected != null) {
                    val cameraNode = selected.parentNode?.camera
                    val activeScene = modelEngineViewModel.getActiveEngine().model.activeScene
                    if (activeScene != null && cameraNode != null) {
                        activeScene.activeCamera = cameraNode
                    }
                }
            }
             refreshOverlayButtons()
        }

        return false
    }

    fun pick(mimeType: String) {

        val connMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connMgr.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
            Toast.makeText(this, "Warning: Background mode is enabled. It may slow down the loading process", Toast.LENGTH_LONG).show()
        }

        getContent.launch(arrayOf(mimeType))
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
            history.forEachIndexed { index, historyItem ->
                val parts = historyItem.split("|")
                val uriString = parts[0]
                val title = if (parts.size > 1) parts[1] else shortenUri(uriString)
                subMenu.add(R.id.group_recent, Menu.NONE, index, title).apply {
                    icon = ContextCompat.getDrawable(
                        this@MainActivity,
                        getIconResForModel(uriString)
                    )
                    MenuItemCompat.setTooltipText(this, uriString)
                }
            }
        } else {
            // Fallback to old behavior if XML ID is missing or not a submenu
            menu.removeGroup(R.id.group_recent)
            history.forEachIndexed { index, historyItem ->
                val parts = historyItem.split("|")
                val uriString = parts[0]
                val title = if (parts.size > 1) parts[1] else shortenUri(uriString)
                menu.add(R.id.group_recent, Menu.NONE, index, title).apply {
                    icon = ContextCompat.getDrawable(
                        this@MainActivity,
                        getIconResForModel(uriString)
                    )
                    MenuItemCompat.setTooltipText(this, uriString)
                }
            }
        }
    }

    private fun shortenUri(uriString: String?): String {
        if (uriString.isNullOrBlank()) return "?"

        // If it doesn't look like a URI (no scheme), don't try to parse it.
        // This avoids IllegalArgumentException on strings with spaces or special chars.
        if (!uriString.contains(":/")) {
            return uriString.substringAfterLast('/')
        }

        return try {
            ContentUtils.getFileName(applicationContext, URI.create(uriString)) ?: uriString
        } catch (e: Exception) {
            Log.w(TAG, "Not a valid URI: $uriString")
            uriString.substringAfterLast('/')
        }
    }

    private fun getIconResForModel(modelName: String): Int {
        return when (modelName) {
            "cube" -> android.R.drawable.ic_menu_help
            "square" -> android.R.drawable.ic_menu_crop
            else -> android.R.drawable.ic_menu_compass
        }
    }

    private fun applyInitialImmersiveMode() {
        val immersive = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(MainActivity::class.java.name + ".immersive", false)
        setImmersiveMode(immersive)
    }

    private fun setImmersiveMode(immersiveMode: Boolean) {
        this.immersiveMode = immersiveMode

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (immersiveMode) {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            windowInsetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            supportActionBar?.hide()
            binding.appBarMain.appBarLayout.visibility = View.GONE
        } else {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            supportActionBar?.show()
            binding.appBarMain.appBarLayout.visibility = View.VISIBLE
        }

        // Update screen insets immediately
        ViewCompat.requestApplyInsets(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.overflow, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_settings) {
            findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.nav_settings)
        } else if (item.itemId == R.id.nav_info) {
            ModelInfoDialogFragment().show(supportFragmentManager, "model_info_dialog")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
