package org.the3deer.android.viewer.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.model.Camera;
import org.the3deer.android.engine.Model;
import org.the3deer.android.engine.model.Scene;
import org.the3deer.android.viewer.R;

import java.util.List;

public class CameraDialogFragment extends DialogFragment {

    private static String TAG = "CameraDialogFragment";

    private ModelEngineViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Log.d(TAG, "onCreateDialog. "+viewModel.getActiveFragment().getValue());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine == null) return createNotAvailableDialog(builder, "modelEngine is null");

        final Model engineModel = modelEngine.getBeanFactory().find(Model.class);
        if (engineModel == null) return createNotAvailableDialog(builder, "engineModel is null");

        final Scene activeScene = engineModel.getActiveScene();
        if (activeScene == null) return createNotAvailableDialog(builder, "No active scene");

        // current camera
        final List<Camera> cameras = engineModel.getCameras();
        if (cameras.isEmpty()) return createNotAvailableDialog(builder, "No cameras available");

        // build names
        final String[] cameraNames = new String[cameras.size()];
        for (int i = 0; i < cameras.size(); i++) {
            cameraNames[i] = cameras.get(i).getName();
        }

        // Set the dialog title.
        builder.setTitle(R.string.cameras)
                // Specify the list array, the items to be selected by default (null for
                // none), and the listener through which to receive callbacks when items
                // are selected.
                .setSingleChoiceItems(cameraNames, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activeScene.setActiveCamera(cameras.get(i));
                    }})


                        // Set the action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User taps OK, so save the selectedItems results
                                // somewhere or return them to the component that opens the
                                // dialog.

                            }
                        })
                        /*.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })*/;

        return builder.create();
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