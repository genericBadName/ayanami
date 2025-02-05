package com.genericbadname.ayanami.client.gltf.properties.types;

import net.minecraft.client.render.VertexFormat;
import static net.minecraft.client.render.VertexFormat.DrawMode;

/**
 * Primitive topology types.
 */
public enum PrimitiveMode {
    POINTS(DrawMode.LINES),
    LINES(DrawMode.LINES),
    LINE_LOOP(DrawMode.LINE_STRIP),
    LINE_STRIP(DrawMode.LINE_STRIP),
    TRIANGLES(DrawMode.TRIANGLES),
    TRIANGLE_STRIP(DrawMode.TRIANGLE_STRIP),
    TRIANGLE_FAN(DrawMode.TRIANGLE_FAN);

    public final VertexFormat.DrawMode drawMode;
    PrimitiveMode(VertexFormat.DrawMode drawMode) {
        this.drawMode = drawMode;
    }
}
