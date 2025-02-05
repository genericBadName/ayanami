package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Reference to a texture.
 * @param index The index of the texture.
 * @param texCoord This integer value is used to construct a string in the format TEXCOORD_<set index> which is a reference to a key in mesh.primitives.attributes (e.g. a value of 0 corresponds to TEXCOORD_0).
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record TextureInfo(
        int index,
        int texCoord,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<TextureInfo> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            int index = ElementDeserializer.integer("index")
                    .required()
                    .apply(object);
            int texCoord = ElementDeserializer.integer("texCoord")
                    .defaultValue(0)
                    .apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new TextureInfo(index, texCoord, extensions, extras);
        };
    }
}
