package com.genericbadname.ayanami.client.processing.processed;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Matrix4d;

/**
 * Fully unpacked mesh, containing shapes for rendering
 * @param parent Parent transform
 * @param processedPrimitives All processed primitive shapes
 * @param transform Local transformation matrix
 */
public record ProcessedMesh(
        int parent,
        ObjectList<ProcessedPrimitive> processedPrimitives,
        Matrix4d transform
) {
}
