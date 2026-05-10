package org.the3deer.android.viewer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;

import org.the3deer.util.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import dalvik.system.ZipPathValidator;

public class ContentUtils {

    private static final Logger logger = Logger.getLogger(ContentUtils.class.getSimpleName());

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ZipPathValidator.clearCallback();
        }
    }

    public interface ContentResolver {
        URI resolveUri(URI uri);
    }

    public static final String MODELS_FOLDER = "models";
    public static final Map<String, URI> documentsProvided = new HashMap<>();
    private static final Map<URI, byte[]> binariesProvided = new HashMap<>();

    private static Context context = null;
    private static File currentDir = null;
    private static ContentResolver contentResolver = null;

    public static void setContext(@NonNull Context context) {
        // register
        ContentUtils.context = context;
    }

    public static void setContentResolver(ContentResolver contentResolver) {
        ContentUtils.contentResolver = contentResolver;
    }

    public static void setCurrentDir(File file) {
        ContentUtils.currentDir = file;
    }

    public static void clearDocumentsProvided() {
        documentsProvided.clear();
        binariesProvided.clear();
    }

    public static void provideAssets(Activity activity) {
        documentsProvided.clear();
        provideAssets(activity, MODELS_FOLDER);
    }

    private static void provideAssets(Activity activity, String directory) {
        try {
            final String[] files = activity.getAssets().list(directory);
            if (files != null && files.length > 0) {
                for (String document : files) {
                    String assetNameSanitized = document.replaceAll(" ", "+");
                    final String[] files2 = activity.getAssets().list(directory + "/" + assetNameSanitized);
                    if (files2 == null) continue;
                    if (files2.length == 0) {
                        documentsProvided.put(document, URI.create("android://" + activity.getPackageName() + "/assets/models/" + assetNameSanitized));
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void addUri(String name, URI uri) {
        documentsProvided.put(name, uri);
        logger.info("File added: " + name+", uri: "+uri);
    }

    public static void addData(URI uri, byte[] data) {
        binariesProvided.put(uri, data);
        logger.info("Binary added: " + uri);
    }

    public static byte[] getData(URI uri) {
        return binariesProvided.get(uri);
    }

    public static InputStream getInputStream(URI uri) throws IOException {
        if (uri == null) throw new IllegalArgumentException("uri cannot be null");

        if (context == null){
            throw new IllegalStateException("There is no context configured. Did you call #setContext() before?");
        }
        final byte[] data = getData(uri);
        if (data != null){
            logger.info("Returning binary: " + uri);
            return new ByteArrayInputStream(data);
        }

        logger.finest("Opening stream ..." + uri);
        if (uri.getScheme() != null && uri.getScheme().equals("android")) {
            if (uri.getPath().startsWith("/binary/")) {
                final String path = uri.getPath().substring("/binary/".length());
                final URI pathUri = uri.resolve(path.replace(" ","+").replace("%20", "+"));
                byte[] buf = binariesProvided.get(pathUri);
                if (buf != null) return new ByteArrayInputStream(buf, 0, buf.length);
                else throw new FileNotFoundException("File not found: " + pathUri);
            }
            else if (uri.getPath().startsWith("/assets/")) {
                final String path = uri.getPath().substring("/assets/".length());
                logger.config("Opening asset: " + path);
                return context.getAssets().open(path);
            } else if (uri.getPath().startsWith("/res/drawable/")){
                final String path = uri.getPath().substring("/res/drawable/".length()).replace(".png","");
                logger.config("Opening drawable: " + path);
                final int resourceId = context.getResources()
                        .getIdentifier(path, "drawable", context.getPackageName());
                return context.getResources().openRawResource(resourceId);
            } else {
                throw new IllegalArgumentException("unknown android path: "+uri.getPath());
            }
        }
        else if (uri.getScheme() != null && (uri.getScheme().equals("http") || uri.getScheme().equals("https"))) {
            return new BufferedInputStream(uri.toURL().openConnection().getInputStream(),8192);
        }

        // Handle content:// or file://
        try {
            Uri androidUri = Uri.parse(uri.toString());
            final URI uriProvided = documentsProvided.get(uri.toString());
            if (uri.getScheme().equals("content") && uriProvided != null) {
                androidUri = Uri.parse(uriProvided.toString());
            }
            return new BufferedInputStream(context.getContentResolver().openInputStream(androidUri), 8192);
        } catch (FileNotFoundException | SecurityException e) {
            logger.warning("Access denied or file not found for " + uri + ". Attempting resolution...");

            // If we have a resolver and we are not on the main thread, we can try to resolve it
            if (contentResolver != null && Looper.myLooper() != Looper.getMainLooper()) {
                final CountDownLatch latch = new CountDownLatch(1);
                final AtomicReference<URI> resolvedUri = new AtomicReference<>();

                // Ask the resolver to find the file
                // This usually triggers a UI prompt, so it must return via a callback or similar mechanism
                // For simplicity, we assume resolveUri might block or trigger an async operation
                new Thread(() -> {
                    try {
                        resolvedUri.set(contentResolver.resolveUri(uri));
                    } finally {
                        latch.countDown();
                    }
                }).start();

                try {
                    if (latch.await(60, TimeUnit.SECONDS) && resolvedUri.get() != null) {
                        logger.info("Successfully resolved URI: " + resolvedUri.get());
                        // Cache the resolution for next time
                        addUri(uri.toString(), URI.create(resolvedUri.get().toString()));
                        // Retry opening the stream with the new URI
                        return new BufferedInputStream(context.getContentResolver().openInputStream(Uri.parse(resolvedUri.get().toString())),8192);
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting for URI resolution", ie);
                }
            }

            throw new IOException("Failed to open " + uri, e);
        }
    }

    public static List<String> readLines(String uriString) {
        return IOUtils.readLines(uriString);
    }

    public static String read(URL url) throws IOException {
        return IOUtils.read(url);
    }

    public static String read(URI uri) throws IOException {
        return IOUtils.read(uri);
    }

    public static long getAvailableMemory() {
        return IOUtils.getAvailableMemory();
    }

    /**
     * Extract a zip file to the application internal cache directory
     * @param uri zip file uri
     * @return map of entry name to file uri
     * @throws IOException if extraction fails
     */
    public static Map<String, URI> extract(URI uri) throws IOException {
        final Map<String, URI> ret = new HashMap<>();
        if (context == null) throw new IllegalStateException("Context not set");

        // Create a unique cache directory for this zip
        final String fileName = getFileName(context, uri);
        final String folderName = fileName != null ? fileName.replace(".", "_") : "temp_zip_" + System.currentTimeMillis();
        final File cacheDir = new File(context.getCacheDir(), "models/" + folderName);
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new IOException("Failed to create cache directory: " + cacheDir);
        }

        try (InputStream is = getInputStream(uri)) {
             IOUtils.extract(is, cacheDir);
             // We need to populate the map because the Engine used to rely on it.
             // Actually, I'll just return it for now as it's used in the App layer too.
             File[] files = cacheDir.listFiles();
             if (files != null) {
                 scanFiles(cacheDir, cacheDir, ret);
             }
        }
        return ret;
    }

    private static void scanFiles(File root, File dir, Map<String, URI> result) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanFiles(root, file, result);
                } else {
                    String relativePath = root.toURI().relativize(file.toURI()).getPath();
                    result.put(relativePath, file.toURI());
                }
            }
        }
    }

    public static List<URI> listAssets(Context context, String path) {
        List<URI> uris = new ArrayList<>();
        try {
            String[] list = context.getAssets().list(path);
            if (list != null) {
                for (String file : list) {
                    uris.add(URI.create("android://" + context.getPackageName() + "/assets/" + path + "/" + file));
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error listing assets at " + path, e);
        }
        return uris;
    }

    public static void showDialog(Context context, String title, String message, String pos, String neg, DialogInterface.OnClickListener listener) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() -> {
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(pos, listener)
                        .setNegativeButton(neg, listener)
                        .show();
            });
        }
    }

    public static void showListDialog(Context context, String title, String[] items, DialogInterface.OnClickListener listener) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(() -> {
                new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setItems(items, listener)
                        .show();
            });
        }
    }

    @SuppressLint("Range")
    public static String getFileName(Context context, URI uri) throws IOException {
        if (uri == null) return null;
        String result = null;
        final Uri androidUri = Uri.parse(uri.toString());
        if ("content".equals(androidUri.getScheme()) && context != null) {
            try (Cursor cursor = context.getContentResolver().query(androidUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (SecurityException ex){
                logger.warning("Access denied or file not found for " + androidUri + ". Attempting resolution...");

                // If we have a resolver and we are not on the main thread, we can try to resolve it
                if (contentResolver != null && Looper.myLooper() != Looper.getMainLooper()) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    final AtomicReference<URI> resolvedUri = new AtomicReference<>();

                    // Ask the resolver to find the file
                    new Thread(() -> {
                        try {
                            resolvedUri.set(contentResolver.resolveUri(uri));
                        } finally {
                            latch.countDown();
                        }
                    }).start();

                    try {
                        if (latch.await(60, TimeUnit.SECONDS) && resolvedUri.get() != null) {
                            logger.info("Successfully resolved URI: " + resolvedUri.get());
                            // Cache the resolution for next time
                            addUri(uri.toString(), URI.create(resolvedUri.get().toString()));
                            // Retry opening the stream with the new URI
                            try (Cursor cursor = context.getContentResolver().query(Uri.parse(resolvedUri.get().toString()), null, null, null, null)) {
                                if (cursor != null && cursor.moveToFirst()) {
                                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    if (index != -1) {
                                        result = cursor.getString(index);
                                    }
                                }
                            }
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while waiting for URI resolution", ie);
                    }
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error querying filename for " + androidUri, e);
            }
        }
        if (result == null) {
            result = androidUri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }


    public static InputStream getInputStream(String uriString) throws IOException {
        uriString = uriString.replace('\\','/');
        URI uri = getUri(uriString);
        if (uri == null) uri = getUri("models/"+uriString);
        if (uri == null) uri = getUri("models/"+uriString.replace(' ', '+'));
        if (uri == null && currentDir != null) {
            File relativeFile = new File(currentDir, uriString);
            if (relativeFile.exists()) {
                uri = relativeFile.toURI();
            }
        }
        if (uri != null) return getInputStream(uri);
        
        // try direct URI
        try {
            uri = URI.create(uriString);
            if (uri.getScheme() != null) {
                return getInputStream(uri);
            }
        } catch (Exception e) {
            // not a valid URI, ignore
        }

        // If we have a resolver and we are not on the main thread, we can try to resolve it
        if (contentResolver != null && Looper.myLooper() != Looper.getMainLooper()) {
            final CountDownLatch latch = new CountDownLatch(1);
            final AtomicReference<URI> resolvedUri = new AtomicReference<>();

            // Ask the resolver to find the file
            final URI finalUri = uri;
            new Thread(() -> {
                try {
                    resolvedUri.set(contentResolver.resolveUri(finalUri));
                } finally {
                    latch.countDown();
                }
            }).start();

            try {
                if (latch.await(60, TimeUnit.SECONDS) && resolvedUri.get() != null) {
                    logger.info("Successfully resolved URI: " + resolvedUri.get());
                    addUri(finalUri.toString(), URI.create(resolvedUri.get().toString()));
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for URI resolution", ie);
            }
        }
        logger.log(Level.SEVERE, "File not found: " + uriString);
        throw new FileNotFoundException("File not found: " + uriString);
    }

    public static URI getUri(String name) {
        URI uri = documentsProvided.get(name);
        if (uri != null) return uri;
        return documentsProvided.get(name.replaceAll("\\\\","/"));
    }
}
