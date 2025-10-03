package org.the3deer.modelviewer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.the3deer.android_3d_model_engine.ModelViewModel;
import org.the3deer.modelviewer.databinding.ActivityMainBinding;
import org.the3deer.modelviewer.ui.LoadContentDialog;
import org.the3deer.modelviewer.ui.MainDialogFragment;
import org.the3deer.modelviewer.ui.help.HelpDialogFragment;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is the main android activity. From here we launch the whole stuff.
 * <p>
 * Basically, this activity may serve to show a Splash screen and copy the assets (obj models) from the jar to external
 * directory.
 *
 * @author andresoviedo
 */
public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int MAX_RECENT_ITEMS = 5;

    private ModelViewModel modelViewModel;
    private Toolbar toolbar;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // variables
    private Fragment fragmentMenu;
    private ModelViewModel viewModel;
    private ActivityResultLauncher<String> mGetContent;

    private LoadContentDialog loadContentDialog = new LoadContentDialog(this);

    // nav drawer
    private NavController navController;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Map<String, MenuItem> recentMenuItemsMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ContentUtils.setThreadActivity(this);

        //WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Set main layout controls.
        // Basically, this is a screen with the app name just in the middle of the scree
        setContentView(binding.getRoot());

        // --- Options ---
        setSupportActionBar(binding.appBarMain.toolbar);

        // Get the ViewModel
        // Make sure ModelViewModel is correctly provided, e.g., via ViewModelProvider
        modelViewModel = new ViewModelProvider(this).get(ModelViewModel.class);

        // Example: To test, you could set an initial value in the ViewModel or call setRecentUri
        // modelViewModel.setRecentUri("content://com.example/some_file.obj");

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showDialog();
            }
        });

        ActivityResultContracts.GetContent contract = loadContentDialog.getActivityContract();

        mGetContent = registerForActivityResult(contract,
                uri -> {
                    try {
                        // Handle the returned Uri
                        Log.i(TAG, "Uri: " + uri);
                        loadContentDialog.load(uri);
                    } catch (Exception e) {
                        Log.e(TAG, "Exception loading uri: " + uri, e);
                        Toast.makeText(getApplication(), "Problem loading " + uri +
                                "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


        // --- Menu ---
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_demo3)
                .setOpenableLayout(drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d("NAV_DEBUG", "Destination Changed To: " + destination.getDisplayName() +
                    " (ID: " + destination.getId() + ")");
            if (arguments != null) {
                Log.d("NAV_DEBUG", "Arguments: " + arguments.toString());
            }
            // You can put a breakpoint here too
        });


        // listen for view events
        getSupportFragmentManager().setFragmentResultListener("app", this, (requestKey, bundle) -> {

            final String action = bundle.getString("action");
            if ("navigate".equals(action)) {
                navController.navigate(bundle.getInt("view"), bundle);
            } else if ("load".equals(action)) {
                bundle.putString("type", null);
                navController.navigate(R.id.nav_home, bundle);

            } else if ("help".equals(action)) {
                showHelpDialog();
            } else if ("pick".equals(action)) {
                loadContentDialog.start();
            } else if ("back".equals(action)) {
                showDialog();
            }
        });

        // observe recentUri to add recent menu items and update the toolbar title
        modelViewModel.getRecentId().observe(this, new Observer<String>() { // Or observe the arguments directly if possible
            @Override
            public void onChanged(String uri) {

                // add menu - if not existing
                Log.v(TAG, "Updating menu... " + uri);
                addRecentFileToMenu(R.id.nav_home, uri, shortenUri(uri));

                // update title
                Log.v(TAG, "Updating title... " + uri);
                if (getSupportActionBar() != null && uri != null) {
                    String displayTitle = shortenUri(uri); // Your method to get a nice name from the URI
                    getSupportActionBar().setTitle(displayTitle);
                    Log.v(TAG, "updated title... " + displayTitle);
                }
            }
        });

        // Inside MainActivity
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Bundle args = null;
                int itemId = item.getItemId();
                boolean closeDrawer = true; // Assume we'll close the drawer

                // handle dialogs, otherwise NavUI will not handle it correctly
                // (it will navigate to home after some timeout)
                if (itemId == R.id.nav_scenes || itemId == R.id.nav_settings
                        || itemId == R.id.nav_help || itemId == R.id.nav_about) {
                    Log.d("NAV_ITEM_SELECTED", "Manually navigating to DIALOG: " + item.getTitle());
                    // args = ... if needed
                    // For dialogs, usually no special NavOptions are needed unless you want specific animations.
                    // We definitely DON'T want popUpTo here.
                    navController.navigate(itemId, args);
                } else {
                    // For any other items not explicitly handled, try NavUI, then fail
                    Log.d("NAV_ITEM_SELECTED", "Defaulting to NavigationUI for: " + item.getTitle());
                    if (!NavigationUI.onNavDestinationSelected(item, navController)) {
                        closeDrawer = false; // Don't close drawer if nothing handled it
                        return false; // Item not handled
                    }
                }

                if (closeDrawer) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                return true; // Item handled
            }
        });



        //FragmentManager.enableDebugLogging(true);
    }


    /**
     * Add a recent file to the menu.
     *
     * @param uri          the model uri
     * @param displayTitle the title to display
     */
    private void addRecentFileToMenu(int viewId, String uri, String displayTitle) {
        if (uri == null || uri.isEmpty()) {
            return;
        }

        final Menu menu = navigationView.getMenu();
        final SubMenu subMenu = menu.findItem(R.id.nav_recent_menu).getSubMenu();
        if (subMenu == null) {
            Log.e(TAG, "SubMenu for recent items not found!");
            return;
        }

        // --- 1. Handle if item already exists (move to top) ---
        if (recentMenuItemsMap.containsKey(uri)) {
            MenuItem existingItem = recentMenuItemsMap.remove(uri);
            if (existingItem != null) {
                subMenu.removeItem(existingItem.getItemId());
            }
            // The item will be re-added below, effectively moving it to the "end" of the LinkedHashMap
            // which represents the "most recent" if you iterate from the beginning for "oldest".
        }

        // --- 2. Enforce the maximum limit BEFORE adding the new one ---
        if (recentMenuItemsMap.size() >= MAX_RECENT_ITEMS) {
            // Get the oldest URI (first key in the LinkedHashMap's keySet iterator)
            Iterator<String> iterator = recentMenuItemsMap.keySet().iterator();
            if (iterator.hasNext()) {
                String oldestUri = iterator.next();
                MenuItem oldestMenuItem = recentMenuItemsMap.remove(oldestUri); // Remove from map
                if (oldestMenuItem != null) {
                    subMenu.removeItem(oldestMenuItem.getItemId()); // Remove from SubMenu
                    Log.d(TAG, "Removed oldest item: " + oldestUri);
                }
            }
        }

        // --- 3. Add the new item to the SubMenu and your tracking map ---
        // Add with order 0 to try and place it at the top of this specific subMenu
        // Or manage order more explicitly if needed (e.g., dynamicSubMenuItemOrder++)
        final MenuItem newItem = subMenu.add(Menu.NONE, View.generateViewId(), 0, displayTitle);
        newItem.setIcon(android.R.drawable.ic_menu_recent_history);
        // newItem.setCheckable(true); // If you want them to be checkable

        newItem.setOnMenuItemClickListener(menuItem -> {
            // Your click listener logic
            final Bundle bundle = new Bundle();
            bundle.putString("uri", uri); // Use the original full URI for navigation
            navController.navigate(viewId, bundle);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            // If checkable:
            // menuItem.setChecked(true);
            // updateCheckedStatesInSubMenu(subMenu, menuItem.getItemId());
            return true;
        });

        recentMenuItemsMap.put(uri, newItem); // Add to the end (most recent)
        Log.d(TAG, "Added recent item: " + uri + ", Total: " + recentMenuItemsMap.size());

        // Optional: Persist recentUrisOrder if you are using SharedPreferences
        // saveRecentItemsToPreferences();
    }

// Example usage within your LiveData observer:
// modelViewModel.getRecentUri().observe(this, uri -> {
//     if (uri != null && !uri.isEmpty()) {
//         addRecentFileToMenu(uri, shortenUri(uri)); // shortenUri is your display formatter
//     }
// });


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

/*@Override
	public void onBackPressed() {
		super.onBackPressed();
		showDialog();
	}*/


    private void showDialog() {
        //Log.i("MainActivity", "setUp: "+getSupportFragmentManager().findFragmentByTag("model"));
        if (getSupportFragmentManager().findFragmentByTag("dialog") == null) {
            MainDialogFragment newFragment = MainDialogFragment.newInstance(R.string.alert_dialog_main_title, getResources().getStringArray(R.array.dialog_menu_items));
            newFragment.show(getSupportFragmentManager(), "dialog");
        }
    }

    public void showHelpDialog() {
        HelpDialogFragment fragment = HelpDialogFragment.newInstance(R.string.alert_dialog_help_title, getResources().getStringArray(R.array.dialog_help_items));
        fragment.show(getSupportFragmentManager(), "help");
    }

    public void pick(String mimeType) {
        this.mGetContent.launch(mimeType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //MenuItem item = menu.getItem(R.id.action_immersive);


        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Let the NavController try to handle appBarConfiguration actions
        // This is often implicitly handled by onSupportNavigateUp for the home/up button
        // but explicitly calling it for other menu items is fine.
        NavController navController = Navigation.findNavController(findViewById(R.id.nav_host_fragment_content_main));
        if (NavigationUI.onNavDestinationSelected(item, navController)) {
            return true;
        }

        return super.onOptionsItemSelected(item); // Ensure you call super for unhandled items
    }*/

    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /*private void launchModelRendererActivity(String uri, String type, boolean demo) {

        try {
            Log.i(TAG, "Launching renderer for '" + uri + "'");
            //URI.create(uri.toString());
            ModelFragment modelFragment = ModelFragment.newInstance(uri, type, demo);
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment_content_main, modelFragment, "model")
                    .commit();

        } catch (Exception e) {
            Log.e(TAG, "Launching renderer for '" + uri + "' failed: " + e.getMessage(), e);
            Toast.makeText(getApplication(), "Error: " + uri, Toast.LENGTH_LONG).show();
        }


		*//*override fun onCreateOptionsMenu(menu: Menu): Boolean {
			// Inflate the menu; this adds items to the action bar if it is present.
			menuInflater.inflate(R.menu.main, menu)
			return true
		}

		override fun onSupportNavigateUp(): Boolean {
			val navController = findNavController(R.id.nav_host_fragment_content_main)
			return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
		}*//*
    }*/

    // Helper to shorten URL for display
    private static String shortenUri(String uriString) {
        if (uriString == null) return "Unknown Model";
        try {
            android.net.Uri uri = android.net.Uri.parse(uriString);
            String path = uri.getLastPathSegment(); // Often gives a filename or ID
            if (path != null && path.length() > 20) {
                return "..." + path.substring(path.length() - 17);
            } else if (path != null) {
                return path;
            }
        } catch (Exception e) {
            // Fallback for malformed URIs or if parsing fails
        }
        if (uriString.length() > 25) {
            return "..." + uriString.substring(uriString.length() - 22);
        }
        return uriString;
    }

}
