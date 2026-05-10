package org.the3deer.android.viewer.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.logging.Logger;

public class AndroidUtils {

    private static final Logger logger = Logger.getLogger(AndroidUtils.class.getSimpleName());

    public static void logd(String sb){
        sb = sb.replaceAll("android_asset", System.getProperty("user.local.dir")+"/app/src/main/assets");
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                    logger.config(sb.substring(4000 * i));
                } else {
                    logger.config(sb.substring(4000 * i, max));
                }
            }
        } else {
            logger.config(sb);
        }
    }

    public static boolean checkPermission(Activity context, String permission, int callback) {
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
