package org.the3deer.polybool.demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import org.andresoviedo.dddmodel2.R;
import org.the3deer.util.android.WebAppInterface;


public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // INFO: in Android there is no user.dir available - Check AndroidUtils.logd(String)
        // This should be set with your own local working directory, in order for logcat links to work on the IDE (Intellij)
        System.setProperty("user.local.dir","/home/andres/Documents/android-polybool-demo");

        // create the html view
        final WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.setVisibility(View.GONE);
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");         // standard interface  ( log(string), toast(string) )
        myWebView.addJavascriptInterface(new PolyBoolWebInterface(this), "PolyBool");   // PolyBool interface
        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        //myWebView.loadUrl("https://rawgit.com/voidqk/polybooljs/main/dist/demo.html");        // Original PolyBool code (web) - Demo 100% Web
        //myWebView.loadUrl("file:///android_asset/polybool/index.html");                         // Original PolyBool code - PolyBool 100% JavaScript
        myWebView.loadUrl("file:///android_asset/polybool/index_java.html");                      // This project - PolyBool library 100% Java :) - the demo is still 100% from the original author

        /*Toolbar toolbar = findViewById(id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

