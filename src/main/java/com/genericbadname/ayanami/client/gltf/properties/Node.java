package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.Constraints;
import com.genericbadname.ayanami.ElementDeserializer;
import com.google.gson.*;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;

/**
 * A node in the node hierarchy.
 * @param camera The index of the camera referenced by this node.
 * @param children The indices of this node’s children.
 *                 Each element in the array MUST be unique.
 * @param skin The index of the skin referenced by this node.
 * @param matrix A floating-point 4x4 transformation matrix stored in column-major order.
 * @param mesh The index of the mesh in this node.
 * @param rotation The node’s unit quaternion rotation in the order (x, y, z, w), where w is the scalar.
 *                 Each element in the array MUST be greater than or equal to -1 and less than or equal to 1.
 * @param scale The node’s non-uniform scale, given as the scaling factors along the x, y, and z axes.
 *              The node’s non-uniform scale, given as the scaling factors along the x, y, and z axes.
 * @param translation The node’s translation along the x, y, and z axes.
 * @param weights The weights of the instantiated morph target.
 *                The number of array elements MUST match the number of morph targets of the referenced mesh.
 *                When defined, mesh MUST also be defined.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Node(
        Integer camera,
        Integer[] children,
        Integer mesh,
        Integer skin,
        Matrix4d matrix,
        Quaterniond rotation,
        Vector3d scale,
        Vector3d translation,
        Double[] weights,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<Node> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            Integer camera = ElementDeserializer.integer("camera").constraint(Constraints.nonZero).apply(object);
            Integer[] children = ElementDeserializer.array("children", JsonElement::getAsInt, Integer[]::new)
                    .constraint(arr -> arr.length >= 1)
                    .constraint(Constraints.allUnique)
                    .constraint(Constraints.allNonZero)
                    .apply(object);
            Integer mesh = ElementDeserializer.integer("mesh")
                    .constraint(Constraints.nonZero)
                    .apply(object);
            Integer skin = ElementDeserializer.integer("skin")
                    .constraint(Constraints.nonZero)
                    .apply(object); // TODO: ensure that mesh is also defined if skin is
            Double[] matrixArray = ElementDeserializer.array("matrix", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == 16)
                    .defaultValue(new Double[]{1D, 0D, 0D, 0D, 0D, 1D, 0D, 0D, 0D, 0D, 1D, 0D, 0D, 0D, 0D, 1D})
                    .apply(object);
            Matrix4d matrix = new Matrix4d(
                    matrixArray[0], matrixArray[1], matrixArray[2], matrixArray[3],
                    matrixArray[4], matrixArray[5], matrixArray[6], matrixArray[7],
                    matrixArray[8], matrixArray[9], matrixArray[10], matrixArray[11],
                    matrixArray[12], matrixArray[13], matrixArray[14], matrixArray[15]
            );
            Double[] rotationArray = ElementDeserializer.array("rotation", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == 4)
                    .constraint(arr -> {
                        for (Double d : arr) {
                            if (d > 1 || d < -1) return false;
                        }
                        return true;
                    })
                    .defaultValue(new Double[]{0D, 0D, 0D, 1D})
                    .apply(object);
            Quaterniond rotation = new Quaterniond(rotationArray[0], rotationArray[1], rotationArray[2], rotationArray[3]);
            Double[] scaleArray = ElementDeserializer.array("scale", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == 3)
                    .defaultValue(new Double[]{1D, 1D, 1D})
                    .apply(object);
            Vector3d scale = new Vector3d(scaleArray[0], scaleArray[1], scaleArray[2]);
            Double[] translationArray = ElementDeserializer.array("translation", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == 3)
                    .defaultValue(new Double[]{1D, 1D, 1D})
                    .apply(object);
            Vector3d translation = new Vector3d(translationArray[0], translationArray[1], translationArray[2]);
            Double[] weights = ElementDeserializer.array("weights", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length >= 1)
                    .constraint(arr -> mesh != null)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Node(camera, children, mesh, skin, matrix, rotation, scale, translation, weights, name, extensions, extras);
        };
    }
}
