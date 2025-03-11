package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import org.joml.Vector4d;

public record SamplerData(
        double[] times,
        Vector4d[] vector4ds,
        Interpolation interpolation
) {
}
