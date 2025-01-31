package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gltf.properties.types.AccessorType;
import com.google.gson.JsonObject;
import org.joou.UInteger;

import java.util.EnumMap;

/**
 * A set of primitives to be rendered. Its global transform is defined by a node that references it.
 * @param primitives An array of primitives, each defining geometry to be rendered.
 * @param weights Array of weights to be applied to the morph targets.
 *                The number of array elements MUST match the number of morph targets.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Mesh(
    Primitive[] primitives,
    double[] weights,
    String name,
    JsonObject extensions,
    JsonObject extras
) {
    /**
     * Geometry to be rendered with the given material.
     * @param attributes Map of mesh attribute semantics. Attribute -> Accessor.
     * @param indices The index of the accessor that contains the vertex indices.
     *                When this is undefined, the primitive defines non-indexed geometry.
     *                When defined, the accessor MUST have SCALAR type and an unsigned integer component type.
     * @param material The index of the material to apply to this primitive when rendering.
     * @param mode The topology type of primitives to render.
     * @param targets An array of morph targets. Attribute -> Accessor. Supports POSITION, NORMAL, and TANGENT.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record Primitive(
            EnumMap<PrimitiveAttribute, Integer> attributes,
            UInteger indices,
            UInteger material,
            PrimitiveMode mode,
            EnumMap<PrimitiveAttribute, UInteger> targets,
            JsonObject extensions,
            JsonObject extras
    ) {

    }

    /**
     * Accessor pairs for attributes. All values with be read as {@link java.lang.Float}
     */
    public enum PrimitiveAttribute {
        /**
         * Unitless XYZ vertex positions
         */
        POSITION(AccessorType.VEC3),
        /**
         * Normalized XYZ vertex normals
         */
        NORMAL(AccessorType.VEC3),
        /**
         * XYZW vertex tangents where the XYZ portion is normalized, and the W component is a sign value (-1 or +1) indicating handedness of the tangent basis
         */
        TANGENT(AccessorType.VEC4),
        /**
         * ST texture coordinates
         */
        TEXCOORD_n(AccessorType.VEC2),
        /**
         * RGBA vertex color linear multiplier
         */
        COLOR_n(AccessorType.VEC4),
        JOINTS_n(AccessorType.VEC4),
        WEIGHTS_n(AccessorType.VEC4);

        public final AccessorType accessorType;
        PrimitiveAttribute(AccessorType accessorType) {
            this.accessorType = accessorType;
        }
    }

    /**
     * Primitive topology types.
     */
    public enum PrimitiveMode {
        POINTS,
        LINES,
        LINE_LOOP,
        LINE_STRIP,
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN
    }
}
