package com.genericbadname.ayanami.client.processing.processed;

import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;

import java.util.List;

public record ProcessedPrimitive(
        List<Vertex> vertices,
        PrimitiveMode mode
) {
}
