package org.the3deer.util.javascript;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class JSMapDeserializer implements JsonDeserializer<JSMap>, JsonSerializer<JSMap> {

    public JSMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx) {
        final JSMap ret = new JSMap<>();
        final JsonObject jsonObj = json.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
            if (entry.getValue().isJsonArray()) {
                ret.put(entry.getKey(), ctx.deserialize(entry.getValue(), JSList.class));
            } else if (entry.getValue().isJsonObject()) {
                ret.put(entry.getKey(), ctx.deserialize(entry.getValue(), JSMap.class));
            } else if (entry.getValue().isJsonPrimitive()){
                JsonPrimitive asJsonPrimitive = entry.getValue().getAsJsonPrimitive();
                if (asJsonPrimitive.isBoolean()){
                    ret.put(entry.getKey(), asJsonPrimitive.getAsBoolean());
                } else if (asJsonPrimitive.isString()){
                    ret.put(entry.getKey(), asJsonPrimitive.getAsString());
                } else if (asJsonPrimitive.isNumber()) {
                    ret.put(entry.getKey(), asJsonPrimitive.getAsNumber());
                }
            } else {
                ret.put(entry.getKey(), ctx.deserialize(entry.getValue(), entry.getValue().getClass()));
            }
        }


        return ret;
    }


    @Override
    public JsonElement serialize(JSMap src, Type typeOfSrc, JsonSerializationContext ctx) {
        JsonObject ret = new JsonObject();
        Set<Map.Entry<String,Object>> s = src.entrySet();
        for (Map.Entry<String,Object> entry : s){
            if (entry.getValue() instanceof JSMap || entry.getValue() instanceof  JSList){
                ret.add(entry.getKey(), ctx.serialize(entry.getValue()));
            } else if (entry.getValue() == null){
                ret.add(entry.getKey(), null);
            } else if (entry.getValue() instanceof Boolean){
                ret.add(entry.getKey(), new JsonPrimitive((Boolean) entry.getValue()));
            } else if (entry.getValue() instanceof Number){
                ret.add(entry.getKey(), new JsonPrimitive((Number) entry.getValue()));
            } else if (entry.getValue() instanceof String){
                ret.add(entry.getKey(), new JsonPrimitive((String) entry.getValue()));
            } else if (entry.getValue() instanceof Character){
                ret.add(entry.getKey(), new JsonPrimitive((Character)entry.getValue()));
            } else {
                ret.add(entry.getKey(), ctx.serialize(entry.getValue()));
            }
        }

        return ret;
    }
}
