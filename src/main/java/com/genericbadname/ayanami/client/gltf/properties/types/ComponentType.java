package com.genericbadname.ayanami.client.gltf.properties.types;

import org.joou.UByte;
import org.joou.UInteger;
import org.joou.UShort;

public enum ComponentType {
    BYTE(Byte.class),
    UNSIGNED_BYTE(UByte.class),
    SHORT(Short.class),
    UNSIGNED_SHORT(UShort.class),
    INT(Integer.class),
    UNSIGNED_INT(UInteger.class),
    FLOAT(Float.class);

    public final Class<?> correspondingClass;
    ComponentType(Class<?> correspondingClass) {
        this.correspondingClass = correspondingClass;
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
