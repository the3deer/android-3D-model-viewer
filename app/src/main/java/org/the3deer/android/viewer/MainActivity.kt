package org.the3deer.android.viewer

import android.content.Intent
import android.content.res.ColorStateList
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
import org.the3deer.android.engine.event.CameraEvent.Code
import org.the3deer.android.engine.event.EngineEvent
import org.the3deer.android.engine.event.FPSEvent
import org.the3deer.android.engine.event.SceneEvent
import org.the3deer.android.engine.model.ModelEvent
import org.the3deer.android.engine.model.Object3D
import org.the3deer.util.event.EventListener
import org.the3deer.util.event.EventManager
import java.net.URI
import java.util.Locale
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

    // Future to handle synchronous-like URI resolution from background threads
    private var pendingResolution: CompletableFuture<URI?>? = null

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            Log.d(TAG, "Picked URI: $uri")
            uri?.let {
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
            binding.appBarMain.joystickLeft.setPadding(insets.left, 0, 0, insets.bottom)
            binding.appBarMain.joystickRight.setPadding(0, 0, insets.right, insets.bottom)

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

                        // Set URI and navigate
                        val arguments = Bundle()
                        arguments.putString("uri", uriString)
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
                binding.appBarMain.containerNormalActions.visibility = if (destination.id == R.id.nav_home) View.VISIBLE else View.GONE
            }

            sharedViewModel.history.observe(this) { history ->
                updateRecentModels(navigationView, history)
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

    private fun updateScreenInsets(insets: Insets) {
        modelEngineViewModel.glScreen.value?.let { screen ->
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

            val engine = modelEngineViewModel.activeEngine.value ?: return@runOnUiThread
            val model = engine.model
            val scene = model.activeScene

            binding.appBarMain.btnScene.isEnabled = (model.scenes?.size ?: 0) > 1
            binding.appBarMain.btnCamera.isEnabled = (scene?.cameras?.size ?: 0) > 1
            binding.appBarMain.btnAnimation.isEnabled = (scene?.animations?.size ?: 0) > 0

            binding.appBarMain.btnScene.alpha =
                if (binding.appBarMain.btnScene.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnCamera.alpha =
                if (binding.appBarMain.btnCamera.isEnabled) 1.0f else 0.5f
            binding.appBarMain.btnAnimation.alpha =
                if (binding.appBarMain.btnAnimation.isEnabled) 1.0f else 0.5f

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

            // Show gravity button only if FirstPersonCameraHandler is active
            val cameraManager = engine.beanFactory.find(CameraManager::class.java)
            val isFirstPerson = cameraManager?.activeController is FirstPersonCameraHandler
            
            if (isFirstPerson) {
                binding.appBarMain.containerNormalActions.visibility = View.GONE
                binding.appBarMain.containerGameActions.visibility = View.VISIBLE
                binding.appBarMain.joystickLeft.visibility = View.VISIBLE
                binding.appBarMain.joystickRight.visibility = View.VISIBLE
                
                binding.appBarMain.btnGameMode.setImageResource(android.R.drawable.ic_menu_edit)
            } else {
                binding.appBarMain.containerNormalActions.visibility = View.VISIBLE
                binding.appBarMain.containerGameActions.visibility = View.GONE
                binding.appBarMain.joystickLeft.visibility = View.GONE
                binding.appBarMain.joystickRight.visibility = View.GONE

                binding.appBarMain.btnGameMode.setImageResource(android.R.drawable.ic_menu_compass)
            }
            
            if (modelStatus == Model.Status.LOADING || engineStatus == ModelEngine.Status.LOADING) {
                binding.loadingLayout.visibility = View.VISIBLE
                binding.loadingText.text = model.message
            } else {
                binding.loadingLayout.visibility = View.GONE
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
            history.forEachIndexed { index, uriString ->
                val title = shortenUri(uriString)
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

    private fun shortenUri(uriString: String): String {
        return uriString.split("/").last().split("?").first()
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
