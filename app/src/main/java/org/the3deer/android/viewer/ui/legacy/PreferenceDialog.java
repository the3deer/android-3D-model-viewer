package org.the3deer.android.viewer.ui.legacy;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import org.the3deer.dddmodel2.R;


public class PreferenceDialog extends DialogFragment {

    private final static String TAG = PreferenceDialog.class.getSimpleName();

    private PreferenceFragment actualPreferenceFragment; // Your existing PreferenceFragment

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create an instance of your existing PreferenceFragment
        actualPreferenceFragment = new PreferenceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_host_for_preferences, container, false);
        // Ensure R.layout.dialog_host_for_preferences exists and has the FrameLayout
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Add your existing PreferenceFragment as a child fragment to this DialogFragment.
        // This is crucial: the PreferenceFragment's UI will be displayed within the
        // content area of this DialogFragment.
        if (getChildFragmentManager().findFragmentByTag("hosted_preference_fragment") == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            // android.R.id.content is a standard ID for the main content area of a dialog.
            transaction.replace(R.id.preference_host_container, actualPreferenceFragment, "hosted_preference_fragment");
            transaction.commit();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // Optional: If your PreferenceFragment defines its own title or Toolbar,
        // you might want to remove the default title bar from the Dialog.
        // dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                // Set the desired size for your modal dialog
                // Example: 90% of screen width, 80% of screen height
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.80);
                window.setLayout(width, height);

                // If you want it to try and wrap content for height (be careful with long preference lists):
                // window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }
}