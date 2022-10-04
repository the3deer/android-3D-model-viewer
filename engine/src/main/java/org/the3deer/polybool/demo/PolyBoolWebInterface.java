package org.the3deer.polybool.demo;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.annotation.RequiresApi;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.the3deer.polybool.PolyBool;
import org.the3deer.util.javascript.JSList;
import org.the3deer.util.javascript.JSListDeserializer;
import org.the3deer.util.javascript.JSMap;
import org.the3deer.util.javascript.JSMapDeserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class PolyBoolWebInterface {
    Context mContext;

    /**
     * Instantiate the interface and set the context
     */
    public PolyBoolWebInterface(Context c) {
        mContext = c;
    }

    public static String toJSon(Object map) {
        GsonBuilder gsonBuilder = getGsonBuilder();
        return gsonBuilder.create().toJson(map);
    }

    public static JSMap<Object> fromJSon(String json) {
        GsonBuilder gsonBuilder = getGsonBuilder();
        return gsonBuilder.create().fromJson(json, JSMap.class);
    }

    public static GsonBuilder getGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();

        Type jsMapType = new TypeToken<JSMap>() {
        }.getType();
        Type jsListType = new TypeToken<JSList>() {
        }.getType();
        Type jsListType1 = new TypeToken<Number>() {
        }.getType();
        Type jsListType2 = new TypeToken<ArrayList>() {
        }.getType();
        Type jsListType3 = new TypeToken<Collections>() {
        }.getType();
        Type jsListType4 = new TypeToken<JsonArray>() {
        }.getType();

        gsonBuilder.registerTypeAdapter(jsMapType, new JSMapDeserializer());
        gsonBuilder.registerTypeAdapter(jsListType, new JSListDeserializer(JSMap.class));
       // gsonBuilder.registerTypeAdapter(jsListType, new JSMapDeserializer());
        /*gsonBuilder.registerTypeAdapter(jsListType2, new JSListDeserializer(JSMap.class));
        gsonBuilder.registerTypeAdapter(jsListType3, new JSListDeserializer(JSMap.class));*/
        gsonBuilder.registerTypeAdapter(jsListType4, new JSListDeserializer(JSMap.class));
        return gsonBuilder;
    }

    /**
     * Show a toast from the web page
     */
    @JavascriptInterface
    public String buildLog(boolean bl) {
        return toJSon(PolyBool.buildLog(bl));
    }

    @JavascriptInterface
    public String buildLog_get() {
        return toJSon(PolyBool.buildLog(false));
    }

    @JavascriptInterface
    public float epsilon(float v) {
        Log.d("PolyBoolWebInterface","epsion: "+PolyBool.epsilon(v));
        return PolyBool.epsilon(v);
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String segments(String poly) {
        return toJSon(PolyBool.segments(fromJSon(poly)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String intersect(String poly1, String poly2) {
        return toJSon(PolyBool.intersect(fromJSon(poly1), fromJSon(poly2)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String union(String poly1, String poly2) {
        return toJSon(PolyBool.union(fromJSon(poly1), fromJSon(poly2)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String difference(String poly1, String poly2) {
        return toJSon(PolyBool.difference(fromJSon(poly1), fromJSon(poly2)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String differenceRev(String poly1, String poly2) {
        return toJSon(PolyBool.differenceRev(fromJSon(poly1), fromJSon(poly2)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String xor(String poly1, String poly2) {
        return toJSon(PolyBool.xor(fromJSon(poly1), fromJSon(poly2)));
    }

    @JavascriptInterface
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String polygonToGeoJSON(String polygon) {
        return toJSon(PolyBool.polygonToGeoJSON(fromJSon(polygon)));
    }
}


