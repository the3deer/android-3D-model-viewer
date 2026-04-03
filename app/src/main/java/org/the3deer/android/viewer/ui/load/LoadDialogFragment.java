package org.the3deer.android.viewer.ui.load;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.the3deer.android.viewer.R;
import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.providers.polyhaven.PolyHaven;
import org.the3deer.android.viewer.ui.DialogFragment;
import org.the3deer.android.viewer.ui.DialogUtils;
import org.the3deer.engine.android.util.AndroidUtils;
import org.the3deer.engine.android.util.AssetUtils;
import org.the3deer.engine.android.util.ContentUtils;
import org.the3deer.engine.android.util.FileUtils;
import org.the3deer.engine.services.wavefront.WavefrontLoader;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.jgltf.model.io.IO;

public class LoadDialogFragment extends DialogFragment {

    private static final Logger logger = Logger.getLogger(LoadDialogFragment.class.getSimpleName());

    private static final URL REPO_URL = AndroidUtils.createURL("https://raw.githubusercontent.com/the3deer/android-3D-model-viewer/main/models/index");
    private static final URL REPO_KHRONOS_URL = AndroidUtils.createURL("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/model-index.json");
    private static final URL REPO_ASSIMP_URL = AndroidUtils.createURL("https://api.github.com/repos/assimp/assimp/contents/test/models/FBX");
    /**
     * Poly Haven repository - free of charge - public domain
     * @see <a href="https://github.com/Poly-Haven/Public-API/blob/master/ToS.md" />
     */
    private static final URL REPO_POLY_HAVEN_URL = AndroidUtils.createURL("https://api.polyhaven.com");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int REQUEST_READ_CONTENT_PROVIDER = 1002;

    private static final int REQUEST_CODE_LOAD_MODEL = 1101;
    private static final int REQUEST_CODE_OPEN_MATERIAL = 1102;
    private static final int REQUEST_CODE_OPEN_TEXTURE = 1103;
    private static final int REQUEST_CODE_ADD_FILES = 1200;
    private static final String SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|glb|zip|index)";


    /**
     * This actions corresponds to the "dialog_load_from" string array defined in strings.xml
     */
    private enum Action {
        SAMPLES, REPOSITORY_THE3DEER, REPOSITORY_KHRONOS /*, REPOSITORY_POLYHAVEN, REPOSITORY_ASSIMP*/, ANDROID_EXPLORER  /* deprecated , FILE_EXPLORER */
    }

    /**
     * Load file user data
     */
    private final Map<String, Object> loadModelParameters = new HashMap<>();

    private SharedViewModel sharedViewModel;

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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {

        final Action action;
        try {
            switch (Action.values()[position]) {
                case SAMPLES:
                    loadModelFromAssets();
                    break;
                case REPOSITORY_THE3DEER:
                    loadModelFromRepository(REPO_URL);
                    break;
                case REPOSITORY_KHRONOS:
                    loadModelFromKhronos(REPO_KHRONOS_URL);
                    break;
                /*case REPOSITORY_ASSIMP:
                    loadModelFromAssimp(REPO_ASSIMP_URL);
                    break;
                case REPOSITORY_POLYHAVEN:
                    loadModelFromPolyHaven();
                    break;*/
                case ANDROID_EXPLORER:
                    Bundle result = new Bundle();
                    result.putString("action", "pick");
                    activity.getSupportFragmentManager().setFragmentResult("app", result);
                    dismiss();
                    break;
                /*case FILE_EXPLORER:
                    loadModelFromSdCard();
                    break;*/
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(),ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void loadModelFromAssets() {
        AssetUtils.createChooserDialog(activity, "Select file", null, "models", SUPPORTED_FILE_TYPES_REGEX,
                (String file) -> {
                    if (file != null) {
                        ContentUtils.provideAssets(activity);
                        launchModelRendererActivity(Uri.parse("android://"+activity.getPackageName()+"/assets/" + file));
                    }
                });
    }

    private void loadModelFromPolyHaven() {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            PolyHaven.load(activity, (String url) -> {
                if (url != null) {
                    launchModelRendererActivity(Uri.parse(url));
                }
            });
        }
    }

    private void loadModelFromKhronos(URL url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) return;

        final ProgressDialog progress = ProgressDialog.show(activity, "", "Loading Khronos Repository...", true);
        
        new Thread(() -> {
            try {
                // read json
                final String json = ContentUtils.read(url);
                final JSONArray jsonArray = new JSONArray(json);
                final List<String> files = new ArrayList<>();
                for (int i=0; i < jsonArray.length(); i++){
                    try {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final String name = jsonObject.getString("name");
                        JSONObject variants = jsonObject.getJSONObject("variants");
                        if (variants.has("glTF-Binary")) {
                            String filename = variants.getString("glTF-Binary");
                            final URI baseUri = IO.getParent(url.toURI());
                            String nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20");
                            String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
                            final String uri = baseUri + nameEncoded + "/glTF-Binary/" + filenameEncoded;
                            files.add(uri);
                        } else if (variants.has("glTF")){
                            String filename = variants.getString("glTF");
                            final URI baseUri = IO.getParent(url.toURI());
                            String nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20");
                            String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
                            final String uri = baseUri + nameEncoded + "/glTF/" + filenameEncoded;
                            files.add(uri);
                        }
                    } catch (Exception e){
                        logger.log(Level.SEVERE, "Error parsing item", e);
                    }
                }

                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(Uri.parse(file));
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Khronos", e);
                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadModelFromRepository(URL url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            logger.log(Level.SEVERE, "Permission not granted");
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_LONG).show();
            });
            return;
        };

        final ProgressDialog progress = ProgressDialog.show(activity, "", "Loading Repository...", true);
        
        new Thread(() -> {
            try {
                // model files
                final List<String> files = ContentUtils.readLines(url.toString());

                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                if (file.endsWith(".index")) {
                                    try {
                                        loadModelFromRepository(new URL(file));
                                    } catch (MalformedURLException e) {
                                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    launchModelRendererActivity(Uri.parse(file));
                                }
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Repository", e);
                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadModelFromAssimp(URL url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) return;

        final ProgressDialog progress = ProgressDialog.show(activity, "", "Loading Assimp Repository...", true);
        
        new Thread(() -> {
            try {
                final String json = ContentUtils.read(url);
                final JSONArray jsonArray = new JSONArray(json);
                final List<String> files = new ArrayList<>();
                for (int i=0; i < jsonArray.length(); i++){
                    try {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        files.add(jsonObject.getString("download_url"));
                    } catch (Exception e){
                        logger.log(Level.SEVERE, "Error parsing item", e);
                    }
                }

                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(Uri.parse(file));
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Assimp", e);
                activity.runOnUiThread(() -> {
                    progress.dismiss();
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadModelFromSdCard() {
        if (AndroidUtils.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            FileUtils.createChooserDialog(activity, "Select file", null, null, SUPPORTED_FILE_TYPES_REGEX,
                    (File file) -> {
                        if (file != null) {
                            ContentUtils.setCurrentDir(file.getParentFile());
                            launchModelRendererActivity(Uri.parse("file://" + file.getAbsolutePath()));
                        }
                    });
        }
    }

    private void loadModelFromContentProvider() {
        if (AndroidUtils.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_CONTENT_PROVIDER)) {
            loadModelParameters.clear();
            ContentUtils.clearDocumentsProvided();
            ContentUtils.setCurrentDir(null);
            askForFile(REQUEST_CODE_LOAD_MODEL, "*/*");
        }
    }

    private void askForFile(int requestCode, String mimeType) {
        try {
            // logger.info("Opening file picker...");
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Error. Please install a file content provider", Toast.LENGTH_LONG).show();
        }
    }

    private void onLoadModel(Uri uri) throws IOException {
        ContentUtils.clearDocumentsProvided();
        loadModelParameters.put("model", uri);
        final String fileName = ContentUtils.getFileName(getContext(), uri);

        if (fileName != null) {
            if (fileName.toLowerCase().endsWith(".obj")) {
                askForRelatedFiles(0);
            } else if (fileName.toLowerCase().endsWith(".stl")) {
                askForRelatedFiles(1);
            } else if (fileName.toLowerCase().endsWith(".dae")) {
                askForRelatedFiles(2);
            } else if (fileName.toLowerCase().endsWith(".gltf")) {
                askForRelatedFiles(3);
            } else if (fileName.toLowerCase().endsWith(".zip")) {
                final Map<String, byte[]> zipFiles = ContentUtils.readFiles(new URL(uri.toString()));
                Uri modelFile = null;
                for (Map.Entry<String, byte[]> zipFile : zipFiles.entrySet()) {
                    final String zipFilename = zipFile.getKey();
                    final int dotIndex = zipFilename.lastIndexOf('.');
                    final String fileExtension = dotIndex != -1 ? zipFilename.substring(dotIndex) : "?";

                    final Uri pseudoUri = Uri.parse("android://" + activity.getPackageName() + "/binary/" + zipFilename);
                    ContentUtils.addUri(zipFilename, pseudoUri);
                    ContentUtils.addData(pseudoUri, zipFile.getValue());

                    if (fileExtension.matches("\\.(obj|stl|dae|gltf)")) {
                        modelFile = pseudoUri;
                    }
                }
                if (modelFile != null) {
                    launchModelRendererActivity(modelFile);
                }
            }
        } else {
            ContentUtils.showListDialog(getActivity(), "Select type", new String[]{"Wavefront (*.obj)", "Stereolithography (*.stl)", "Collada (*.dae)"}, (dialog, which) -> {
                try {
                    askForRelatedFiles(which);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Uri getUserSelectedModel() {
        return (Uri) loadModelParameters.get("model");
    }

    private void askForRelatedFiles(int modelType) throws IOException {
        loadModelParameters.put("type", modelType);
        switch (modelType) {
            case 0: // obj
                String materialFile = WavefrontLoader.getMaterialLib(getUserSelectedModel());
                if (materialFile == null) {
                    launchModelRendererActivity(getUserSelectedModel());
                    break;
                }
                ContentUtils.showDialog(getActivity(), "Select material file", "This model references a material file (" + materialFile + "). Please select it", "OK", "Cancel", (DialogInterface dialog, int which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_NEGATIVE:
                            launchModelRendererActivity(getUserSelectedModel());
                            break;
                        case DialogInterface.BUTTON_POSITIVE:
                            loadModelParameters.put("file", materialFile);
                            askForFile(REQUEST_CODE_OPEN_MATERIAL, "*/*");
                    }
                });
                break;
            case 1: // stl
                launchModelRendererActivity(getUserSelectedModel());
                break;
        }
    }

    private void launchModelRendererActivity(Uri uri) {
        //sharedViewModel.setActiveFragment(uri.toString());
        dismiss();
        final Bundle arguments = new Bundle();
        arguments.putString("uri", uri.toString());
        activity.runOnUiThread(()->Navigation.findNavController(activity, R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, arguments));
    }
}
