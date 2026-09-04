package org.the3deer.android.viewer.util.content;

import org.the3deer.android.viewer.util.AndroidURLConnection;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *  Android's content URL handler
 */
public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(final URL url) {
        return new AndroidURLConnection(url);
    }

}
