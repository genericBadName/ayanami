package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import org.joml.Vector4d;

public record Vector4Frame(
        double timestamp,
        Vector4d vector,
        Interpolation interpolation
) {
}
