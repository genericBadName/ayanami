package com.genericbadname.ayanami.client.gltf.properties.types;

/**
 * The material’s alpha rendering mode enumeration specifying the interpretation of the alpha value of the base color.
 */
public enum AlphaMode {
    /**
     * The alpha value is ignored, and the rendered output is fully opaque.
     */
    OPAQUE,
    /**
     * The rendered output is either fully opaque or fully transparent depending on the alpha value and the specified alphaCutoff value
     */
    MASK,
    /**
     * The alpha value is used to composite the source and destination areas. The rendered output is combined with the background using the normal painting operation (i.e. the Porter and Duff over operator).
     */
    BLEND
}
