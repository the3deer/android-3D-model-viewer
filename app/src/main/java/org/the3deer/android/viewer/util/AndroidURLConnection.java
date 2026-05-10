package org.the3deer.android.viewer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class AndroidURLConnection extends URLConnection {

    private InputStream stream;

    public AndroidURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException
    {
        if (stream == null) {
            stream = ContentUtils.getInputStream(url.toString());
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();
        return stream;
    }
}
