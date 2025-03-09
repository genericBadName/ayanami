package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.genericbadname.ayanami.client.gltf.properties.types.AccessorType;
import com.genericbadname.ayanami.client.gltf.properties.types.ComponentType;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A typed view into a buffer view that contains raw binary data.
 * @param bufferView The index of the buffer view. When undefined, the accessor MUST be initialized with zeros; sparse property or extensions MAY override zeros with actual values.
 * @param byteOffset The offset relative to the start of the buffer view in bytes. This MUST be a multiple of the size of the component datatype. This property MUST NOT be defined when bufferView is undefined.
 * @param componentType The datatype of the accessor’s components. UNSIGNED_INT type MUST NOT be used for any accessor that is not referenced by mesh.primitive.indices.
 * @param normalized Specifies whether integer data values are normalized (true) to [0, 1] (for unsigned types) or to [-1, 1] (for signed types) when they are accessed.
 *                   This property MUST NOT be set to true for accessors with FLOAT or UNSIGNED_INT component type.
 * @param count The number of elements referenced by this accessor, not to be confused with the number of bytes or number of components.
 * @param type Specifies if the accessor’s elements are scalars, vectors, or matrices.
 * @param max Maximum value of each component in this accessor. Array elements MUST be treated as having the same data type as accessor’s componentType.
 * @param min Minimum value of each component in this accessor. Array elements MUST be treated as having the same data type as accessor’s componentType.
 * @param sparse Sparse storage of elements that deviate from their initialization value.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Accessor(
        Integer bufferView,
        int byteOffset,
        ComponentType componentType,
        boolean normalized,
        int count,
        AccessorType type,
        Double[] max,
        Double[] min,
        Sparse sparse,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<Accessor> deserializer() throws JsonParseException {
        return (json, typeR, context) -> {
            JsonObject object = json.getAsJsonObject();

            Integer bufferView = ElementDeserializer.integer("bufferView")
                    .constraint(Constraints.nonNegative)
                    .apply(object);
            int byteOffset = ElementDeserializer.integer("byteOffset")
                    .defaultValue(0)
                    .constraint(Constraints.nonNegative)
                    .apply(object);
            ComponentType componentType = ElementDeserializer.enumInt("componentType", ComponentType::fromMagic)
                    .required()
                    .apply(object);
            boolean normalized = ElementDeserializer.bool("normalized")
                    .defaultValue(false)
                    .constraint(bool -> !((componentType.equals(ComponentType.FLOAT) || componentType.equals(ComponentType.UNSIGNED_INT)) && bool))
                    .apply(object);
            int count = ElementDeserializer.integer("count")
                    .required()
                    .constraint(i -> i >= 1)
                    .apply(object);
            AccessorType type = ElementDeserializer.custom("type", e -> AccessorType.valueOf(e.getAsString()))
                    .required()
                    .apply(object);
            Double[] max = ElementDeserializer.array("max", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == type.components)
                    .apply(object);
            Double[] min = ElementDeserializer.array("min", JsonElement::getAsDouble, Double[]::new)
                    .constraint(arr -> arr.length == type.components)
                    .apply(object);
            Sparse sparse = ElementDeserializer.defined("sparse", Sparse.class).apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Accessor(bufferView, byteOffset, componentType, normalized, count, type, max, min, sparse, name, extensions, extras);
        };
    }
    /**
     * Sparse storage of accessor values that deviate from their initialization value.
     * @param count Number of deviating accessor values stored in the sparse array.
     * @param indices An object pointing to a buffer view containing the indices of deviating accessor values. The number of indices is equal to count. Indices MUST strictly increase.
     * @param values An object pointing to a buffer view containing the deviating accessor values.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record Sparse( // TODO: implement sparse accessor functionality
            int count,
            SparseIndices indices,
            SparseValues values,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<Sparse> deserializer() throws JsonParseException {
            return (json, typeR, context) -> {
                JsonObject object = json.getAsJsonObject();

                int count = ElementDeserializer.integer("count")
                        .required()
                        .apply(object);
                SparseIndices indices = ElementDeserializer.defined("indices", SparseIndices.class)
                        .required()
                        .apply(object);
                SparseValues values = ElementDeserializer.defined("values", SparseValues.class)
                        .required()
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new Sparse(count, indices, values, extensions, extras);
            };
        }
    }

    /**
     * An object pointing to a buffer view containing the indices of deviating accessor values. The number of indices is equal to accessor.sparse.count. Indices MUST strictly increase.
     * @param bufferView The index of the buffer view with sparse indices.
     *                   The referenced buffer view MUST NOT have its target or byteStride properties defined.
     *                   The buffer view and the optional byteOffset MUST be aligned to the componentType byte length.
     * @param byteOffset The offset relative to the start of the buffer view in bytes.
     * @param componentType The indices data type.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record SparseIndices(
            int bufferView,
            int byteOffset,
            ComponentType componentType,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<SparseIndices> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int bufferView = ElementDeserializer.integer("bufferView")
                        .required()
                        .constraint(Constraints.nonNegative)
                        .apply(object);
                int byteOffset = ElementDeserializer.integer("byteOffset")
                        .defaultValue(0)
                        .constraint(Constraints.nonNegative)
                        .apply(object);
                ComponentType componentType = ElementDeserializer.enumInt("componentType", i -> ComponentType.values()[i])
                        .required()
                        .constraint(c -> c.equals(ComponentType.UNSIGNED_BYTE) || c.equals(ComponentType.UNSIGNED_SHORT) || c.equals(ComponentType.UNSIGNED_INT))
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new SparseIndices(bufferView, byteOffset, componentType, extensions, extras);
            };
        }
    }

    /**
     * An object pointing to a buffer view containing the deviating accessor values. The number of elements is equal to accessor.sparse.count times number of components.
     * The elements have the same component type as the base accessor.
     * The elements are tightly packed.
     * Data MUST be aligned following the same rules as the base accessor.
     * @param bufferView The index of the bufferView with sparse values. The referenced buffer view MUST NOT have its target or byteStride properties defined.
     * @param byteOffset The offset relative to the start of the bufferView in bytes.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record SparseValues(
            int bufferView,
            int byteOffset,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<SparseValues> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int bufferView = ElementDeserializer.integer("bufferView")
                        .required()
                        .constraint(Constraints.nonNegative)
                        .apply(object);
                int byteOffset = ElementDeserializer.integer("byteOffset")
                        .defaultValue(0)
                        .constraint(Constraints.nonNegative)
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new SparseValues(bufferView, byteOffset, extensions, extras);
            };
        }
    }
}
