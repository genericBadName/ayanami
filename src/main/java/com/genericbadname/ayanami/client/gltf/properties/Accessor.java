package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.ArgUtils;
import com.genericbadname.ayanami.client.gltf.properties.types.AccessorType;
import com.genericbadname.ayanami.client.gltf.properties.types.ComponentType;
import com.google.gson.JsonObject;
import org.joou.UInteger;

public record Accessor(
        UInteger bufferView,
        UInteger byteOffset,
        int componentType,
        boolean normalized,
        int count,
        AccessorType type,
        Number[] max,
        Number[] min,
        Sparse sparse,
        String name,
        JsonObject extensions,
        JsonObject extras
) {
    public Accessor(UInteger bufferView, UInteger byteOffset, int componentType, boolean normalized, int count, AccessorType type, Number[] max, Number[] min, Sparse sparse, String name, JsonObject extensions, JsonObject extras) {
        this.bufferView = bufferView;
        this.byteOffset = byteOffset;
        this.componentType = componentType; // MUST NOT be used for any accessor that is not referenced by mesh.primitive.indices
        if (normalized && (componentType == ComponentType.FLOAT.magic || componentType == ComponentType.UNSIGNED_INT.magic)) {
            throw new IllegalArgumentException("Cannot be normalized and have ComponentType of Float or Unsigned Int");
        } else {
            this.normalized = normalized;
        }
        this.count = ArgUtils.greater(count, 1);
        this.type = type;
        this.max = ArgUtils.ensureLength(max, type.length);
        this.min = ArgUtils.ensureLength(min, type.length);
        this.sparse = sparse;
        this.name = name;
        this.extensions = extensions;
        this.extras = extras;
    }

    public record Sparse(
            int count,
            JsonObject extensions,
            JsonObject extras
    ) {
        public Sparse(int count, JsonObject extensions, JsonObject extras) {
            this.count = ArgUtils.greater(count, 1);
            this.extensions = extensions;
            this.extras = extras;
        }
    }

    // The referenced buffer view MUST NOT have its target or byteStride properties defined.
    // The buffer view and the optional byteOffset MUST be aligned to the componentType byte length.
    public record SparseIndices(
            UInteger bufferView,
            UInteger byteOffset,
            int componentType,
            JsonObject extensions,
            JsonObject extras
    ) {

    }
}
