package org.andresoviedo.util.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;

import org.andresoviedo.util.event.EventListener;

import java.io.File;
import java.util.EventObject;
import java.util.List;

public class AndroidUtils {

    @FunctionalInterface
    public interface Callback {
        void onClick(File file);
    }

    public static void fireEvent(List<EventListener> listeners, EventObject eventObject){
        for (int i=0; i<listeners.size(); i++) listeners.get(i).onEvent(eventObject);
    }

    public static boolean supportsMultiTouch(PackageManager packageManager) {
        boolean ret = false;
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
            ret = true;
            Log.i("utils", "System supports multitouch (2 fingers)");
        }
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
            Log.i("utils", "System supports advanced multitouch (multiple fingers)");
            ret = true;
        }
        return ret;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(Activity context, String permission, int callback) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        if (checkPermission(context, permission)) {
            return true;
        }
        requestPermission(context, permission, callback);
        return false;
    }

    public static boolean checkPermission(Activity context, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity context, String permission, int callback) {
        ActivityCompat.requestPermissions(context, new String[]{permission}, callback);
    }

    public static void openUrl(Activity activity, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }
}
