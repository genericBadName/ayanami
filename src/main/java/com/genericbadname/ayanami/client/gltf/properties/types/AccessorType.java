package com.genericbadname.ayanami.client.gltf.properties.types;

public enum AccessorType {
    SCALAR(1),
    VEC2(2),
    VEC3(3),
    VEC4(4),
    MAT2(4),
    MAT3(9),
    MAT4(16);

    public final int components;
    AccessorType(int components) {
        this.components = components;
    }
}
