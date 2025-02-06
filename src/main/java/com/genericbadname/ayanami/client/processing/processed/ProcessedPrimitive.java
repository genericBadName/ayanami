package com.genericbadname.ayanami.client.processing.processed;

import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;

import java.util.List;

/**
 * Fully processed and unpacked vertex information ready to be rendered
 * @param vertices A list of vertices that form this primitive
 * @param mode Draw mode during rendering
 */
public record ProcessedPrimitive(
        List<Vertex> vertices,
        PrimitiveMode mode
) {
}
