package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Arrays;

/**
 * Joints and matrices defining a skin.
 * @param inverseBindMatrices The index of the accessor containing the floating-point 4x4 inverse-bind matrices.
 *                            Its accessor.count property MUST be greater than or equal to the number of elements of the joints array. When undefined, each matrix is a 4x4 identity matrix.
 * @param skeleton The index of the node used as a skeleton root.
 *                 The node MUST be the closest common root of the joints hierarchy or a direct or indirect parent node of the closest common root.
 * @param joints Indices of skeleton nodes, used as joints in this skin.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Skin(
        Integer inverseBindMatrices,
        Integer skeleton,
        int[] joints,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<Skin> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            Integer inverseBindMatrices = ElementDeserializer.integer("inverseBindMatrices")
                    .constraint(Constraints.nonNegative)
                    .apply(object);
            Integer skeleton = ElementDeserializer.integer("skeleton")
                    .constraint(Constraints.nonNegative)
                    .apply(object);
            Integer[] joints = ElementDeserializer.array("joints", JsonElement::getAsInt, Integer[]::new)
                    .constraint(arr -> arr.length >= 1)
                    .constraint(Constraints.allUnique)
                    .constraint(Constraints.allNonNegative)
                    .required()
                    .apply(object);
            String name = ElementDeserializer.string("name")
                    .apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions")
                    .apply(object);
            JsonObject extras = ElementDeserializer.object("extras")
                    .apply(object);

            return new Skin(inverseBindMatrices, skeleton, Arrays.stream(joints).mapToInt(Integer::intValue).toArray(), name, extensions, extras);
        };
    }
}
