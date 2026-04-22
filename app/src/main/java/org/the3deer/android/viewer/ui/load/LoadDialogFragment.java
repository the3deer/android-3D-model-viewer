package org.the3deer.android.viewer.ui.load;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;
import org.the3deer.android.util.AndroidUtils;
import org.the3deer.android.util.AssetUtils;
import org.the3deer.android.util.ContentUtils;
import org.the3deer.android.util.FileUtils;
import org.the3deer.android.viewer.MainActivity;
import org.the3deer.android.viewer.R;
import org.the3deer.android.viewer.providers.polyhaven.PolyHaven;
import org.the3deer.android.viewer.ui.DialogFragment;
import org.the3deer.android.viewer.ui.DialogUtils;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.jgltf.model.io.IO;

public class LoadDialogFragment extends DialogFragment {

    private static final Logger logger = Logger.getLogger(LoadDialogFragment.class.getSimpleName());

    private static final URI REPO_URL = URI.create("https://raw.githubusercontent.com/the3deer/android-3D-model-viewer/main/models/index");
    private static final URI REPO_KHRONOS_URL = URI.create("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/model-index.json");
    private static final URI REPO_ASSIMP_URL = URI.create("https://api.github.com/repos/assimp/assimp/contents/test/models/FBX");
    /**
     * Poly Haven repository - free of charge - public domain
     * @see <a href="https://github.com/Poly-Haven/Public-API/blob/master/ToS.md" />
     */
    private static final URI REPO_POLY_HAVEN_URL = URI.create("https://api.polyhaven.com");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private static final int REQUEST_INTERNET_ACCESS = 1001;

    private static final int REQUEST_CODE_OPEN_MATERIAL = 1102;
    private static final int REQUEST_CODE_OPEN_TEXTURE = 1103;
    private static final int REQUEST_CODE_ADD_FILES = 1200;
    private static final String SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|fbx|zip|index)";


    /**
     * This actions corresponds to the "dialog_load_from" string array defined in strings.xml
     */
    private enum Action {
        SAMPLES, REPOSITORY_THE3DEER, REPOSITORY_KHRONOS /*, REPOSITORY_POLYHAVEN, REPOSITORY_ASSIMP*/, ANDROID_EXPLORER  /* deprecated , FILE_EXPLORER */
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
                        launchModelRendererActivity(URI.create("android://"+activity.getPackageName()+"/assets/" + file));
                    }
                });
    }

    private void loadModelFromPolyHaven() {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            PolyHaven.load(activity, (String url) -> {
                if (url != null) {
                    launchModelRendererActivity(URI.create(url));
                }
            });
        }
    }

    private void loadModelFromKhronos(URI url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) return;

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setLoading(true, "Loading Khronos Repository...");
        }
        
        new Thread(() -> {
            try {
                // read JSON
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
                            final URI baseUri = IO.getParent(url);
                            String nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20");
                            String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
                            final String uri = baseUri + nameEncoded + "/glTF-Binary/" + filenameEncoded;
                            files.add(uri);
                        } else if (variants.has("glTF")){
                            String filename = variants.getString("glTF");
                            final URI baseUri = IO.getParent(url);
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
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(URI.create(file));
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Khronos", e);
                activity.runOnUiThread(() -> {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadModelFromRepository(URI url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            logger.log(Level.SEVERE, "Permission not granted");
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Permission not granted", Toast.LENGTH_LONG).show();
            });
            return;
        };

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setLoading(true, "Loading Repository...");
        }
        
        new Thread(() -> {
            try {
                // model files
                final List<String> files = ContentUtils.readLines(url.toString());

                activity.runOnUiThread(() -> {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                if (file.endsWith(".index")) {
                                    loadModelFromRepository(URI.create(file));
                                } else {
                                    launchModelRendererActivity(URI.create(file));
                                }
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Repository", e);
                activity.runOnUiThread(() -> {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
                    Toast.makeText(activity, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadModelFromAssimp(URI url) {
        if (!AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) return;

        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setLoading(true, "Loading Assimp Repository...");
        }
        
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
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
                    DialogUtils.createChooserDialog(activity, "Select file", null,
                        files, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(URI.create(file));
                            }
                        }).create().show();
                });

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error loading Assimp", e);
                activity.runOnUiThread(() -> {
                    if (activity instanceof MainActivity) {
                        ((MainActivity) activity).setLoading(false, null);
                    }
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
                            launchModelRendererActivity(URI.create("file://" + file.getAbsolutePath()));
                        }
                    });
        }
    }

    private void launchModelRendererActivity(URI uri) {
        //sharedViewModel.setActiveFragment(uri.toString());
        dismiss();
        final Bundle arguments = new Bundle();
        arguments.putString("uri", uri.toString());
        activity.runOnUiThread(()->Navigation.findNavController(activity, R.id.nav_host_fragment_content_main).navigate(R.id.nav_home, arguments));
    }
}
