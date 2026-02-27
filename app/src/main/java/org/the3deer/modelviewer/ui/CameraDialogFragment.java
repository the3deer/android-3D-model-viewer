package org.the3deer.modelviewer.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android_3d_model_engine.ModelEngine;
import org.the3deer.android_3d_model_engine.ModelViewModel;
import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Scene;
import org.the3deer.android_3d_model_engine.scene.SceneManager;
import org.the3deer.modelviewer.R;

import java.util.ArrayList;
import java.util.List;

public class CameraDialogFragment extends DialogFragment {

    private static String TAG = "CameraDialogFragment";

    private ModelViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d(TAG, "onCreateDialog. "+viewModel.getRecentId().getValue());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ModelEngine modelEngine = viewModel.getModelEngine();
        if (modelEngine == null) return createNotAvailableDialog(builder, "modelEngine is null");

        final SceneManager sceneManager = modelEngine.getBeanFactory().find(SceneManager.class);
        if (sceneManager == null) return createNotAvailableDialog(builder, "sceneManager is null");

        // current scene
        final Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene == null) return createNotAvailableDialog(builder, "currentScene is null");

        // current camera
        final Camera currentCamera = currentScene.getCamera();
        if (currentCamera == null) return createNotAvailableDialog(builder, "currentCamera is null");

        final List<Camera> cameras = currentScene.getCameras();
        if (cameras == null) return createNotAvailableDialog(builder, "cameras is null");

        final String[] cameraNames = new String[cameras.size()];
        for (int i = 0; i < cameras.size(); i++) {
            cameraNames[i] = cameras.get(i).getName();
        }

        ArrayList selectedItems = new ArrayList();  // Where we track the selected items

        // Set the dialog title.
        builder.setTitle(R.string.cameras)
                // Specify the list array, the items to be selected by default (null for
                // none), and the listener through which to receive callbacks when items
                // are selected.
                .setSingleChoiceItems(cameraNames, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        currentScene.setCamera(cameras.get(i));
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