package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A texture and its sampler.
 * @param sampler The index of the sampler used by this texture. When undefined, a sampler with repeat wrapping and auto filtering SHOULD be used.
 * @param source The index of the image used by this texture. When undefined, an extension or other mechanism SHOULD supply an alternate texture source, otherwise behavior is undefined.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Texture(
        Integer sampler,
        Integer source,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<Texture> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();
            Integer sampler = ElementDeserializer.integer("sampler")
                    .constraint(Constraints.nonZero)
                    .apply(object);
            Integer source = ElementDeserializer.integer("source")
                    .constraint(Constraints.nonZero)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Texture(sampler, source, name, extensions, extras);
        };
    }
}
