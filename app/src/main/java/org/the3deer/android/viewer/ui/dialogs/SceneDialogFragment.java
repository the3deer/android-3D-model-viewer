package org.the3deer.android.viewer.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.engine.Model;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.model.Scene;
import org.the3deer.android.viewer.R;

import java.util.List;

public class SceneDialogFragment extends DialogFragment {

    private static String TAG = "SceneDialogFragment";

    private ModelEngineViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //logger.config("onCreateDialog. "+viewModel.getActiveFragment().getValue());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine == null) return createNotAvailableDialog(builder, "ModelEngine is null");

        final Model sceneManager = modelEngine.getBeanFactory().find(Model.class);
        if (sceneManager == null) return createNotAvailableDialog(builder, "SceneManager is null");

        final List<Scene> scenes = sceneManager.getScenes();
        if (scenes == null || scenes.isEmpty()) return createNotAvailableDialog(builder, "No scenes available");

        final String[] sceneNames = new String[scenes.size()];
        for (int i = 0; i < scenes.size(); i++) {
            sceneNames[i] = scenes.get(i).getName();
        }

        // Set the dialog title.
        builder.setTitle(R.string.scenes)
                // Specify the list array, the items to be selected by default (null for
                // none), and the listener through which to receive callbacks when items
                // are selected.
                .setSingleChoiceItems(sceneNames, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sceneManager.setActiveScene(scenes.get(i));
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
        builder.setTitle(getString(R.string.app_name))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }


}