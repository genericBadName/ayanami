package com.genericbadname.ayanami.client.gson;

import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ElementDeserializer<T> {
    private final String element;
    private final Function<JsonElement, T> converter;

    private Predicate<T> constraints = val -> true;
    private T defaultValue = null;
    private boolean required = false;

    private ElementDeserializer(String element, Function<JsonElement, T> converter) {
        this.element = element;
        this.converter = converter;
    }

    public static ElementDeserializer<String> string(String element) {
        return new ElementDeserializer<>(element, JsonElement::getAsString);
    }

    public static ElementDeserializer<Integer> integer(String element) {
        return new ElementDeserializer<>(element, JsonElement::getAsInt);
    }

    public static ElementDeserializer<Boolean> bool(String element) {
        return new ElementDeserializer<>(element, JsonElement::getAsBoolean);
    }

    public static ElementDeserializer<Double> doubleVal(String element) {
        return new ElementDeserializer<>(element, JsonElement::getAsDouble);
    }

    public static ElementDeserializer<JsonObject> object(String element) {
        return new ElementDeserializer<>(element, JsonElement::getAsJsonObject);
    }

    public static <T> ElementDeserializer<T[]> array(String element, Function<JsonElement, T> elementConverter, Function<Integer, T[]> constructor) {
        Function<JsonElement, T[]> arrayConverter = e -> {
            if (!e.isJsonArray()) return null;
            JsonArray jArr = e.getAsJsonArray();
            List<T> outputList = new ArrayList<>();

            for (JsonElement arrElement : jArr) {
                outputList.add(elementConverter.apply(arrElement));
            }

            return outputList.toArray(constructor.apply(outputList.size()));
        };
        return new ElementDeserializer<>(element, arrayConverter);
    }

    public static <K, V, T extends Map<K, V>> ElementDeserializer<T> map(String element, Function<String, K> keyConverter, Function<JsonElement, V> valueConverter, Supplier<T> constructor) {
        Function<JsonElement, T> mapConverter = e -> {
            if (!e.isJsonObject()) return null;
            JsonObject object = e.getAsJsonObject();
            T map = constructor.get();

            object.entrySet().forEach(entry -> map.put(keyConverter.apply(entry.getKey()), valueConverter.apply(entry.getValue())));

            return map;
        };

        return new ElementDeserializer<>(element, mapConverter);
    }

    public static <T> ElementDeserializer<T> enumInt(String element, Function<Integer, T> enumConverter) {
        return new ElementDeserializer<>(element, e -> enumConverter.apply(e.getAsInt()));
    }

    public static <T> ElementDeserializer<T> defined(String element, Class<T> clazz) {
        return new ElementDeserializer<>(element, e -> GltfAsset.ASSET_GSON.fromJson(e, clazz));
    }

    public static <T> ElementDeserializer<T> custom(String element, Function<JsonElement, T> converter) {
        return new ElementDeserializer<>(element, converter);
    }

    public ElementDeserializer<T> constraint(Predicate<T> predicate) {
        constraints = constraints.and(predicate);

        return this;
    }

    public ElementDeserializer<T> defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ElementDeserializer<T> required() {
        this.required = true;
        return this;
    }

    public T apply(JsonObject object) {
        JsonElement jsonElement = object.get(element);

        if (jsonElement != null) {
            T output = converter.apply(jsonElement);

            if (!constraints.test(output)) throw new JsonParseException("Constraints on "+element+" not met!");

            return output;
        } else if (required) {
            throw new JsonParseException("Required element "+element+" not found!");
        }

        return defaultValue;
    }
}
