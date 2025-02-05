package com.genericbadname.ayanami.client.processing.processed;

import org.joml.Vector3d;

public record Vertex(
        Vector3d position,
        Vector3d normal,
        TexCoord texCoord
) {
}
