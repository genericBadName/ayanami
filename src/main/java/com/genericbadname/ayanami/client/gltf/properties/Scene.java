package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.*;

/**
 * The root nodes of a scene.
 * @param nodes The indices of each root node.
 *              Each element in the array MUST be unique.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Scene(
    Integer[] nodes,
    String name,
    JsonObject extensions,
    JsonObject extras
) {
    public static JsonDeserializer<Scene> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();
            Integer[] nodes = ElementDeserializer.array("nodes", JsonElement::getAsInt, Integer[]::new)
                    .constraint(arr -> arr.length >= 1)
                    .constraint(Constraints.allUnique)
                    .constraint(Constraints.allNonZero)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Scene(nodes, name, extensions, extras);
        };
    }
}
