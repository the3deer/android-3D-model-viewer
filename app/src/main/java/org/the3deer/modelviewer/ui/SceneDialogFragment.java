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
import org.the3deer.android_3d_model_engine.model.Scene;
import org.the3deer.android_3d_model_engine.scene.SceneManager;
import org.the3deer.modelviewer.R;

import java.util.ArrayList;
import java.util.List;

public class SceneDialogFragment extends DialogFragment {

    private static String TAG = "SceneDialogFragment";

    private ModelViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelViewModel.class);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d(TAG, "onCreateDialog. "+viewModel.getRecentId().getValue());
        final ModelEngine modelEngine = viewModel.getModelEngine();
        if (modelEngine == null) return null;

        final SceneManager sceneManager = modelEngine.getBeanFactory().find(SceneManager.class);
        if (sceneManager == null) return null;

        final List<Scene> scenes = sceneManager.getScenes();
        final String[] sceneNames = new String[scenes.size()];
        for (int i = 0; i < scenes.size(); i++) {
            sceneNames[i] = scenes.get(i).getName();
        }

        ArrayList selectedItems = new ArrayList();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title.
        builder.setTitle(R.string.scenes)
                // Specify the list array, the items to be selected by default (null for
                // none), and the listener through which to receive callbacks when items
                // are selected.
                .setSingleChoiceItems(sceneNames, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sceneManager.setCurrentScene(scenes.get(i));
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


}