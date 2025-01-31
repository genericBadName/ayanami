package com.genericbadname.ayanami.client.gltf.properties.types;

import org.joou.UByte;
import org.joou.UInteger;
import org.joou.UShort;

public enum ComponentType {
    BYTE(Byte.class, 5120),
    UNSIGNED_BYTE(UByte.class, 5121),
    SHORT(Short.class, 5122),
    UNSIGNED_SHORT(UShort.class, 5123),
    UNSIGNED_INT(UInteger.class, 5125),
    FLOAT(Float.class, 5126);

    public final Class<?> correspondingClass;
    public final int magic;
    ComponentType(Class<?> correspondingClass, int magic) {
        this.correspondingClass = correspondingClass;
        this.magic = magic;
    }
}
