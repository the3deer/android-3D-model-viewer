package org.the3deer.app.model3D.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.andresoviedo.dddmodel2.R;
import org.json.JSONArray;
import org.json.JSONObject;
import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.android_3d_model_engine.services.collada.ColladaLoader;
import org.the3deer.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.the3deer.util.android.AndroidUtils;
import org.the3deer.util.android.AssetUtils;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.DialogFragment;
import org.the3deer.util.android.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.javagl.jgltf.model.io.IO;

public class LoadDialogFragment extends DialogFragment {

    private static final URL REPO_URL = AndroidUtils.createURL("https://raw.githubusercontent.com/the3deer/android-3D-model-viewer/main/models/index");
    private static final URL REPO_KHRONOS_URL = AndroidUtils.createURL("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/model-index.json");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int REQUEST_READ_CONTENT_PROVIDER = 1002;

    private static final int REQUEST_CODE_LOAD_MODEL = 1101;
    private static final int REQUEST_CODE_OPEN_MATERIAL = 1102;
    private static final int REQUEST_CODE_OPEN_TEXTURE = 1103;
    private static final int REQUEST_CODE_ADD_FILES = 1200;
    private static final String SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|glb|index)";


    private enum Action {
        SAMPLES, REPOSITORY_THE3DEER, REPOSITORY_KHRONOS, ANDROID_EXPLORER, FILE_EXPLORER
    }

    /**
     * Load file user data
     */
    private final Map<String, Object> loadModelParameters = new HashMap<>();

    public static LoadDialogFragment newInstance(int title) {
        LoadDialogFragment frag = new LoadDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", R.string.alert_dialog_repository);
        args.putInt("items", R.array.dialog_load_items);
        frag.setArguments(args);
        return frag;
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
                    loadModelFromRepository();
                    break;
                case REPOSITORY_KHRONOS:
                    loadModelFromKhronos();
                    break;
                case ANDROID_EXPLORER:
                    loadModelFromContentProvider();
                    break;
                case FILE_EXPLORER:
                    loadModelFromSdCard();
                    break;
            }
        } catch (Exception ex) {
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

    private void loadModelFromRepository() {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            new LoadRepoIndexTask().execute(REPO_URL);
        }
        return;
    }

    private void askRselectRepository() {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            new LoadRepoIndexTask().execute(REPO_URL);
        }

        // testing purposes only
        ContentUtils.showListDialog(activity, "Choose Repository",
                new String[]{"android-3D-model-viewer", "Khronos glTF-Sample-Models"},
                (DialogInterface dialog, int which) -> {
                    if (which == 0) {
                        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
                            new LoadRepoIndexTask().execute(REPO_URL);
                        }
                    } else if (which == 1) {
                        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
                            new LoadRepoKhronos().execute(REPO_KHRONOS_URL);
                        }
                    }
                });
    }



    private void loadModelFromRepository(URL url) {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            new LoadRepoIndexTask().execute(url);
        }
    }

    private void loadModelFromKhronos() {
        if (AndroidUtils.checkPermission(activity, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            new LoadRepoKhronos().execute(REPO_KHRONOS_URL);
        }
    }



    class LoadRepoKhronos extends AsyncTask<URL, Integer, List<String>> {

        private final ProgressDialog dialog;
        private android.app.AlertDialog.Builder chooser;

        public LoadRepoKhronos() {
            this.dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected List<String> doInBackground(URL... urls) {

            try {
                // read json
                final String json = ContentUtils.read(urls[0]);

                // parse json
                final JSONArray jsonArray = new JSONArray(json);

                final List<String> files = new ArrayList<>();
                for (int i=0; i < jsonArray.length(); i++){
                    try {
                        final JSONObject jsonObject = jsonArray.getJSONObject(i);
                        final String name = jsonObject.getString("name");
                        JSONObject variants = jsonObject.getJSONObject("variants");
                        if (variants.has("glTF-Binary")) {
                            String filename = variants.getString("glTF-Binary");
                            final URI baseUri = IO.getParent(urls[0].toURI());
                            String nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20");
                            String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
                            final String uri = baseUri + nameEncoded + "/glTF-Binary/" + filenameEncoded;
                            files.add(uri);
                        } else if (variants.has("glTF")){
                            String filename = variants.getString("glTF");
                            final URI baseUri = IO.getParent(urls[0].toURI());
                            String nameEncoded = URLEncoder.encode(name, "UTF-8").replace("+", "%20");
                            String filenameEncoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
                            final String uri = baseUri + nameEncoded + "/glTF/" + filenameEncoded;
                            files.add(uri);
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                // chooser
                chooser = ContentUtils.createChooserDialog(activity, "Select file", null,
                        files, null, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(Uri.parse(file));
                            }
                        });
                return files;
            } catch (Exception e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (strings == null) {
                Toast.makeText(activity, "Couldn't load repo index", Toast.LENGTH_LONG).show();
                return;
            }

            chooser.create().show();
        }
    }

    class LoadRepoIndexTask extends AsyncTask<URL, Integer, List<String>> {

        private final ProgressDialog dialog;
        private android.app.AlertDialog.Builder chooser;

        public LoadRepoIndexTask() {
            this.dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected List<String> doInBackground(URL... urls) {

            // model files
            final List<String> files = ContentUtils.readLines(urls[0].toString());

            // optional icons
            Map<String, byte[]> icons = null;
            try {
                icons = ContentUtils.readFiles(new URL(urls[0].toString()+".icons.zip"));
            } catch (MalformedURLException ex) {
                Log.e("MenuActivity", ex.getMessage(), ex);
            }

            // chooser
            chooser = ContentUtils.createChooserDialog(activity, "Select file", null,
                    files, icons, SUPPORTED_FILE_TYPES_REGEX,
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
                    });
            return files;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (strings == null) {
                Toast.makeText(activity, "Couldn't load repo index", Toast.LENGTH_LONG).show();
                return;
            }

            chooser.create().show();
        }
    }

    private void loadModelFromSdCard() {
        // check permission starting from android API 23 - Marshmallow
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
        // check permission starting from android API 23 - Marshmallow
        if (AndroidUtils.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_CONTENT_PROVIDER)) {
            loadModelParameters.clear();
            ContentUtils.clearDocumentsProvided();
            ContentUtils.setCurrentDir(null);
            askForFile(REQUEST_CODE_LOAD_MODEL, "*/*");
        }
    }

    private void askForFile(int requestCode, String mimeType) {
        Intent target = ContentUtils.createGetContentIntent(mimeType);
        Intent intent = Intent.createChooser(target, "Select file");
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Error. Please install a file content provider", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentUtils.setThreadActivity(activity);
        try {
            switch (requestCode) {
                case REQUEST_READ_EXTERNAL_STORAGE:
                    loadModelFromSdCard();
                    break;
                case REQUEST_READ_CONTENT_PROVIDER:
                    loadModelFromContentProvider();
                    break;
                case REQUEST_INTERNET_ACCESS:
                    loadModelFromRepository();
                    break;
                case REQUEST_CODE_LOAD_MODEL:
                    if (resultCode != Activity.RESULT_OK) {
                        return;
                    }
                    final Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    onLoadModel(uri);
                    break;
                case REQUEST_CODE_OPEN_MATERIAL:
                    if (resultCode != Activity.RESULT_OK || data.getData() == null) {
                        launchModelRendererActivity(getUserSelectedModel());
                        break;
                    }
                    String filename = (String) loadModelParameters.get("file");
                    ContentUtils.addUri(filename, data.getData());
                    // check if material references texture file
                    String textureFile = WavefrontLoader.getTextureFile(data.getData());
                    if (textureFile == null) {
                        launchModelRendererActivity(getUserSelectedModel());
                        break;
                    }
                    ContentUtils.showDialog(getActivity(), "Select texture file", "This model references a " +
                                    "texture file (" + textureFile + "). Please select it", "OK",
                            "Cancel", (DialogInterface dialog, int which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        launchModelRendererActivity(getUserSelectedModel());
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        loadModelParameters.put("file", textureFile);
                                        askForFile(REQUEST_CODE_OPEN_TEXTURE, "image/*");
                                }
                            });
                    break;
                case REQUEST_CODE_OPEN_TEXTURE:
                    if (resultCode != Activity.RESULT_OK || data.getData() == null) {
                        launchModelRendererActivity(getUserSelectedModel());
                        break;
                    }
                    String textureFilename = (String) loadModelParameters.get("file");
                    ContentUtils.addUri(textureFilename, data.getData());
                    launchModelRendererActivity(getUserSelectedModel());
                    break;
                case REQUEST_CODE_ADD_FILES:

                    // get list of files to prompt to user
                    List<String> files = (List<String>) loadModelParameters.get("files");
                    if (files == null || files.isEmpty()) {
                        launchModelRendererActivity(getUserSelectedModel());
                        break;
                    }

                    // save picked up file
                    final String current = files.remove(0);
                    ContentUtils.addUri(current, data.getData());

                    // no more files then load model...
                    if (files.isEmpty()) {
                        launchModelRendererActivity(getUserSelectedModel());
                        break;
                    }
                    ;

                    final String next = files.get(0);
                    ContentUtils.showDialog(getActivity(), "Select file", "Please select file " + next, "OK",
                            "Cancel", (DialogInterface dialog, int which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        launchModelRendererActivity(getUserSelectedModel());
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        askForFile(REQUEST_CODE_ADD_FILES, "image/*");
                                }
                            });

                    break;
            }
        } catch (Exception ex) {
            Log.e("MenuActivity", ex.getMessage(), ex);
            Toast.makeText(activity, "Unexpected exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onLoadModel(Uri uri) throws IOException {

        // reset status
        ContentUtils.clearDocumentsProvided();

        // save user selected model
        loadModelParameters.put("model", uri);

        // detect model type
        // example: content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3Dv5Vt6bpRbbWqAplsgXWSPOVnJa1cmWj2SQIwTejAt2kl2xistjSRKLP4S-s%3D
        final String fileName = ContentUtils.getFileName(uri);

        // detect model type
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
                    final String fileExtension;
                    if (dotIndex != -1){
                        fileExtension = zipFilename.substring(dotIndex);
                    } else {
                        fileExtension = "?";
                    }

                    // register all zip entries
                    final Uri pseudoUri = Uri.parse("android://" + activity.getPackageName() + "/binary/" + zipFilename);
                    ContentUtils.addUri(zipFilename, pseudoUri);
                    ContentUtils.addData(pseudoUri, zipFile.getValue());

                    // detect model
                    switch (fileExtension) {
                        case ".obj":
                        case ".stl":
                        case ".dae":
                        case ".gltf":
                            modelFile = pseudoUri;
                            break;
                    }
                }
                if (modelFile != null) {
                    launchModelRendererActivity(modelFile);
                }
            }
        } else {
            // no model type from filename, ask user...
            ContentUtils.showListDialog(getActivity(), "Select type", new String[]{"Wavefront (*.obj)", "Stereolithography (*" +
                    ".stl)", "Collada (*.dae)"}, (dialog, which) -> {
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
                // check if model references material file
                String materialFile = WavefrontLoader.getMaterialLib(getUserSelectedModel());
                if (materialFile == null) {
                    launchModelRendererActivity(getUserSelectedModel());
                    break;
                }
                ContentUtils.showDialog(getActivity(), "Select material file", "This model references a " +
                                "material file (" + materialFile + "). Please select it", "OK",
                        "Cancel", (DialogInterface dialog, int which) -> {
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
            case 2: // dae
                final List<String> images = ColladaLoader.getImages(ContentUtils.getInputStream(getUserSelectedModel()));
                if (images == null || images.isEmpty()) {
                    launchModelRendererActivity(getUserSelectedModel());
                } else {

                    Log.i("MenuActivity", "Prompting user to choose files from picker...");

                    loadModelParameters.put("files", images);
                    String file = images.get(0);

                    ContentUtils.showDialog(getActivity(), "Select texture", "This model references a " +
                                    " file (" + file + "). Please select it", "OK",
                            "Cancel", (DialogInterface dialog, int which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        launchModelRendererActivity(getUserSelectedModel());
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        askForFile(REQUEST_CODE_ADD_FILES, "*/*");
                                }
                            });
                }
                break;
        }
    }

    private void launchModelRendererActivity(Uri uri) {

        try {
            Log.i("Menu", "Launching renderer for '" + uri + "'");
            //URI.create(uri.toString());
            ModelFragment modelFragment = ModelFragment.newInstance(uri.toString(), (String) loadModelParameters.get("type"), false);
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, modelFragment, "model")
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            dismiss();
        } catch (Exception e) {
            Log.e("Menu", "Launching renderer for '" + uri + "' failed: "+e.getMessage(),e);
            Toast.makeText(getActivity(), "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
            // info: filesystem url may contain spaces, therefore we re-encode URI
            /*try {
                intent.putExtra("uri", new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString());
            } catch (URISyntaxException ex) {
                Toast.makeText(activity, "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
                return;
            }*/
        }
    }

    /*private void launchModelRendererActivity(Uri uri) {
        Log.i("Menu", "Launching renderer for '" + uri + "'");
        Intent intent = new Intent(activity, ModelActivity.class);
        try {
            URI.create(uri.toString());
            intent.putExtra("uri", uri.toString());
        } catch (Exception e) {
            // info: filesystem url may contain spaces, therefore we re-encode URI
            try {
                intent.putExtra("uri", new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString());
            } catch (URISyntaxException ex) {
                Toast.makeText(activity, "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        }
        intent.putExtra("immersiveMode", "false");

        // content provider case
        if (loadModelParameters.containsKey("type")) {
            intent.putExtra("type", loadModelParameters.get("type").toString());
            //intent.putExtra("backgroundColor", "0.25 0.25 0.25 1");
        }
        loadModelParameters.clear();

        activity.startActivity(intent);
    }*/
}