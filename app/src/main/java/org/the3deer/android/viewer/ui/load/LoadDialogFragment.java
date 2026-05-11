package org.the3deer.android.viewer.ui.load;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.providers.AndroidExplorerModelProvider;
import org.the3deer.android.viewer.providers.AssetsModelProvider;
import org.the3deer.android.viewer.providers.KhronosModelProvider;
import org.the3deer.android.viewer.providers.ModelProvider;
import org.the3deer.android.viewer.providers.PolyHavenModelProvider;
import org.the3deer.android.viewer.providers.RepositoryModelProvider;
import org.the3deer.android.viewer.providers.SdCardModelProvider;
import org.the3deer.android.viewer.ui.DialogFragment;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadDialogFragment extends DialogFragment {

    private static final Logger logger = Logger.getLogger(LoadDialogFragment.class.getSimpleName());

    private SharedViewModel sharedViewModel;

    /**
     * This actions corresponds to the "dialog_load_from" string array defined in strings.xml
     */
    private enum Action {
        SAMPLES, 
        REPOSITORY_THE3DEER, 
        REPOSITORY_KHRONOS, 
        REPOSITORY_POLYHAVEN, 
        ANDROID_EXPLORER,
        FILE_EXPLORER;

        ModelProvider getProvider(android.app.Application application) {
            switch (this) {
                case SAMPLES:
                    return new AssetsModelProvider(application);
                case REPOSITORY_THE3DEER:
                    return new RepositoryModelProvider();
                case REPOSITORY_KHRONOS:
                    return new KhronosModelProvider();
                case REPOSITORY_POLYHAVEN:
                    return new PolyHavenModelProvider();
                case ANDROID_EXPLORER:
                    return new AndroidExplorerModelProvider();
                case FILE_EXPLORER:
                    return new SdCardModelProvider();
                default:
                    return null;
            }
        }
    }

    public static LoadDialogFragment newInstance(int title, String[] items) {
        LoadDialogFragment frag = new LoadDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArray("items", items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {

        final Action action = position < Action.values().length ? Action.values()[position] : null;
        if (action == null) return;

        try {
            ModelProvider provider = action.getProvider(activity.getApplication());
            if (provider != null) {
                provider.load(activity, (model) -> {
                    if (model != null) {
                        dismiss();
                        sharedViewModel.loadModel(model);
                    }
                });
                
                // If it's the Android Explorer, we dismiss immediately because it triggers an external activity
                if (action == Action.ANDROID_EXPLORER) dismiss();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(),ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
