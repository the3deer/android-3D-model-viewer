package org.the3deer.app.model3D.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.andresoviedo.dddmodel2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.the3deer.android_3d_model_engine.services.collada.ColladaLoader;
import org.the3deer.android_3d_model_engine.services.wavefront.WavefrontLoader;
import org.the3deer.app.model3D.demo.EarCutDemoActivity;
import org.the3deer.app.model3D.demo.GlyphsDemoActivity;
import org.the3deer.polybool.demo.WebActivity;
import org.the3deer.util.android.AndroidUtils;
import org.the3deer.util.android.AssetUtils;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.FileUtils;
import org.the3deer.util.view.TextActivity;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.javagl.jgltf.model.io.IO;

public class MenuActivity extends ListActivity {

    private static final URL REPO_URL = createURL("https://raw.githubusercontent.com/the3deer/android-3D-model-viewer/main/models/index");
    private static final URL REPO_KHRONOS_URL = createURL("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/main/2.0/model-index.json");
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private static final int REQUEST_INTERNET_ACCESS = 1001;
    private static final int REQUEST_READ_CONTENT_PROVIDER = 1002;

    private static final int REQUEST_CODE_LOAD_MODEL = 1101;
    private static final int REQUEST_CODE_OPEN_MATERIAL = 1102;
    private static final int REQUEST_CODE_OPEN_TEXTURE = 1103;
    private static final int REQUEST_CODE_ADD_FILES = 1200;
    private static final String SUPPORTED_FILE_TYPES_REGEX = "(?i).*\\.(obj|stl|dae|gltf|index)";


    private enum Action {
        LOAD_MODEL, CARGAR_MODELO, GITHUB, SETTINGS, HELP, AYUDA, ABOUT, ACERCA, EXIT, SALIR, UNKNOWN, DEMO, DEMOS
    }

    /**
     * Load file user data
     */
    private Map<String, Object> loadModelParameters = new HashMap<>();

    private static URL createURL(String url) {
        try {
            return new URL(url);
        } catch(MalformedURLException e){
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setListAdapter(new ArrayAdapter<>(this, R.layout.activity_menu_item,
                getResources().getStringArray(R.array.menu_items)));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String selectedItem = (String) getListView().getItemAtPosition(position);
        // Toast.makeText(getApplicationContext(), "Click ListItem '" + selectedItem + "'", Toast.LENGTH_LONG).show();
        String selectedAction = selectedItem.replace(' ', '_')
                .replaceAll("\\.", "")
        .toUpperCase(Locale.getDefault());
        Action action = Action.UNKNOWN;
        try {
            action = Action.valueOf(selectedAction);
        } catch (IllegalArgumentException ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        try {
            switch (action) {
                case DEMOS:
                    ContentUtils.showListDialog(this, "Demos List", new String[]{"Random Objects", "GUI", "EarCut Algebra", "PolyBool Algebra"}, (DialogInterface dialog, int which) -> {
                        if (which == 0) {
                            Intent demoIntent = new Intent(MenuActivity.this.getApplicationContext(), ModelActivity.class);
                            demoIntent.putExtra("immersiveMode", "false");
                            demoIntent.putExtra("backgroundColor", "0 0 0 1");
                            MenuActivity.this.startActivity(demoIntent);
                        } else if (which == 1) {
                            Intent demoIntent = new Intent(MenuActivity.this.getApplicationContext(), GlyphsDemoActivity.class);
                            MenuActivity.this.startActivity(demoIntent);
                        } else if (which == 2) {
                            Intent demoIntent = new Intent(MenuActivity.this.getApplicationContext(), EarCutDemoActivity.class);
                            MenuActivity.this.startActivity(demoIntent);
                        } else if (which == 3) {
                            Intent demoIntent = new Intent(MenuActivity.this.getApplicationContext(), WebActivity.class);
                            MenuActivity.this.startActivity(demoIntent);
                        } else {
                            // TODO:
                        }
                    });
                    break;
                case DEMO:
                    Intent demoIntent = new Intent(MenuActivity.this.getApplicationContext(), ModelActivity.class);
                    demoIntent.putExtra("immersiveMode", "false");
                    demoIntent.putExtra("backgroundColor", "0 0 0 1");
                    MenuActivity.this.startActivity(demoIntent);
                    break;
                case GITHUB:
                    AndroidUtils.openUrl(this, "https://github.com/the3deer/android-3D-model-viewer");
                    break;
                case LOAD_MODEL:
                case CARGAR_MODELO:
                    loadModel();
                    break;
                case ABOUT:
                case ACERCA:
                    Intent aboutIntent = new Intent(MenuActivity.this.getApplicationContext(), TextActivity.class);
                    aboutIntent.putExtra("title", selectedItem);
                    aboutIntent.putExtra("text", getResources().getString(R.string.about_text));
                    MenuActivity.this.startActivity(aboutIntent);
                    break;
                case HELP:
                case AYUDA:
                    Intent helpIntent = new Intent(MenuActivity.this.getApplicationContext(), TextActivity.class);
                    helpIntent.putExtra("title", selectedItem);
                    helpIntent.putExtra("text", getResources().getString(R.string.help_text));
                    MenuActivity.this.startActivity(helpIntent);
                    break;
                case SETTINGS:
                    break;
                case EXIT:
                case SALIR:
                    MenuActivity.this.finish();
                    break;
                case UNKNOWN:
                    Toast.makeText(getApplicationContext(), "Unrecognized action '" + selectedAction + "'",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void loadModel() {
        ContentUtils.showListDialog(this, "File Provider", new String[]{"Samples", "Repository",
                "Android Explorer", "File Explorer (Android <= 10)"}, (DialogInterface dialog, int which) -> {
            if (which == 0) {
                loadModelFromAssets();
            } else if (which == 1) {
                loadModelFromRepository();
            } else if (which == 2) {
                loadModelFromContentProvider();
            } else {
                loadModelFromSdCard();
            }
        });

    }

    private void loadModelFromRepository() {

        if (true){
            if (AndroidUtils.checkPermission(this, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
                new LoadRepoIndexTask().execute(REPO_URL);
            }
            return;
        }

        // testing purposes only
        ContentUtils.showListDialog(this, "Choose Repository",
                new String[]{"android-3D-model-viewer", "Khronos glTF-Sample-Models"},
                (DialogInterface dialog, int which) -> {
            if (which == 0) {
                if (AndroidUtils.checkPermission(this, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
                    new LoadRepoIndexTask().execute(REPO_URL);
                }
            } else if (which == 1) {
                if (AndroidUtils.checkPermission(this, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
                    new LoadRepoKhronos().execute(REPO_KHRONOS_URL);
                }
            }
        });
    }

    private void loadModelFromAssets() {
        AssetUtils.createChooserDialog(this, "Select file", null, "models", SUPPORTED_FILE_TYPES_REGEX,
                (String file) -> {
                    if (file != null) {
                        ContentUtils.provideAssets(this);
                        launchModelRendererActivity(Uri.parse("android://"+getPackageName()+"/assets/" + file));
                    }
                });
    }

    private void loadModelFromRepository(URL url) {
        if (AndroidUtils.checkPermission(this, Manifest.permission.INTERNET, REQUEST_INTERNET_ACCESS)) {
            new LoadRepoIndexTask().execute(url);
        }
    }

    class LoadRepoKhronos extends AsyncTask<URL, Integer, List<String>> {

        private final ProgressDialog dialog;
        private AlertDialog.Builder chooser;

        public LoadRepoKhronos() {
            this.dialog = new ProgressDialog(MenuActivity.this);
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
                    final JSONObject jsonObject = jsonArray.getJSONObject(i);
                    final String name = jsonObject.getString("name");
                    JSONObject variants = jsonObject.getJSONObject("variants");
                    for (Iterator<String> it = variants.keys(); it.hasNext(); ) {
                        final String variant = it.next();
                        final URI baseUri = IO.getParent(urls[0].toURI());
                        final String filename = variants.getString(variant);
                        final String uri = baseUri
                                + name + "/"+ variant + "/" + filename;
                        files.add(uri);
                    }
                }

                // chooser
                chooser = ContentUtils.createChooserDialog(MenuActivity.this, "Select file", null,
                        files, null, SUPPORTED_FILE_TYPES_REGEX,
                        (String file) -> {
                            if (file != null) {
                                launchModelRendererActivity(Uri.parse(file));
                            }
                        });
                return files;
            } catch (JSONException | URISyntaxException e) {
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
                Toast.makeText(MenuActivity.this, "Couldn't load repo index", Toast.LENGTH_LONG).show();
                return;
            }

            chooser.create().show();
        }
    }

    class LoadRepoIndexTask extends AsyncTask<URL, Integer, List<String>> {

        private final ProgressDialog dialog;
        private AlertDialog.Builder chooser;

        public LoadRepoIndexTask() {
            this.dialog = new ProgressDialog(MenuActivity.this);
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
            chooser = ContentUtils.createChooserDialog(MenuActivity.this, "Select file", null,
                    files, icons, SUPPORTED_FILE_TYPES_REGEX,
                    (String file) -> {
                        if (file != null) {
                            if (file.endsWith(".index")) {
                                try {
                                    loadModelFromRepository(new URL(file));
                                } catch (MalformedURLException e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(MenuActivity.this, "Couldn't load repo index", Toast.LENGTH_LONG).show();
                return;
            }

            chooser.create().show();
        }
    }

    private void loadModelFromSdCard() {
        // check permission starting from android API 23 - Marshmallow
        if (AndroidUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_EXTERNAL_STORAGE)) {
            FileUtils.createChooserDialog(this, "Select file", null, null, SUPPORTED_FILE_TYPES_REGEX,
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
        if (AndroidUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_CONTENT_PROVIDER)) {
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
            Toast.makeText(this, "Error. Please install a file content provider", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentUtils.setThreadActivity(this);
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
                    if (resultCode != RESULT_OK) {
                        return;
                    }
                    final Uri uri = data.getData();
                    if (uri == null) {
                        return;
                    }
                    onLoadModel(uri);
                    break;
                case REQUEST_CODE_OPEN_MATERIAL:
                    if (resultCode != RESULT_OK || data.getData() == null) {
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
                    ContentUtils.showDialog(this, "Select texture file", "This model references a " +
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
                    if (resultCode != RESULT_OK || data.getData() == null) {
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
                    ContentUtils.showDialog(this, "Select file", "Please select file " + next, "OK",
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
            Toast.makeText(this, "Unexpected exception: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onLoadModel(Uri uri) throws IOException {

        // save user selected model
        loadModelParameters.put("model", uri);

        // detect model type
        if (uri.toString().toLowerCase().endsWith(".obj")) {
            askForRelatedFiles(0);
        } else if (uri.toString().toLowerCase().endsWith(".stl")) {
            askForRelatedFiles(1);
        } else if (uri.toString().toLowerCase().endsWith(".dae")) {
            askForRelatedFiles(2);
        } if (uri.toString().toLowerCase().endsWith(".gltf")) {
            askForRelatedFiles(3);
        } else {
            // no model type from filename, ask user...
            ContentUtils.showListDialog(this, "Select type", new String[]{"Wavefront (*.obj)", "Stereolithography (*" +
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
                ContentUtils.showDialog(this, "Select material file", "This model references a " +
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

                    ContentUtils.showDialog(this, "Select texture", "This model references a " +
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
        Log.i("Menu", "Launching renderer for '" + uri + "'");
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        try {
            URI.create(uri.toString());
            intent.putExtra("uri", uri.toString());
        } catch (Exception e) {
            // info: filesystem url may contain spaces, therefore we re-encode URI
            try {
                intent.putExtra("uri", new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString());
            } catch (URISyntaxException ex) {
                Toast.makeText(this, "Error: " + uri.toString(), Toast.LENGTH_LONG).show();
                return;
            }
        }
        intent.putExtra("immersiveMode", "false");

        // content provider case
        if (!loadModelParameters.isEmpty()) {
            intent.putExtra("type", loadModelParameters.get("type").toString());
            //intent.putExtra("backgroundColor", "0.25 0.25 0.25 1");
            loadModelParameters.clear();
        }

        startActivity(intent);
    }
}
