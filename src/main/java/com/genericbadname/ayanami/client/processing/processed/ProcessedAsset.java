package com.genericbadname.ayanami.client.processing.processed;

import it.unimi.dsi.fastutil.objects.ObjectList;

/**
 * Fully processed, ready-to-render asset, only containing necessary data.
 * @param meshes All used meshes within the asset
 */
public record ProcessedAsset(
        ObjectList<ProcessedMesh> meshes
) {
}
