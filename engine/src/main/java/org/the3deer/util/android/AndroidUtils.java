package org.the3deer.util.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.the3deer.util.event.EventListener;

import java.io.File;
import java.util.EventObject;
import java.util.List;


public class AndroidUtils {

    @FunctionalInterface
    public interface Callback {
        void onClick(File file);
    }

    public static void logd(String sb){
        sb = sb.replaceAll("android_asset", System.getProperty("user.local.dir")+"/app/src/main/assets");
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    Log.d("AndroidUtils", sb.substring(4000 * i));
                } else {
                    Log.d("AndroidUtils", sb.substring(4000 * i, max));
                }
            }
        } else {
            Log.d("AndroidUtils", sb);
        }
    }

    public static void fireEvent(List<EventListener> listeners, EventObject eventObject){
        for (int i=0; i<listeners.size(); i++) {
            if(listeners.get(i).onEvent(eventObject)){
                break;
            }
        }
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