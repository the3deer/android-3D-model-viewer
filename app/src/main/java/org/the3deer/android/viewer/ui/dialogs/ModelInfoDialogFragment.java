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
import org.the3deer.android.engine.model.Model;

public class ModelInfoDialogFragment extends DialogFragment {

    private ModelEngineViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
    }

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

        builder.setTitle("Model Information")
                .setMessage(info.toString())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

    private Dialog createSimpleDialog(AlertDialog.Builder builder, String title, String message) {
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
        return builder.create();
    }
}
