package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.genericbadname.ayanami.client.gltf.properties.types.BufferType;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A view into a buffer generally representing a subset of the buffer.
 * @param buffer The index of the buffer.
 * @param byteOffset The offset into the buffer in bytes.
 * @param byteLength The length of the bufferView in bytes.
 * @param byteStride The stride, in bytes, between vertex attributes.
 *                  When this is not defined, data is tightly packed.
 *                  When two or more accessors use the same buffer view, this field MUST be defined.
 * @param target The hint representing the intended GPU buffer type to use with this buffer view.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record BufferView(
        int buffer,
        int byteOffset,
        int byteLength,
        Integer byteStride,
        BufferType target,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public static JsonDeserializer<BufferView> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();
            int buffer = ElementDeserializer.integer("buffer")
                    .required()
                    .constraint(Constraints.nonZero)
                    .apply(object);
            int byteOffset = ElementDeserializer.integer("byteOffset")
                    .constraint(Constraints.nonZero)
                    .defaultValue(0)
                    .apply(object);
            int byteLength = ElementDeserializer.integer("byteLength")
                    .required()
                    .constraint(i -> i >= 1)
                    .apply(object);
            Integer byteStride = ElementDeserializer.integer("byteStride")
                    .constraint(i -> i >= 4)
                    .constraint(i -> i <= 252)
                    .apply(object); // TODO: When two or more accessors use the same buffer view, this field MUST be defined.
            BufferType target = ElementDeserializer.enumInt("target", BufferType::fromMagic).apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new BufferView(buffer, byteOffset, byteLength, byteStride, target, name, extensions, extras);
        };
    }
}
