package org.the3deer.android.viewer.ui.load;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.navigation.Navigation;

import org.the3deer.android.viewer.R;
import org.the3deer.android.viewer.providers.*;
import org.the3deer.android.viewer.ui.DialogFragment;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadDialogFragment extends DialogFragment {

    private static final Logger logger = Logger.getLogger(LoadDialogFragment.class.getSimpleName());

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {

        final Action action = position < Action.values().length ? Action.values()[position] : null;
        if (action == null) return;

        try {
            ModelProvider provider = action.getProvider(activity.getApplication());
            if (provider != null) {
                provider.load(activity, uri -> {
                    if (uri != null) launchModelRendererActivity(uri);
                });
                
                // If it's the Android Explorer, we dismiss immediately because it triggers an external activity
                if (action == Action.ANDROID_EXPLORER) dismiss();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(),ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void launchModelRendererActivity(URI uri) {
        dismiss();
        final Bundle arguments = new Bundle();
        arguments.putString("uri", uri.toString());
        activity.runOnUiThread(()->Navigation.findNavController(activity, R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, arguments));
    }
}
