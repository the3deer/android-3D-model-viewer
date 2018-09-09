package org.andresoviedo.app.util.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ContentUtils {

    /**
     * Documents opened by the user. This list helps finding the relative filenames found in the model
     */
    private static Map<String,Uri> documentsProvided = new HashMap<>();

    private static ThreadLocal<Activity> currentActivity = new ThreadLocal<>();

    private static File currentDir = null;

    public static void printTouchCapabilities(PackageManager packageManager) {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
            Log.i("utils", "System supports multitouch (2 fingers)");
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
            Log.i("utils", "System supports advanced multitouch (multiple fingers)");
        }
    }

    public static void setThreadActivity(Activity currentActivity) {
        Log.i("ContentUtils","Current activity thread: "+Thread.currentThread().getName());
        ContentUtils.currentActivity.set(currentActivity);
    }

    private static Activity getCurrentActivity(){
        return ContentUtils.currentActivity.get();
    }

    public static void setCurrentDir(File file){
        ContentUtils.currentDir = file;
    }

    public static void clearDocumentsProvided(){
        // clear documents provided by user (kitkat only)
        documentsProvided.clear();
    }

    public static void provideAssets(Activity activity) {
        documentsProvided.clear();
        try {
            for (String document : activity.getAssets().list("models")) {
                documentsProvided.put(document, Uri.parse("assets://assets/models/" + document));
            }
        }catch(IOException ex){
            Log.e("ContentUtils","Error listing assets from models folder", ex);
        }
    }

    public static void addUri(String name, Uri uri){
        documentsProvided.put(name, uri);
        Log.i("ContentUtils","Added ("+name+") "+uri);
    }

    public static Uri getUri(String name){
        return documentsProvided.get(name);
    }

    /**
     * Find the relative file that should be already selected by the user
     * @param path relative file
     * @return InputStream of the file
     * @throws IOException if there is an error opening stream
     */
    public static InputStream getInputStream(String path) throws IOException {
        Uri uri = getUri(path);
        if (uri == null && currentDir != null){
            uri = Uri.parse("file://"+new File(currentDir, path).getAbsolutePath());
        }
        if (uri != null){
            return getInputStream(uri);
        }
        return null;
    }

    public static InputStream getInputStream(URI uri) throws IOException {
        return getInputStream(Uri.parse(uri.toString()));
    }

    public static InputStream getInputStream(Uri uri) throws IOException {
        Log.i("ContentUtils","Opening stream "+uri.getPath());
        if (uri.getScheme().equals("assets")){
            Log.i("ContentUtils","Opening asset: "+uri.getPath());
            return getCurrentActivity().getAssets().open(uri.getPath().substring(1));
        }
        return getCurrentActivity().getContentResolver().openInputStream(uri);
    }



	public static Intent createGetContentIntent(String mimeType) {
		// check here to KITKAT or new version
		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		if (isKitKat){
			return createGetMultipleContentIntent(mimeType);
		}
		return createGetSingleContentIntent(mimeType);
	}

	/**
	 * Get the Intent for selecting content to be used in an Intent Chooser.
	 *
	 * @return The intent for opening a file with Intent.createChooser()
	 *
	 * @author paulburke
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static Intent createGetMultipleContentIntent(String mimeType) {
		// Implicitly allow the user to select a particular kind of data
		final Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		// The MIME data type filter
		intent.setType(mimeType);
		// EXTRA_ALLOW_MULTIPLE: added in API level 18
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		return intent;
	}

	/**
	 * Get the Intent for selecting content to be used in an Intent Chooser.
	 *
	 * @return The intent for opening a file with Intent.createChooser()
	 *
	 * @author paulburke
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static Intent createGetSingleContentIntent(String mimeType) {
		// Implicitly allow the user to select a particular kind of data
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// The MIME data type filter
		intent.setType(mimeType);
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		return intent;
	}

    public static void showDialog(Activity activity, String title, CharSequence message, String positiveButtonLabel,
                                  String negativeButtonLabel, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonLabel, listener);
        builder.setNegativeButton(negativeButtonLabel, listener);
        builder.create().show();
    }

    public static void showListDialog(Activity activity, String title, String[] options, DialogInterface
            .OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setItems(options, listener);
        builder.create().show();
    }
}