package com.genericbadname.ayanami.client.processing.processed;

import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Matrix4d;
import org.joml.Vector4d;

/**
 * Fully unpacked mesh, containing shapes for rendering
 * @param children Children meshes
 * @param processedPrimitives All processed primitive shapes
 * @param transform Local transformation matrix
 */
public record MeshNode(
        Integer[] children,
        ObjectList<ProcessedPrimitive> processedPrimitives,
        Matrix4d transform
) {
}
