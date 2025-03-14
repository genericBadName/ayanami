package com.genericbadname.ayanami.client.processing.processed.animation;

import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import org.joml.Quaterniond;

public record QuaternionFrame(
    double timestamp,
    Quaterniond quaternion,
    Interpolation interpolation
) {
}
