package com.genericbadname.ayanami;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonHelper;
import org.joou.UInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JsonUtil {
    public static String string(JsonObject object, String key) {
        return (object.has(key)) ? JsonHelper.getString(object, key) : null;
    }

    public static Integer integer(JsonObject object, String key) {
        return (object.has(key)) ? JsonHelper.getInt(object, key) : null;
    }

    public static UInteger uinteger(JsonObject object, String key) {
        return (object.has(key)) ? UInteger.valueOf(JsonHelper.getInt(object, key)) : null;
    }

    public static JsonObject object(JsonObject object, String key) {
        return (object.has(key)) ? JsonHelper.getObject(object, key) : null;
    }

    public static <T> T[] array(JsonObject object, String key, Function<JsonElement, T> converter, Function<Integer, T[]> constructor) {
        if (!object.has(key)) return null;
        if (!object.get(key).isJsonArray()) return null;
        JsonArray jArr = object.get(key).getAsJsonArray();
        List<T> outputList = new ArrayList<>();

        for (JsonElement element : jArr) {
            outputList.add(converter.apply(element));
        }

        return outputList.toArray(constructor.apply(outputList.size()));
    }


}
