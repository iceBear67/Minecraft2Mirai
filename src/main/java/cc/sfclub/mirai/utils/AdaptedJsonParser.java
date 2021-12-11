package cc.sfclub.mirai.utils;

import cc.sfclub.mirai.adapts.SBSpigot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AdaptedJsonParser {
    public static JsonElement parseString(String parsed){
        var jo = JsonParser.parseString(parsed);
        if(jo.isJsonObject() && jo.getAsJsonObject().has("data")){
            return jo.getAsJsonObject().get("data");
        }
        return jo;
    }
    private static final Gson gson = new Gson();
    public static <T> T adaptedSerialize(String text,Class<T> clazzOfT){
        var t = AdaptedJsonParser.parseString(text).toString();
        return gson.fromJson(t,clazzOfT);
    }
}
