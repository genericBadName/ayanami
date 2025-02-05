package com.genericbadname.ayanami.client.gltf.properties.types;

import java.nio.ByteBuffer;
import java.util.function.Function;

public enum ComponentType {
    BYTE(ByteBuffer::get),
    UNSIGNED_BYTE(ByteBuffer::get),
    SHORT(ByteBuffer::getShort),
    UNSIGNED_SHORT(ByteBuffer::getShort),
    INT(ByteBuffer::getInt),
    UNSIGNED_INT(ByteBuffer::getInt),
    FLOAT(ByteBuffer::getFloat);

    public final Function<ByteBuffer, Number> converter;
    ComponentType(Function<ByteBuffer, Number> converter) {
        this.converter = converter;
    }

    public static ComponentType fromMagic(int magic) {
        return switch (magic) {
            case 5120 -> BYTE;
            case 5121 -> UNSIGNED_BYTE;
            case 5122 -> SHORT;
            case 5123 -> UNSIGNED_SHORT;
            case 5124 -> INT;
            case 5125 -> UNSIGNED_INT;
            case 5126 -> FLOAT;
            default -> null;
        };
    }
}
