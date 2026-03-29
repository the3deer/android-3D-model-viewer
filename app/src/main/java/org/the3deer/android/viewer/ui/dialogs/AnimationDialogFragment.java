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
import org.the3deer.android.engine.animation.Animation;
import org.the3deer.android.engine.model.Model;
import org.the3deer.android.engine.model.Scene;
import org.the3deer.android.viewer.R;

import java.util.List;

public class AnimationDialogFragment extends DialogFragment {

    private static String TAG = "AnimationDialogFragment";

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

        final ModelEngine modelEngine = viewModel.getActiveEngine().getValue();
        if (modelEngine == null) return createNotAvailableDialog(builder, "ModelEngine is null");

        final Model sceneManager = modelEngine.getBeanFactory().find(Model.class);
        if (sceneManager == null) return createNotAvailableDialog(builder, "SceneManager is null");;

        final Scene currentScene = sceneManager.getActiveScene();
        if (currentScene == null) return createNotAvailableDialog(builder, "No scene available");;;

        final List<org.the3deer.android.engine.animation.Animation> animations = currentScene.getAnimations();
        if (animations == null || animations.isEmpty()) {
            return createNotAvailableDialog(builder, "No animations available");
        }

        final Animation currentAnimation = currentScene.getActiveAnimation();
        final int animationIndex = animations.indexOf(currentAnimation);

        final String[] animationNames = new String[animations.size()+1];
        for (int i = 0; i < animations.size(); i++) {
            animationNames[i] = animations.get(i).getName();
        }
        animationNames[animationNames.length-1] = "None";

        // Set the dialog title.
        builder.setTitle(R.string.animations)
                // Specify the list array, the items to be selected by default (null for
                // none), and the listener through which to receive callbacks when items
                // are selected.
                .setSingleChoiceItems(animationNames, animationIndex, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i < animations.size()) {
                            currentScene.setActiveAnimation(animations.get(i));
                        } else {
                            currentScene.setActiveAnimation(null);
                        }
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