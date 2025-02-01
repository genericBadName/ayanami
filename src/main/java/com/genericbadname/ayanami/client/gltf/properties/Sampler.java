package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.ElementDeserializer;
import com.genericbadname.ayanami.client.gltf.properties.types.SamplerFilter;
import com.genericbadname.ayanami.client.gltf.properties.types.WrapMode;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Texture sampler properties for filtering and wrapping modes.
 * @param magFilter Magnification filter.
 * @param minFilter Minification filter.
 * @param wrapS S (U) wrapping mode. All valid values correspond to WebGL enums.
 * @param wrapT T (V) wrapping mode.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Sampler(
    SamplerFilter magFilter,
    SamplerFilter minFilter,
    WrapMode wrapS,
    WrapMode wrapT,
    String name,
    JsonObject extensions,
    JsonObject extras
) {
    public static JsonDeserializer<Sampler> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();
            SamplerFilter magFilter = ElementDeserializer.enumInt("magFilter", SamplerFilter::fromMagic).apply(object);
            SamplerFilter minFilter = ElementDeserializer.enumInt("minFilter", SamplerFilter::fromMagic).apply(object);
            WrapMode wrapS = ElementDeserializer.enumInt("wrapS", WrapMode::fromMagic)
                    .defaultValue(WrapMode.REPEAT)
                    .apply(object);
            WrapMode wrapT = ElementDeserializer.enumInt("wrapT", WrapMode::fromMagic)
                    .defaultValue(WrapMode.REPEAT)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Sampler(magFilter, minFilter, wrapS, wrapT, name, extensions, extras);
        };
    }
}
