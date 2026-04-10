package org.the3deer.android.viewer.ui.load;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.the3deer.android.util.ContentUtils;
import org.the3deer.android.viewer.MainActivity;
import org.the3deer.android.viewer.R;
import org.the3deer.android.viewer.SharedViewModel;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class LoadContentDialog {

    private static final Logger logger = Logger.getLogger(LoadContentDialog.class.getSimpleName());
    private final static String SUPPORTED_MODELS_EXTENSIONS = "ob,stl,dae,gltf,glb,fbx,zip";
    private final static Pattern SUPPORTED_MODELS_REGEX =
            Pattern.compile("(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)");
    /**
     * Activity
     */
    private final MainActivity activity;
    /**
     * Selected model
     */
    private Map<String, Object> arguments = new HashMap<>();
    /**
     * Model url (linked files)
     */
    private URI parentUri;

    private SharedViewModel sharedViewModel;

    public LoadContentDialog(MainActivity activity) {
        this.activity = activity;

        sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
    }

    private Activity getActivity() {
        return activity;
    }

    public ActivityResultContracts.GetContent getActivityContract() {
        return new ActivityResultContracts.GetContent();
    }

    public void start() {
        // reset status
        /**
         * File being processed
         */
        arguments.clear();
        //ContentUtils.setContext(getActivity());
        ContentUtils.clearDocumentsProvided();

        // inform user


        // pick model
        pick("model", "*/*");
    }

    /**
     * ask for file
     *
     * @param nextAction
     * @param mimeType
     */
    private void pick(String nextAction, String mimeType) {
        /**
         * Model part
         */
        this.activity.pick(mimeType);
    }

    /**
     * Process the resource and checks for next steps...
     *
     * @param uri model resource
     * @throws IOException
     */
    public void load(URI uri) throws IOException, InterruptedException {

        // detect model type
        // example: content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3Dv5Vt6bpRbbWqAplsgXWSPOVnJa1cmWj2SQIwTejAt2kl2xistjSRKLP4S-s%3D
        final String fileName = ContentUtils.getFileName(getActivity().getApplicationContext(), URI.create(uri.toString()));
        final String fileType = fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() : "unsupported";

        // check
        if (fileName == null) throw new IllegalArgumentException("No filename found for " + uri);

        // check if zip
        switch (fileType) {
            case "obj":
            case "stl":
            case "dae":
            case "gltf":
            case "glb":
            case "fbx":
                launchFragment(uri, fileName, fileType);
                break;
            case "zip":

                // sanitize filename
                final String fileNameSanitized = fileName.replaceAll(" ", "+");

                // create uri
                final URI dataUri = URI.create("android://" + getActivity().getPackageName() + "/binary/" + fileNameSanitized);

                // register resource
                ContentUtils.addUri(fileName, dataUri);

                // FIXME: potential out of memory error
                final Map<String, byte[]> zipFiles = ContentUtils.readFiles(URI.create(uri.toString()));
                URI modelFile = null;
                String extension = null;
                for (Map.Entry<String, byte[]> zipFile : zipFiles.entrySet()) {

                    final String zipFilename = zipFile.getKey();
                    final int dotIndex = zipFilename.lastIndexOf('.');
                    final String fileExtension;
                    if (dotIndex != -1) {
                        fileExtension = zipFilename.substring(dotIndex);
                    } else {
                        continue; // it's probably a folder. ignore it
                    }

                    // sanitize filename
                    final String fileNameEntrySanitized = zipFilename.replaceAll(" ", "+");

                    // build uri
                    final URI pseudoUri = dataUri.resolve(fileNameEntrySanitized);

                    // register all zip entries
                    ContentUtils.addUri(fileNameEntrySanitized, pseudoUri);
                    ContentUtils.addData(pseudoUri, zipFile.getValue());

                    // detect model
                    switch (fileExtension) {
                        case ".obj":
                        case ".stl":
                        case ".dae":
                        case ".gltf":
                        case ".fbx":
                        case ".glb":
                            modelFile = pseudoUri;
                            logger.info("Found model in zip:");
                            extension = fileExtension;
                            break;
                    }
                }
                if (modelFile != null) {
                    launchFragment(modelFile, fileName, extension);
                } else {
                    throw new IllegalArgumentException("No model found in zip file: " + uri);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown extension: " + fileName +
                        ". Valid extensions are " + SUPPORTED_MODELS_EXTENSIONS);
        }
    }


    private void launchFragment(URI uri, String name, String type) {
        final Bundle arguments = new Bundle();
        arguments.putString("uri", uri.toString());
        arguments.putString("name", name);
        arguments.putString("type", type);
        activity.runOnUiThread(() -> Navigation.findNavController(activity, R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, arguments));
    }
}
