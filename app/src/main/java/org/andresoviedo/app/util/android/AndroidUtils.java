package org.andresoviedo.app.util.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class AndroidUtils {

    public static void openUrl(Activity activity, String url){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }
}
