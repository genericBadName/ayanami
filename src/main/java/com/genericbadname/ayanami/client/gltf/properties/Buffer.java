package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A buffer points to binary geometry, animation, or skins.
 * @param uri The URI (or IRI) of the buffer.
 *            Relative paths are relative to the current glTF asset.
 *            Instead of referencing an external file, this field MAY contain a data:-URI.
 * @param byteLength The length of the buffer in bytes.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Buffer(
        String uri,
        int byteLength,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<Buffer> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();
            String uri = ElementDeserializer.string("uri").apply(object); // TODO: check iri-format
            int byteLength = ElementDeserializer.integer("byteLength")
                    .required()
                    .constraint(i -> i >= 1)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Buffer(uri, byteLength, name, extensions, extras);
        };
    }
}
