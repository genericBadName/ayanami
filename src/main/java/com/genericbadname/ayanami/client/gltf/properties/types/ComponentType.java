package com.genericbadname.ayanami.client.gltf.properties.types;

import java.nio.ByteBuffer;
import java.util.function.Function;

public enum ComponentType {
    BYTE(ByteBuffer::get, 1),
    UNSIGNED_BYTE(ByteBuffer::get, 1),
    SHORT(ByteBuffer::getShort, 2),
    UNSIGNED_SHORT(ByteBuffer::getShort, 2),
    INT(ByteBuffer::getInt, 4),
    UNSIGNED_INT(ByteBuffer::getInt, 4),
    FLOAT(ByteBuffer::getFloat, 4);

    public final Function<ByteBuffer, Number> converter;
    public final int bytes;
    ComponentType(Function<ByteBuffer, Number> converter, int bytes) {
        this.converter = converter;
        this.bytes = bytes;
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
