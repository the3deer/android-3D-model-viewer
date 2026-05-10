package org.the3deer.android.viewer.util;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class AndroidURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("android".equals(protocol)) {
            return new org.the3deer.android.viewer.util.assets.Handler();
        } else if ("content".equals(protocol)){
            return new org.the3deer.android.viewer.util.content.Handler();
        }
        return null;
    }
}
