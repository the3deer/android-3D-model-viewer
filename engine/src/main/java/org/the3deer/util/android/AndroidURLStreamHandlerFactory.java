package org.the3deer.util.android;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class AndroidURLStreamHandlerFactory implements URLStreamHandlerFactory {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("android".equals(protocol)) {
            return new org.the3deer.util.android.assets.Handler();
        } else if ("content".equals(protocol)){
            return new org.the3deer.util.android.content.Handler();
        }
        return null;
    }
}
