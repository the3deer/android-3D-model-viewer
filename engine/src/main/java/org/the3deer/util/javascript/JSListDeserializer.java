package org.the3deer.util.javascript;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.List;

public class JSListDeserializer<T> implements JsonDeserializer<List<T>>/*, JsonSerializer<JSList>*/ {
    Class<T[]> tClass;

    public JSListDeserializer(Class<T[]> tClass) {
        this.tClass = tClass;
    }

    @Override
    public List<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        final JSList ret = new JSList();
        final JsonArray jsonObj = json.getAsJsonArray();
        for (int i = 0; i < jsonObj.size(); i++) {
            JsonElement jsonEl = jsonObj.get(i);
            if (jsonEl.isJsonArray() && jsonEl.getAsJsonArray().size() > 0 && !jsonEl.getAsJsonArray().get(0).isJsonPrimitive()) {
                ret.add(ctx.deserialize(jsonEl, JSList.class));
            } else if (jsonEl.isJsonArray() && jsonEl.getAsJsonArray().size() > 0 && jsonEl.getAsJsonArray().get(0).isJsonPrimitive()) {
                JsonPrimitive asJsonPrimitive = jsonEl.getAsJsonArray().get(0).getAsJsonPrimitive();
                // Log.d("JSListDeserializer", "primitive: " + asJsonPrimitive+", "+asJsonPrimitive.get);
                // ret.add(ctx.deserialize(jsonEl, Array.newInstance(float[].class, 1).getClass()));
                ret.add(ctx.deserialize(jsonEl, float[].class));
            } else if (jsonEl.isJsonObject()) {
                ret.add(ctx.deserialize(jsonEl, JSMap.class));
            } else {
                ret.add(ctx.deserialize(jsonEl, jsonEl.getClass()));
            }
        }

        return ret;
    }

/*    @Override
    public JsonElement serialize(JSList src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject ret = new JsonObject();
        Set<Map.Entry<String,Object>> s = src.entrySet();
        for (Map.Entry<String,Object> entry : s){
            if (entry.getValue() instanceof JSMap || entry.getValue() instanceof  JSList){
                ret.add(entry.getKey(), ctx.serialize(entry.getValue()));
            } else if (entry.getValue() == null){
                //ret.add(entry.getKey(), ctx.);
            } else if (entry.getValue() instanceof Boolean){
                ret.add(entry.getKey(), new JsonPrimitive((Boolean) entry.getValue()));
            } else if (entry.getValue() instanceof Number){
                ret.add(entry.getKey(), new JsonPrimitive((Number) entry.getValue()));
            } else if (entry.getValue() instanceof String){
                ret.add(entry.getKey(), new JsonPrimitive((String) entry.getValue()));
            } else if (entry.getValue() instanceof Character){
                ret.add(entry.getKey(), new JsonPrimitive((Character)entry.getValue()));
            }
        }

        return ret;
    }*/

}
