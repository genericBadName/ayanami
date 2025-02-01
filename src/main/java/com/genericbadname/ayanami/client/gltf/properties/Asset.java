package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * Metadata about the glTF asset.
 * @param version The glTF version in the form of <major>.<minor> that this asset targets.
 * @param copyright A copyright message suitable for display to credit the content creator.
 * @param generator Tool that generated this glTF model. Useful for debugging.
 * @param minVersion The minimum glTF version in the form of <major>.<minor> that this asset targets. This property MUST NOT be greater than the asset version.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Asset(
    String version,
    String copyright,
    String generator,
    String minVersion,
    JsonObject extensions,
    JsonObject extras
) {
    public static JsonDeserializer<Asset> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            String version = ElementDeserializer.string("version")
                    .required()
                    .apply(object); // TODO: check semver
            String copyright = ElementDeserializer.string("copyright").apply(object);
            String generator = ElementDeserializer.string("generator").apply(object);
            String minVersion = ElementDeserializer.string("minVersion").apply(object); // TODO: check semver against other version
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Asset(version, copyright, generator, minVersion, extensions, extras);
        };
    }
}
