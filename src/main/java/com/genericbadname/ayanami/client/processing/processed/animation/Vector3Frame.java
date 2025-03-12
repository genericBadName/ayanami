package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import org.joml.Vector3d;

public record Vector3Frame(
        double timestamp,
        Vector3d vector,
        Interpolation interpolation
) {
}
