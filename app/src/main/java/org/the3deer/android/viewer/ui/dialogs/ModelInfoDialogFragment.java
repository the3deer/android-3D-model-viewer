package org.the3deer.android.viewer.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.engine.Model;
import org.the3deer.engine.ModelEngine;
import org.the3deer.engine.android.ModelEngineViewModel;

public class ModelInfoDialogFragment extends DialogFragment {

    private ModelEngineViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine == null) {
            return createSimpleDialog(builder, "Information", "No active engine available.");
        }

        final Model model = modelEngine.getModel();
        if (model == null) {
            return createSimpleDialog(builder, "Information", "No model data available.");
        }

        builder.setTitle("Model Information")
                .setMessage(buildInfoText(model))
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog) {
            final AlertDialog alertDialog = (AlertDialog) dialog;

            // Observe memory info and update the dialog message in real-time
            viewModel.memoryInfo.observe(this, memory -> {
                final ModelEngine modelEngine = viewModel.getActiveEngine();
                if (modelEngine != null && modelEngine.getModel() != null) {
                    alertDialog.setMessage(buildInfoText(modelEngine.getModel()));
                }
            });
        }
    }

    /**
     * Builds the information text for the model, including real-time memory stats.
     *
     * @param model the model to show info for
     * @return the formatted information string
     */
    private String buildInfoText(Model model) {
        StringBuilder info = new StringBuilder();
        info.append("Model: ").append(model.getName()).append("\n");
        info.append("Type: ").append(model.getType()).append("\n");
        info.append("Uri: ").append(model.getUri()).append("\n\n");

        info.append("--- Memory ---\n");
        info.append(viewModel.memoryInfo.getValue()).append("\n\n");

        info.append("--- Stats ---\n");
        info.append("Scenes: ").append(model.getScenes().size()).append("\n");
        if (model.getActiveScene() != null) {
            info.append("Objects: ").append(model.getActiveScene().getObjects().size()).append("\n");
            info.append("Cameras: ").append(model.getActiveScene().getCameras().size()).append("\n");
            info.append("Animations: ").append(model.getActiveScene().getAnimations().size()).append("\n");
        }
        return info.toString();
    }

    private Dialog createSimpleDialog(AlertDialog.Builder builder, String title, String message) {
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
        return builder.create();
    }
}
