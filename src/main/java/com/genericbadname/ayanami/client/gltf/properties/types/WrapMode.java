package com.genericbadname.ayanami.client.gltf.properties.types;

public enum WrapMode {
    CLAMP_TO_EDGE,
    MIRRORED_REPEAT,
    REPEAT;

    public static WrapMode fromMagic(int magic) {
        return switch (magic) {
            case 33071 -> CLAMP_TO_EDGE;
            case 33648 -> MIRRORED_REPEAT;
            case 10497 -> REPEAT;
            default -> null;
        };
    }
}
