package com.genericbadname.ayanami.client.processing.processed;

import org.joml.Matrix4d;

public record ProcessedSkin(
        Matrix4d[] inverseBindMatrices,
        Integer skeleton,
        int[] joints
) {
}
