package org.the3deer.android.viewer.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.engine.Model;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;

public class ModelInfoDialogFragment extends DialogFragment {

    private ModelEngineViewModel viewModel;

    private Observer<String> observer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ModelEngineViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Model Information")
                .setMessage(buildInfoText())
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Dialog dialog = getDialog();
        if (dialog instanceof AlertDialog) {
            final AlertDialog alertDialog = (AlertDialog) dialog;
            observer = (info) -> {
                final ModelEngine modelEngine = viewModel.getActiveEngine();
                if (modelEngine != null && modelEngine.getModel() != null) {
                    alertDialog.setMessage(buildInfoText());
                }
            };
            // Observe memory info and update the dialog message in real-time
            viewModel.memoryInfo.observe(this, observer);
        }
    }

    /**
     * Builds the information text for the model, including real-time memory stats.
     *
     * @return the formatted information string
     */
    private String buildInfoText() {

        final ModelEngine modelEngine = viewModel.getActiveEngine();
        Model model = null;
        if (modelEngine != null) {
            model = modelEngine.getModel();
        }

        final StringBuilder info = new StringBuilder();

        // model info
        if (model != null) {
            info.append("Model: ").append(model.getName()).append("\n");
            info.append("Type: ").append(model.getType()).append("\n");
            info.append("Uri: ").append(model.getUri()).append("\n\n");
        }

        // memory info
        info.append("--- Memory ---\n");
        info.append(viewModel.memoryInfo.getValue()).append("\n\n");

        // active scene
        if (model != null) {
            info.append("--- Stats ---\n");
            info.append("Scenes: ").append(model.getScenes().size()).append("\n");
            if (model.getActiveScene() != null) {
                info.append("Objects: ").append(model.getActiveScene().getObjects().size()).append("\n");
                info.append("Cameras: ").append(model.getActiveScene().getCameras().size()).append("\n");
                info.append("Animations: ").append(model.getActiveScene().getAnimations().size()).append("\n");
            }
        }
        return info.toString();
    }

    private Dialog createSimpleDialog(final AlertDialog.Builder builder, final String title, final String message) {
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (observer != null) {
            viewModel.memoryInfo.removeObserver(observer);
            observer = null;
        }
    }

    @Override
    public void onDestroyView() {
        // [SAFE APPLY] Clear listeners to prevent the Message.obj leak in the framework
        // This breaks the reference chain: Handler -> Message -> Listener -> DialogFragment
        final Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setOnDismissListener(null);
            dialog.setOnCancelListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel = null;
    }
}