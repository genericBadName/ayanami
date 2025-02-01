package com.genericbadname.ayanami.client.gltf.properties.types;

public enum SamplerFilter {
    NEAREST,
    LINEAR,
    NEAREST_MIPMAP_NEAREST,
    LINEAR_MIPMAP_NEAREST,
    NEAREST_MIPMAP_LINEAR,
    LINEAR_MIPMAP_LINEAR;

    public static SamplerFilter fromMagic(int magic) {
        return switch (magic) {
            case 9728 -> NEAREST;
            case 9729 -> LINEAR;
            case 9984 -> NEAREST_MIPMAP_NEAREST;
            case 9985 -> LINEAR_MIPMAP_NEAREST;
            case 9986 -> NEAREST_MIPMAP_LINEAR;
            case 9987 -> LINEAR_MIPMAP_LINEAR;
            default -> null;
        };
    }
}
