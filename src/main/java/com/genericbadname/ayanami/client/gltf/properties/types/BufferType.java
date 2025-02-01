package com.genericbadname.ayanami.client.gltf.properties.types;

public enum BufferType {
    ARRAY_BUFFER,
    ELEMENT_ARRAY_BUFFER;

    public static BufferType fromMagic(int magic) {
        return switch (magic) {
            case 34962 -> ARRAY_BUFFER;
            case 34963 -> ELEMENT_ARRAY_BUFFER;
            default -> null;
        };
    }
}
