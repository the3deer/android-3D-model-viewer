package org.the3deer.android.viewer.providers;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONObject;
import org.the3deer.android.engine.Model;
import org.the3deer.android.viewer.MainActivity;
import org.the3deer.android.viewer.util.ContentUtils;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provider for Poly Haven assets.
 * CC0 Assets from https://polyhaven.com
 * 
 * @author Gemini AI
 */
public class PolyHavenModelProvider implements ModelProvider {

    private static final Logger logger = Logger.getLogger(PolyHavenModelProvider.class.getSimpleName());
    private static final String API_URL = "https://api.polyhaven.com";

    @Override
    public Object list() {
        return listAssets();
    }

    @Override
    public void load(Activity activity, Callback callback) {
        new LoadAssetsTask(activity, (assetId, url, includes) -> {
            if (url != null) {
                callback.onModelSelected(buildModel(URI.create(url), url, includes)); 
            } else {
                callback.onModelSelected(null);
            }
        }).execute();
    }
    
    @Override
    public Model resolve(URI uri) {
        // If it's already an absolute URL from this provider, just extract the ID and re-resolve
        String assetId = null;
        String uriString = uri.toString();
        
        if (uri.isAbsolute() && uri.getHost() != null && uri.getHost().contains("polyhaven")) {
            String path = uri.getPath();
            if (path != null) {
                int lastSlash = path.lastIndexOf('/');
                int lastDot = path.lastIndexOf('.');
                if (lastSlash != -1 && lastDot > lastSlash) {
                    assetId = path.substring(lastSlash + 1, lastDot).replace("_1k", "").replace("_2k", "").replace("_4k", "");
                }
            }
        } else {
            // Assume it's a relative Asset ID
            assetId = uriString;
        }
        
        if (assetId == null) return null;

        ModelMetadata meta = resolveAssetMetadata(assetId);
        if (meta != null) {
            return buildModel(URI.create(meta.url), meta.url, meta.includes);
        }
        return null;
    }
    
    private Model buildModel(URI identity, String url, Map<String, String> includes) {
        Model model = new Model(identity);
        model.setUriModel(URI.create(url));
        
        // Extract name from the absolute URL
        String path = identity.getPath();
        if (path != null) {
            String fileName = path.substring(path.lastIndexOf('/') + 1);
            model.setName(fileName.substring(0, fileName.lastIndexOf('.')));
        }
        
        model.setType("gltf");
        model.setLicense("CC0");
        model.setProvider("Poly Haven");

        if (includes != null) {
            for (Map.Entry<String, String> entry : includes.entrySet()) {
                model.addUri(entry.getKey(), URI.create(entry.getValue()));
            }
        }
        return model;
    }

    public static class ModelMetadata {
        public final String url;
        public final Map<String, String> includes;
        public ModelMetadata(String url, Map<String, String> includes) {
            this.url = url;
            this.includes = includes;
        }
    }

    public interface PolyHavenCallback {
        void onModelSelected(String assetId, String url, Map<String, String> includes);
    }

    /**
     * Returns a hierarchical map of Poly Haven models.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> listAssets() {
        Map<String, Object> root = new TreeMap<>();
        try {
            URI url = URI.create(API_URL + "/assets?type=models");
            String json = ContentUtils.read(url);
            JSONObject assetsJson = new JSONObject(json);

            Iterator<String> keys = assetsJson.keys();
            while (keys.hasNext()) {
                String id = keys.next();
                JSONObject asset = assetsJson.getJSONObject(id);
                if (asset.has("categories")) {
                    org.json.JSONArray cats = asset.getJSONArray("categories");

                    Map<String, Object> current = root;
                    for (int i = 0; i < cats.length(); i++) {
                        String cat = cats.getString(i);
                        if (!current.containsKey(cat)) {
                            current.put(cat, new TreeMap<String, Object>());
                        }
                        Object next = current.get(cat);
                        if (next instanceof Map) {
                            current = (Map<String, Object>) next;
                        }
                    }
                    if (!current.containsKey("_assets")) {
                        current.put("_assets", new ArrayList<URI>());
                    }
                    Object assets = current.get("_assets");
                    if (assets instanceof List) {
                        ((List<URI>) assets).add(URI.create(id)); // IDs are relative here
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error listing Poly Haven assets", e);
        }
        return root;
    }

    /**
     * Resolves a Poly Haven asset ID to a real GLTF URL and all its dependencies.
     */
    public static ModelMetadata resolveAssetMetadata(String assetId) {
        logger.info("Resolving asset metadata... id: " + assetId);
        try {
            URI url = URI.create(API_URL + "/files/" + assetId);
            String json = ContentUtils.read(url);
            JSONObject files = new JSONObject(json);
            if (files.has("gltf")) {
                JSONObject gltfResolutions = files.getJSONObject("gltf");
                
                // Prefer 1k
                String res = gltfResolutions.has("1k") ? "1k" : (gltfResolutions.keys().hasNext() ? (String) gltfResolutions.keys().next() : null);
                if (res != null && !gltfResolutions.isNull(res)) {
                    JSONObject resObj = gltfResolutions.getJSONObject(res);
                    if (resObj.has("gltf")) {
                        JSONObject target = resObj.getJSONObject("gltf");
                        String gltfUrl = target.getString("url");
                        Map<String, String> includes = new HashMap<>();

                        if (target.has("include")) {
                            JSONObject include = target.getJSONObject("include");
                            Iterator<String> keys = include.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                includes.put(key, include.getJSONObject(key).getString("url"));
                            }
                        }

                        return new ModelMetadata(gltfUrl, includes);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching Poly Haven metadata for " + assetId, e);
        }
        return null;
    }

    /**
     * Internal representation of the asset tree
     */
    private static class Node {
        final String name;
        final Map<String, Node> children = new TreeMap<>();
        final List<String> assets = new ArrayList<>();

        Node(String name) {
            this.name = name;
        }
    }

    private static class LoadAssetsTask extends android.os.AsyncTask<Void, Void, Node> {
        private final WeakReference<Activity> activityRef;
        private final PolyHavenCallback callback;

        LoadAssetsTask(Activity activity, PolyHavenCallback callback) {
            this.activityRef = new WeakReference<>(activity);
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = activityRef.get();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).setLoading(true, "Fetching Poly Haven repository...");
            }
        }

        @Override
        protected Node doInBackground(Void... voids) {
            try {
                URI url = URI.create(API_URL + "/assets?type=models");
                String json = ContentUtils.read(url);
                JSONObject assetsJson = new JSONObject(json);

                Node root = new Node("Root");
                Iterator<String> keys = assetsJson.keys();
                while (keys.hasNext()) {
                    String id = keys.next();
                    JSONObject asset = assetsJson.getJSONObject(id);
                    if (asset.has("categories")) {
                        org.json.JSONArray cats = asset.getJSONArray("categories");
                        
                        // Build hierarchy up to 3 levels
                        Node current = root;
                        for (int i = 0; i < Math.min(cats.length(), 3); i++) {
                            String cat = cats.getString(i);
                            if (!current.children.containsKey(cat)) {
                                current.children.put(cat, new Node(cat));
                            }
                            current = current.children.get(cat);
                        }
                        current.assets.add(id);
                    }
                }
                return root;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error building asset tree", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Node root) {
            Activity activity = activityRef.get();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).setLoading(false, null);
            }
            if (activity == null) return;

            if (root == null) {
                Toast.makeText(activity, "Failed to connect to Poly Haven", Toast.LENGTH_SHORT).show();
                return;
            }

            showCategoryDialog(activity, root, callback);
        }
    }

    private static void showCategoryDialog(Activity activity, Node node, PolyHavenCallback callback) {
        List<String> options = new ArrayList<>();
        List<Runnable> actions = new ArrayList<>();

        // Add subcategories
        for (Node child : node.children.values()) {
            options.add(child.name + " >");
            actions.add(() -> showCategoryDialog(activity, child, callback));
        }

        // Add assets in this node
        for (String assetId : node.assets) {
            options.add(assetId);
            actions.add(() -> fetchDownloadUrl(activity, assetId, callback));
        }

        if (options.isEmpty()) {
            Toast.makeText(activity, "Empty category", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentUtils.showListDialog(activity, node.name.equals("Root") ? "Poly Haven Models" : node.name,
                options.toArray(new String[0]), (dialog, which) -> actions.get(which).run());
    }

    private static void fetchDownloadUrl(Activity activity, String assetId, PolyHavenCallback callback) {
        new FetchFileTask(activity, assetId, callback).execute();
    }

    private static class FetchFileTask extends android.os.AsyncTask<Void, Void, ModelMetadata> {
        private final WeakReference<Activity> activityRef;
        private final String assetId;
        private final PolyHavenCallback callback;

        FetchFileTask(Activity activity, String assetId, PolyHavenCallback callback) {
            this.activityRef = new WeakReference<>(activity);
            this.assetId = assetId;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = activityRef.get();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).setLoading(true, "Getting download links for " + assetId + "...");
            }
        }

        @Override
        protected ModelMetadata doInBackground(Void... voids) {
            return resolveAssetMetadata(assetId);
        }

        @Override
        protected void onPostExecute(ModelMetadata meta) {
            Activity activity = activityRef.get();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).setLoading(false, null);
            }
            if (meta != null) {
                callback.onModelSelected(assetId, meta.url, meta.includes);
            } else {
                if (activity != null) {
                    Toast.makeText(activity, "Could not find GLTF file for this asset", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
