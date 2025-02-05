package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.genericbadname.ayanami.client.gltf.properties.types.AlphaMode;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.joml.Vector3d;
import org.joml.Vector4d;

/**
 * The material appearance of a primitive.
 * @param pbrMetallicRoughness A set of parameter values that are used to define the metallic-roughness material model from Physically Based Rendering (PBR) methodology.
 *                             When undefined, all the default values of pbrMetallicRoughness MUST apply.
 * @param normalTexture The tangent space normal texture. The texture encodes RGB components with linear transfer function.
 * @param occlusionTexture The occlusion texture. The occlusion values are linearly sampled from the R channel. Higher values indicate areas that receive full indirect lighting and lower values indicate no indirect lighting.
 * @param emissiveTexture The emissive texture. It controls the color and intensity of the light being emitted by the material. This texture contains RGB components encoded with the sRGB transfer function.
 * @param emissiveFactor The factors for the emissive color of the material. This value defines linear multipliers for the sampled texels of the emissive texture.
 * @param alphaMode The material’s alpha rendering mode enumeration specifying the interpretation of the alpha value of the base color.
 * @param alphaCutoff Specifies the cutoff threshold when in MASK alpha mode. If the alpha value is greater than or equal to this value then it is rendered as fully opaque, otherwise, it is rendered as fully transparent.
 * @param doubleSided Specifies whether the material is double sided. When this value is false, back-face culling is enabled. When this value is true, back-face culling is disabled and double-sided lighting is enabled.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Material(
    PBRMetallicRoughness pbrMetallicRoughness,
    NormalTextureInfo normalTexture,
    OcclusionTextureInfo occlusionTexture,
    TextureInfo emissiveTexture,
    Vector3d emissiveFactor,
    AlphaMode alphaMode,
    double alphaCutoff,
    boolean doubleSided,
    String name,
    JsonObject extensions,
    JsonObject extras
) {
    public static JsonDeserializer<Material> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            PBRMetallicRoughness pbrMetallicRoughness = ElementDeserializer.defined("pbrMetallicRoughness", PBRMetallicRoughness.class).apply(object);
            NormalTextureInfo normalTexture = ElementDeserializer.defined("normalTexture", NormalTextureInfo.class).apply(object);
            OcclusionTextureInfo occlusionTexture = ElementDeserializer.defined("occlusionTexture", OcclusionTextureInfo.class).apply(object);
            TextureInfo emissiveTexture = ElementDeserializer.defined("emissiveTexture", TextureInfo.class).apply(object);
            Double[] efArray = ElementDeserializer.array("emissiveFactor", JsonElement::getAsDouble, Double[]::new)
                    .defaultValue(new Double[]{0D, 0D, 0D})
                    .constraint(arr -> arr.length == 3)
                    .constraint(arr -> {
                        for (Double doub : arr) {
                            if (doub < 0) return false;
                        }
                        return true;
                    })
                    .constraint(arr -> {
                        for (Double doub : arr) {
                            if (doub > 1) return false;
                        }
                        return true;
                    })
                    .apply(object);

            Vector3d emissiveFactor = new Vector3d(efArray[0], efArray[1], efArray[2]);
            AlphaMode alphaMode = ElementDeserializer.custom("alphaMode", e -> AlphaMode.valueOf(e.getAsString()))
                    .defaultValue(AlphaMode.OPAQUE)
                    .apply(object);
            double alphaCutoff = ElementDeserializer.doubleVal("alphaCutoff")
                    .defaultValue(0.5D)
                    .constraint(d -> d >= 0)
                    .constraint(d -> alphaMode != null)
                    .apply(object);
            boolean doubleSided = ElementDeserializer.bool("doubleSided")
                    .defaultValue(false)
                    .apply(object);
            String name = ElementDeserializer.string("name").apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
            JsonObject extras = ElementDeserializer.object("extras").apply(object);

            return new Material(pbrMetallicRoughness, normalTexture, occlusionTexture, emissiveTexture, emissiveFactor, alphaMode, alphaCutoff, doubleSided, name, extensions, extras);
        };
    }

    /**
     *
     * @param baseColorFactor
     * @param baseColorTexture
     * @param metallicFactor
     * @param roughnessFactor
     * @param metallicRoughnessTexture
     * @param extensions
     * @param extras
     */
    public record PBRMetallicRoughness(
            Vector4d baseColorFactor,
            TextureInfo baseColorTexture,
            double metallicFactor,
            double roughnessFactor,
            TextureInfo metallicRoughnessTexture,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<PBRMetallicRoughness> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                Double[] bcfArray = ElementDeserializer.array("baseColorFactor", JsonElement::getAsDouble, Double[]::new)
                        .constraint(arr -> arr.length == 4)
                        .constraint(arr -> {
                            for (Double doub : arr) {
                                if (doub < 0) return false;
                            }
                            return true;
                        })
                        .constraint(arr -> {
                            for (Double doub : arr) {
                                if (doub > 1) return false;
                            }
                            return true;
                        })
                        .defaultValue(new Double[]{1D, 1D, 1D, 1D})
                        .apply(object);
                Vector4d baseColorFactor = new Vector4d(bcfArray[0], bcfArray[1], bcfArray[2], bcfArray[3]);
                TextureInfo baseColorTexture = ElementDeserializer.defined("baseColorTexture", TextureInfo.class).apply(object);
                double metallicFactor = ElementDeserializer.doubleVal("metallicFactor")
                        .defaultValue(1D)
                        .constraint(d -> d >= 0)
                        .constraint(d -> d <= 1)
                        .apply(object);
                double roughnessFactor = ElementDeserializer.doubleVal("roughnessFactor")
                        .defaultValue(1D)
                        .constraint(d -> d >= 0)
                        .constraint(d -> d <= 1)
                        .apply(object);
                TextureInfo metallicRoughnessTexture = ElementDeserializer.defined("metallicRoughnessTexture", TextureInfo.class).apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new PBRMetallicRoughness(baseColorFactor, baseColorTexture, metallicFactor, roughnessFactor, metallicRoughnessTexture, extensions, extras);
            };
        }
    }

    /**
     * Reference to a texture.
     * @param index The index of the texture.
     * @param texCoord This integer value is used to construct a string in the format TEXCOORD_<set index> which is a reference to a key in mesh.primitives.attributes (e.g. a value of 0 corresponds to TEXCOORD_0).
     * @param scale The scalar parameter applied to each normal vector of the texture.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record NormalTextureInfo(
            int index,
            int texCoord,
            double scale,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<NormalTextureInfo> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int index = ElementDeserializer.integer("index")
                        .required()
                        .apply(object);
                int texCoord = ElementDeserializer.integer("texCoord")
                        .defaultValue(0)
                        .apply(object);
                double scale = ElementDeserializer.doubleVal("scale").apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new NormalTextureInfo(index, texCoord, scale, extensions, extras);
            };
        }
    }

    /**
     * Reference to a texture.
     * @param index The index of the texture.
     * @param texCoord This integer value is used to construct a string in the format TEXCOORD_<set index> which is a reference to a key in mesh.primitives.attributes (e.g. a value of 0 corresponds to TEXCOORD_0).
     * @param strength A scalar parameter controlling the amount of occlusion applied. A value of 0.0 means no occlusion. A value of 1.0 means full occlusion.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record OcclusionTextureInfo(
            int index,
            int texCoord,
            double strength,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<OcclusionTextureInfo> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int index = ElementDeserializer.integer("index")
                        .required()
                        .apply(object);
                int texCoord = ElementDeserializer.integer("texCoord")
                        .defaultValue(0)
                        .apply(object);
                double strength = ElementDeserializer.doubleVal("strength").apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions").apply(object);
                JsonObject extras = ElementDeserializer.object("extras").apply(object);

                return new OcclusionTextureInfo(index, texCoord, strength, extensions, extras);
            };
        }
    }
}
