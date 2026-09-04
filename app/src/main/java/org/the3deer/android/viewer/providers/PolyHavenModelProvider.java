package org.the3deer.android.viewer.providers;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONObject;
import org.the3deer.android.engine.Model;
import org.the3deer.android.viewer.MainActivity;
import org.the3deer.android.viewer.util.ContentUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provider for Poly Haven assets.
 * CC0 Assets from https://polyhaven.com
 * 
 * @author Gemini AI
 */
class PolyHavenModelProvider implements ModelProvider {

    private static final Logger logger = Logger.getLogger(PolyHavenModelProvider.class.getSimpleName());
    private static final String API_URL = "https://api.polyhaven.com";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public Object list() {
        return listAssets();
    }

    @Override
    public void load(final Activity activity, final Callback callback) {
        loadAssets(activity, (assetId, url, includes) -> {
            if (url != null) {
                callback.onModelSelected(buildModel(URI.create(url), includes)); 
            } else {
                callback.onModelSelected(null);
            }
        });
    }

    private static void loadAssets(final Activity activity, final PolyHavenCallback callback) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setLoading(true, "Fetching Poly Haven repository...");
        }

        // Execute background fetch on single thread executor replacing deprecated AsyncTask
        executor.execute(() -> {
            final Node root = buildAssetTree();
            activity.runOnUiThread(() -> {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).setLoading(false, null);
                }
                if (root == null) {
                    Toast.makeText(activity, "Failed to connect to Poly Haven", Toast.LENGTH_SHORT).show();
                    return;
                }
                showCategoryDialog(activity, root, callback);
            });
        });
    }
    
    @Override
    public Model resolve(final URI uri) {
        // If it's already an absolute URL from this provider, just extract the ID and re-resolve
        String assetId = null;
        final String uriString = uri.toString();
        
        if (uri.isAbsolute() && uri.getHost() != null && uri.getHost().contains("polyhaven")) {
            final String path = uri.getPath();
            if (path != null) {
                final int lastSlash = path.lastIndexOf('/');
                final int lastDot = path.lastIndexOf('.');
                if (lastSlash != -1 && lastDot > lastSlash) {
                    assetId = path.substring(lastSlash + 1, lastDot).replace("_1k", "").replace("_2k", "").replace("_4k", "");
                }
            }
        } else {
            // Assume it's a relative Asset ID
            assetId = uriString;
        }
        
        if (assetId == null) return null;

        final ModelMetadata meta = resolveAssetMetadata(assetId);
        if (meta != null) {
            return buildModel(URI.create(meta.url), meta.includes);
        }
        return null;
    }
    
    private Model buildModel(final URI identity, final Map<String, String> includes) {
        final Model model = new Model(identity);
        
        // Extract name from the absolute URL
        final String path = identity.getPath();
        if (path != null) {
            final String fileName = path.substring(path.lastIndexOf('/') + 1);
            model.setName(fileName.substring(0, fileName.lastIndexOf('.')));
        }
        
        model.setType("gltf");
        model.setLicense("CC0");
        model.setAuthor("Poly Haven Community");
        model.setProvider("Poly Haven");

        if (includes != null) {
            for (final Map.Entry<String, String> entry : includes.entrySet()) {
                model.addUri(entry.getKey(), URI.create(entry.getValue()));
            }
        }
        return model;
    }

    public static class ModelMetadata {
        public final String url;
        public final Map<String, String> includes;
        public ModelMetadata(final String url, final Map<String, String> includes) {
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
        final Map<String, Object> root = new TreeMap<>();
        try {
            final URI url = URI.create(API_URL + "/assets?type=models");
            final String json = ContentUtils.read(url);
            final JSONObject assetsJson = new JSONObject(json);

            final Iterator<String> keys = assetsJson.keys();
            while (keys.hasNext()) {
                final String id = keys.next();
                final JSONObject asset = assetsJson.getJSONObject(id);
                if (asset.has("categories")) {
                    final org.json.JSONArray cats = asset.getJSONArray("categories");

                    Map<String, Object> current = root;
                    for (int i = 0; i < cats.length(); i++) {
                        final String cat = cats.getString(i);
                        if (!current.containsKey(cat)) {
                            current.put(cat, new TreeMap<String, Object>());
                        }
                        final Object next = current.get(cat);
                        if (next instanceof Map) {
                            current = (Map<String, Object>) next;
                        }
                    }
                    if (!current.containsKey("_assets")) {
                        current.put("_assets", new ArrayList<URI>());
                    }
                    final Object assets = current.get("_assets");
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
    public static ModelMetadata resolveAssetMetadata(final String assetId) {
        logger.info("Resolving asset metadata... id: " + assetId);
        try {
            final URI url = URI.create(API_URL + "/files/" + assetId);
            final String json = ContentUtils.read(url);
            final JSONObject files = new JSONObject(json);
            if (files.has("gltf")) {
                final JSONObject gltfResolutions = files.getJSONObject("gltf");
                
                // Prefer 1k
                final String res = gltfResolutions.has("1k") ? "1k" : (gltfResolutions.keys().hasNext() ? (String) gltfResolutions.keys().next() : null);
                if (res != null && !gltfResolutions.isNull(res)) {
                    final JSONObject resObj = gltfResolutions.getJSONObject(res);
                    if (resObj.has("gltf")) {
                        final JSONObject target = resObj.getJSONObject("gltf");
                        final String gltfUrl = target.getString("url");
                        final Map<String, String> includes = new HashMap<>();

                        if (target.has("include")) {
                            final JSONObject include = target.getJSONObject("include");
                            final Iterator<String> keys = include.keys();
                            while (keys.hasNext()) {
                                final String key = keys.next();
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

        Node(final String name) {
            this.name = name;
        }
    }

    private static Node buildAssetTree() {
        try {
            final URI url = URI.create(API_URL + "/assets?type=models");
            final String json = ContentUtils.read(url);
            final JSONObject assetsJson = new JSONObject(json);

            final Node root = new Node("Root");
            final Iterator<String> keys = assetsJson.keys();
            while (keys.hasNext()) {
                final String id = keys.next();
                final JSONObject asset = assetsJson.getJSONObject(id);
                if (asset.has("categories")) {
                    final org.json.JSONArray cats = asset.getJSONArray("categories");
                    
                    // Build hierarchy up to 3 levels
                    Node current = root;
                    for (int i = 0; i < Math.min(cats.length(), 3); i++) {
                        final String cat = cats.getString(i);
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

    private static void showCategoryDialog(final Activity activity, final Node node, final PolyHavenCallback callback) {
        final List<String> options = new ArrayList<>();
        final List<Runnable> actions = new ArrayList<>();

        // Add subcategories
        for (final Node child : node.children.values()) {
            options.add(child.name + " >");
            actions.add(() -> showCategoryDialog(activity, child, callback));
        }

        // Add assets in this node
        for (final String assetId : node.assets) {
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

    private static void fetchDownloadUrl(final Activity activity, final String assetId, final PolyHavenCallback callback) {
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setLoading(true, "Getting download links for " + assetId + "...");
        }

        // Execute background fetch on single thread executor replacing deprecated AsyncTask
        executor.execute(() -> {
            final ModelMetadata meta = resolveAssetMetadata(assetId);
            activity.runOnUiThread(() -> {
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).setLoading(false, null);
                }
                if (meta != null) {
                    callback.onModelSelected(assetId, meta.url, meta.includes);
                } else {
                    Toast.makeText(activity, "Could not find GLTF file for this asset", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
