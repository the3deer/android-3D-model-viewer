package org.the3deer.android.viewer.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.engine.Model;
import org.the3deer.android.engine.ModelEngine;
import org.the3deer.android.engine.ModelEngineViewModel;
import org.the3deer.android.engine.model.Dimensions;
import org.the3deer.android.engine.model.Object3D;

import java.util.Locale;

public class ModelInfoDialogFragment extends DialogFragment {

    private ModelEngineViewModel viewModel;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            final Dialog dialog = getDialog();
            if (dialog instanceof AlertDialog) {
                ((AlertDialog) dialog).setMessage(buildInfoText());
            }
            handler.postDelayed(this, 5000);
        }
    };

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
        handler.post(updateRunnable);
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
        info.append(getMemoryInfo()).append("\n\n");


        // active scene
        if (model != null) {
            info.append("--- Stats ---\n");
            info.append("Scenes: ").append(model.getScenes().size()).append("\n");
            if (model.getActiveScene() != null) {
                info.append("Objects: ").append(model.getActiveScene().getObjects().size()).append("\n");
                info.append("Cameras: ").append(model.getActiveScene().getCameras().size()).append("\n");
                info.append("Animations: ").append(model.getActiveScene().getAnimations().size()).append("\n");
            }
            info.append("\n");
        }

        // camera info
        info.append("--- Camera ---\n");
        info.append(getCameraInfo());


        // dimensions info
        info.append("\n\n");
        info.append("--- Object ---\n");
        info.append(getCenterInfo());
        info.append("\n");
        info.append(getDimensionsInfo());

        return info.toString();
    }

    private String getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory());
        long maxMemory = runtime.maxMemory();

        long modelMemory = 0;
        final ModelEngine active = viewModel.getActiveEngine();
        if (active != null && active.getModel() != null) {
            modelMemory = active.getModel().getMemoryUsage();
        }

        return String.format(Locale.getDefault(), "Memory: %d/%d MB\nModel: %d MB",
                usedMemory / 1024 / 1024, maxMemory / 1024 / 1024, modelMemory / 1024 / 1024);
    }

    private String getCameraInfo() {
        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine != null && modelEngine.getModel().getActiveScene() != null && modelEngine.getModel().getActiveScene().getActiveCamera() != null) {
            float[] pos = modelEngine.getModel().getActiveScene().getActiveCamera().getPos();
            return String.format(Locale.US, "Position: %.1f, %.1f, %.1f", pos[0], pos[1], pos[2]);
        }
        return "Position: No active camera";
    }

    private String getPositionInfo() {
        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine != null && modelEngine.getModel().getActiveScene() != null) {
            Object3D selectedObject = modelEngine.getModel().getActiveScene().getSelectedObject();
            if (selectedObject != null) {
                return String.format(Locale.US, "Position: %.1f, %.1f, %.1f", selectedObject.getLocationX(), selectedObject.getLocationY(), selectedObject.getLocationZ());
            }
        }
        return "Position: No object selected";
    }

    private String getCenterInfo() {
        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine != null && modelEngine.getModel().getActiveScene() != null) {
            Object3D selectedObject = modelEngine.getModel().getActiveScene().getSelectedObject();
            if (selectedObject != null) {
                return String.format(Locale.US, "Center: %.1f, %.1f, %.1f", selectedObject.getBoundingBox().getCenter()[0], selectedObject.getBoundingBox().getCenter()[1], selectedObject.getBoundingBox().getCenter()[2]);
            }
        }
        return "Center: No object selected";
    }

    private String getDimensionsInfo() {
        final ModelEngine modelEngine = viewModel.getActiveEngine();
        if (modelEngine != null && modelEngine.getModel().getActiveScene() != null && modelEngine.getModel().getActiveScene().getSelectedObject() != null) {
            Dimensions dim = modelEngine.getModel().getActiveScene().getSelectedObject().getDimensions();
            return String.format(Locale.US, "Dimensions: %.1f, %.1f, %.1f", dim.getWidth(), dim.getHeight(), dim.getDepth());
        }
        return "Dimensions: No object selected";
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
        handler.removeCallbacks(updateRunnable);
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