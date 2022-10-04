package org.the3deer.util.javascript;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.the3deer.util.function.QuadFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JSMap<V> extends TreeMap<String, V> {

    public static <V> JSMap<V> of(String key, V val) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key, val);
        return ret;
    }

    public static <V> JSMap<V> of(String key, V val, String key2, V val2) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key, val);
        ret.put(key2, val2);
        return ret;
    }

    public static <V> JSMap<V> of(String key, V val, String key2, V val2, String key3, V val3) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key, val);
        ret.put(key2, val2);
        ret.put(key3, val3);
        return ret;
    }

    public static <V> JSMap<V> of(String key, V val, String key2, V val2, String key3, V val3, String key4, V val4) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key, val);
        ret.put(key2, val2);
        ret.put(key3, val3);
        ret.put(key4, val4);
        return ret;
    }

    public static <V> JSMap<V> of(String key, V val, String key2, V val2, String key3, V val3, String key4, V val4, String key5, V val5) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key, val);
        ret.put(key2, val2);
        ret.put(key3, val3);
        ret.put(key4, val4);
        ret.put(key5, val5);
        return ret;
    }

    public static <V> JSMap of(String key1, V val1, String key2, V val2, String key3, V val3, String key4, V val4, String key5, V val5, String key6, V val6) {
        final JSMap<V> ret = new JSMap<V>();
        ret.put(key1, val1);
        ret.put(key2, val2);
        ret.put(key3, val3);
        ret.put(key4, val4);
        ret.put(key5, val5);
        ret.put(key6, val6);
        return ret;

    }

    public JSMap<V> p(String key, V val) {
        super.put(key, val);
        return this;
    }

    public <V2> JSMap<V> pr(String key, V2 val) {
        List<String> keys = new ArrayList<>(Arrays.asList(key.split("\\.")));
        JSMap cur = this;
        while (keys.size() > 1) {
            final String token = keys.remove(0);
            if (cur.c(token)) {
                cur = cur.gm(token);
            } else {
                cur.put(token, new JSMap<>());
                cur = (JSMap) cur.get(token);
            }
        }
        cur.put(keys.remove(0), val);
        return this;
    }

    public <V2> JSMap<V2> gm(String key) {
        return (JSMap<V2>) super.get(key);
    }

    public <V2> JSMap<V2> grm(String key) {
        return gr(key);
    }

    public <T> T gr(String key) {
        if (!key.contains(".")){
            return (T) super.get(key);
        }
        List<String> keys = new ArrayList<>(Arrays.asList(key.split("\\.")));
        JSMap cur = this;
        T ret = null;
        while (keys.size() > 1) {
            cur = cur.gm(keys.remove(0));
            if (cur == null) return null;
        }
        return (T) cur.get(keys.remove(0));
    }

    public Boolean gb(String key) {
        List<String> keys = new ArrayList<>(Arrays.asList(key.split("\\.")));
        JSMap cur = this;
        while (keys.size() > 1) {
            cur = cur.gm(keys.remove(0));
            if (cur == null) return false;
        }
        String key2 = keys.remove(0);
        return cur.gb_(key2);
    }

    private Boolean gb_(String key) {
        if (super.get(key) == null) return Boolean.FALSE;
        if (super.get(key) instanceof Boolean) {
            return (Boolean) super.get(key);
        } else {
            return Boolean.TRUE;
        }
    }

    public Integer gi(String key) {
        return (Integer) super.get(key);
    }

    public <E> JSList gl(String key) {
        List<E> list = (List<E>) super.get(key);
        return (JSList<E>) list;
    }

    public V next() {
        return super.get("next");
    }

    public JSMap nextm() {
        return (JSMap) next();
    }

    public float[] gfa(String key) {
        return (float[]) super.get(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T, R> Function<T, R> gfn(String key) {
        return (Function<T, R>) super.get(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Runnable grn(String key) {
        return (Runnable) super.get(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public <T, U, R> BiFunction<T, U, R> gbfn(String key) {
        return (BiFunction<T, U, R>) super.get(key);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public <A1, A2, A3, A4, R> QuadFunction<A1, A2, A3, A4, R> gqfn(String key) {
        return (QuadFunction<A1, A2, A3, A4, R>) super.get(key);
    }

    public boolean c(String key) {
        return super.get(key) != null;
    }

    protected boolean nc(String key) {
        return !c(key);
    }

    @Nullable
    @Override
    public V put(String key, V value) {
        return super.put(key, value);
    }

    public String gs(String key) {
        return (String)super.get(key);
    }
}
