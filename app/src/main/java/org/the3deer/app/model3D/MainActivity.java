package org.the3deer.app.model3D;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.andresoviedo.dddmodel2.R;
import org.the3deer.android_3d_model_engine.ModelEngine;
import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.ModelViewModel;
import org.the3deer.app.model3D.view.MainDialogFragment;

/**
 * This is the main android activity. From here we launch the whole stuff.
 * 
 * Basically, this activity may serve to show a Splash screen and copy the assets (obj models) from the jar to external
 * directory.
 * 
 * @author andresoviedo
 *
 */
public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {

	private final static String TAG = MainActivity.class.getSimpleName();

	// variables
	private Fragment fragmentMenu;
	private ModelViewModel viewModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set main layout controls.
		// Basically, this is a screen with the app name just in the middle of the scree
		setContentView(R.layout.activity_main);

		// configure model
		viewModel = new ViewModelProvider(this).get(ModelViewModel.class);
		final ModelEngine modelEngine = ModelEngine.newInstance(this, savedInstanceState, null);
		//viewModel.setModelEngine(modelEngine);

		// listen for engine events
		getSupportFragmentManager().setFragmentResultListener("app", this, (requestKey, bundle) -> {
            // We use a String here, but any type that can be put in a Bundle is supported.
            String action = bundle.getString("action");
            // Do something with the result.
            if ("load".equals(action)) {
                showDialog();
            } else if ("back".equals(action)){
				showDialog();
			}
        });

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				showDialog();
			}
		});

		showDialog();
		//launchModelRendererActivity(Uri.parse("android://org.the3deer.dddmodel2/assets/models/teapot.obj"), "0");
	}



	/*@Override
	public void onBackPressed() {
		super.onBackPressed();
		showDialog();
	}*/

	private void showDialog(){
		//Log.i("MainActivity", "setUp: "+getSupportFragmentManager().findFragmentByTag("model"));
		if (getSupportFragmentManager().findFragmentByTag("dialog") == null) {
			MainDialogFragment newFragment = MainDialogFragment.newInstance(R.string.alert_dialog_main_title);
			newFragment.show(getSupportFragmentManager(), "dialog");
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		//showDialog();
	}

	private void launchModelRendererActivity(Uri uri, String type) {

		try {
			Log.i("Menu", "Launching renderer for '" + uri + "'");
			//URI.create(uri.toString());
			ModelFragment modelFragment = ModelFragment.newInstance(uri.toString(), type, false);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.main_container, modelFragment, "model")
					.setReorderingAllowed(true)
					.addToBackStack(null)
					.commit();

		} catch (Exception e) {
			Log.e("Menu", "Launching renderer for '" + uri + "' failed: "+e.getMessage(),e);
			Toast.makeText(getApplication(), "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
		}
	}

}
