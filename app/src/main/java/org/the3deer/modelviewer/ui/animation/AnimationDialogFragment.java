package org.the3deer.modelviewer.ui.animation;

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
import org.the3deer.android_3d_model_engine.animation.Animation;
import org.the3deer.android_3d_model_engine.model.AnimatedModel;
import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.android_3d_model_engine.model.Scene;
import org.the3deer.android_3d_model_engine.scene.SceneManager;
import org.the3deer.modelviewer.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimationDialogFragment extends DialogFragment {

    private static String TAG = "AnimationDialogFragment";

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
        if (modelEngine == null) return createNotAvailableDialog(builder, "ModelEngine is null");

        final SceneManager sceneManager = modelEngine.getBeanFactory().find(SceneManager.class);
        if (sceneManager == null) return createNotAvailableDialog(builder, "SceneManager is null");;

        final Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene == null) return createNotAvailableDialog(builder, "No scene available");;;

        final Object3DData selectedObject = currentScene.getSelectedObject();
        if (selectedObject == null) return createNotAvailableDialog(builder, "Please select an object first");;;;

        if (!(selectedObject instanceof AnimatedModel)) {
            return createNotAvailableDialog(builder, "Object is not animated");
        }

        final List<Animation> animations = ((AnimatedModel) selectedObject).getAnimations();
        if (animations == null || animations.isEmpty()) {
            return createNotAvailableDialog(builder, "No animations available");
        }

        final Animation currentAnimation = ((AnimatedModel) selectedObject).getCurrentAnimation();
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
                            ((AnimatedModel) selectedObject).setCurrentAnimation(animations.get(i));
                        } else {
                            ((AnimatedModel) selectedObject).setCurrentAnimation(null);
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