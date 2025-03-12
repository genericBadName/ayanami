package com.genericbadname.ayanami.client.processing.processed;

import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;
import com.genericbadname.ayanami.client.processing.processed.animation.ProcessedAnimation;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;

/**
 * Fully processed, ready-to-render asset, only containing necessary data.
 */
public record ProcessedAsset(
        ProcessedMesh[] meshes,
        int[] roots,
        ProcessedAnimation[] animations,
        int totalNodes
) {

}
