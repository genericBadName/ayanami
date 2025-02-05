package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Image data used to create a texture. Image MAY be referenced by an URI (or IRI) or a buffer view index.
 * @param uri The URI (or IRI) of the image. Relative paths are relative to the current glTF asset. Instead of referencing an external file, this field MAY contain a data:-URI.
 * @param mimeType The image’s media type. This field MUST be defined when bufferView is defined.
 * @param bufferView The index of the bufferView that contains the image. This field MUST NOT be defined when uri is defined.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record ImageData(
        String uri,
        String mimeType,
        Integer bufferView,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<ImageData> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            String uri = ElementDeserializer.string("uri").apply(object);
            String mimeType = ElementDeserializer.string("mimeType")
                    .apply(object);
            Integer bufferView = ElementDeserializer.integer("bufferView")
                    .constraint(Constraints.nonZero)
                    .constraint(i -> uri == null)
                    .constraint(i -> mimeType != null)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new ImageData(uri, mimeType, bufferView, name, extensions, extras);
        };
    }
}
