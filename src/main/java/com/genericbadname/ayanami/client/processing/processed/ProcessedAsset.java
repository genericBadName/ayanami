package com.genericbadname.ayanami.client.processing.processed;

import com.genericbadname.ayanami.client.processing.processed.animation.ProcessedAnimation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

/**
 * Fully processed, ready-to-render asset, only containing necessary data.
 */
public record ProcessedAsset(
        int[] roots,
        int totalNodes,
        MeshNode[] meshNodes,
        Int2ObjectMap<Int2ObjectMap<JointNode>> skeletonNodes,
        ProcessedAnimation[] animations
) {

}
