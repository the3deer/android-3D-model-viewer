package org.the3deer.android.viewer.ui.legacy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.dddmodel2.R;

import java.util.List;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private final static String TAG = PreferenceFragment.class.getSimpleName();
    private List<PreferenceAdapter> adapters;

    public PreferenceFragment() {
        //setEnterTransition(new android.transition.Slide(Gravity.RIGHT));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


        // get view model
        final SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // check
        if (viewModel.getActiveEngine() == null) return;

        // get adapters
        adapters = viewModel.getActiveEngine().getValue().getBeanFactory().findAll(PreferenceAdapter.class, null);

        // check
        if (adapters == null || adapters.isEmpty()){
            Log.e(TAG, "onCreate: adapters is null or empty");
            return;
        }

        Log.i(TAG, "Creating the preferences screen...");

        // create screen
        final Context context = requireContext();

        // main screen
        final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        // check
        if (adapters == null || adapters.isEmpty()) return;

        // inflate
        for (PreferenceAdapter a : adapters) {
            try {
                a.onCreatePreferences(savedInstanceState, rootKey, context, screen);
            } catch (Exception e) {
                Log.e(TAG, "Issue onCreatePreferences: " + e.getMessage(), e);
            }
        }

        // update
        setPreferenceScreen(screen);

        Log.i(TAG, "Preferences screen set.");
    }

    public void onSaveInstanceState(Bundle outState) {

        Log.v(TAG, "Saving state... ");

        // assert
        if (outState == null || this.adapters == null) return;

        // inform listeners
        for (PreferenceAdapter l : this.adapters) {
            if (l == this) continue;
            l.onSaveInstanceState(outState);
        }
    }


    public void onViewStateRestored(Bundle state) {

        super.onViewStateRestored(state);

        Log.v(TAG, "Restoring state... " + state);

        // assert
        if (state == null || this.adapters == null) return;

        // inform listeners
        for (PreferenceAdapter l : this.adapters) {
            if (l == this) continue;
            l.onRestoreInstanceState(state);
        }
    }

    private @NonNull SwitchPreference getActivityPrefs() {
        SwitchPreference activityPrefs = new SwitchPreference(getContext());
        activityPrefs.setKey("immersive");
        activityPrefs.setTitle("Load");
        activityPrefs.setOnPreferenceChangeListener((preference, newValue) -> {
            // perform
            Log.i("PreferenceFragment", "Clicked! " + newValue);
            Bundle result = new Bundle();
            result.putString("action", "load");
            getParentFragmentManager().setFragmentResult("app", result);
            return true;
        });
        return activityPrefs;
    }

    /**
     * Helper method to create a simple "Not Available" dialog.
     */
    private Dialog createNotAvailableDialog(AlertDialog.Builder builder, String message) {
        // You might want a specific title for this kind of message
        // e.g., <string name="title_not_available">Information</string>
        builder.setTitle(getString(R.string.app_name)) // Use a string resource
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss(); // Dismiss the dialog on "Accept"
                    }
                });
        return builder.create();
    }
}