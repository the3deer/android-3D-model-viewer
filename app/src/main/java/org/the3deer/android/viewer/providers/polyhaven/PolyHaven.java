package org.the3deer.android.viewer.providers.polyhaven;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import org.json.JSONObject;
import org.the3deer.engine.android.util.ContentUtils;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
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
public class PolyHaven {

    private static final Logger logger = Logger.getLogger(PolyHaven.class.getSimpleName());
    private static final String API_URL = "https://api.polyhaven.com";

    public interface PolyHavenCallback {
        void onModelSelected(String url);
    }

    public static void load(Activity activity, PolyHavenCallback callback) {
        new LoadAssetsTask(activity, callback).execute();
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
        private ProgressDialog dialog;

        LoadAssetsTask(Activity activity, PolyHavenCallback callback) {
            this.activityRef = new WeakReference<>(activity);
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = activityRef.get();
            if (activity != null) {
                dialog = new ProgressDialog(activity);
                dialog.setMessage("Fetching Poly Haven repository...");
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        @Override
        protected Node doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL + "/assets?type=models");
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
            if (dialog != null) dialog.dismiss();
            Activity activity = activityRef.get();
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

    private static class FetchFileTask extends android.os.AsyncTask<Void, Void, String> {
        private final WeakReference<Activity> activityRef;
        private final String assetId;
        private final PolyHavenCallback callback;
        private ProgressDialog dialog;

        FetchFileTask(Activity activity, String assetId, PolyHavenCallback callback) {
            this.activityRef = new WeakReference<>(activity);
            this.assetId = assetId;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            Activity activity = activityRef.get();
            if (activity != null) {
                dialog = new ProgressDialog(activity);
                dialog.setMessage("Getting download links for " + assetId + "...");
                dialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(API_URL + "/files/" + assetId);
                String json = ContentUtils.read(url);
                JSONObject files = new JSONObject(json);
                
                // Poly Haven structure: gltf -> resolution -> [gltf, bin, textures]
                // We pick 1k resolution as it's mobile friendly
                if (files.has("gltf")) {
                    JSONObject gltfObj = files.getJSONObject("gltf");
                    String res = gltfObj.has("1k") ? "1k" : (String) gltfObj.keys().next();
                    JSONObject bestRes = gltfObj.getJSONObject(res);
                    if (bestRes.has("gltf")) {
                        return bestRes.getJSONObject("gltf").getString("url");
                    }
                }
                return null;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error fetching file URL", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String url) {
            if (dialog != null) dialog.dismiss();
            if (url != null) {
                callback.onModelSelected(url);
            } else {
                Activity activity = activityRef.get();
                if (activity != null) {
                    Toast.makeText(activity, "Could not find GLTF file for this asset", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
