
package org.the3deer.util.android;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public final class WebAppInterface {
    private Context mContext;

    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void log(String txt) {
        AndroidUtils.logd(txt);
    }

    @JavascriptInterface
    public void logd(String txt) {
        AndroidUtils.logd(txt);
    }

    @JavascriptInterface
    public void toast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
    }
}

