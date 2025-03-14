package com.genericbadname.ayanami.client.processing.processed;

import org.joml.Vector3d;
import org.joml.Vector4d;
import org.joml.Vector4i;

public record Vertex(
        Vector3d position,
        Vector3d normal,
        TexCoord texCoord,
        Vector4i joints,
        Vector4d weights
) {
}
