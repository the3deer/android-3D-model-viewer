package org.the3deers.util.android;

import android.util.Log;

public final class AndroidUtils {

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

}
