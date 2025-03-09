package com.genericbadname.ayanami.client.gltf.properties;

import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.gltf.properties.types.Interpolation;
import com.genericbadname.ayanami.client.gltf.properties.types.TargetPath;
import com.genericbadname.ayanami.client.gson.Constraints;
import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * A keyframe animation.
 * @param channels An array of animation channels. An animation channel combines an animation sampler with a target property being animated.
 *                 Different channels of the same animation MUST NOT have the same targets.
 * @param samplers An array of animation samplers. An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
 * @param name The user-defined name of this object.
 * @param extensions JSON object with extension-specific objects.
 * @param extras Application-specific data.
 */
public record Animation(
    Channel[] channels,
    Sampler[] samplers,
    String name,
    JsonObject extensions,
    JsonObject extras
) {
    public static JsonDeserializer<Animation> deserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            Channel[] channels = ElementDeserializer.array("channels", e -> GltfAsset.ASSET_GSON.fromJson(e, Channel.class), Channel[]::new)
                    .required()
                    .apply(object);
            Sampler[] samplers = ElementDeserializer.array("samplers", e -> GltfAsset.ASSET_GSON.fromJson(e, Sampler.class), Sampler[]::new)
                    .required()
                    .apply(object);
            String name = ElementDeserializer.string("name")
                    .apply(object);
            JsonObject extensions = ElementDeserializer.object("extensions")
                    .apply(object);
            JsonObject extras = ElementDeserializer.object("extras")
                    .apply(object);

            return new Animation(channels, samplers, name, extensions, extras);
        };
    }

    /**
     * An animation channel combines an animation sampler with a target property being animated.
     * @param sampler The index of a sampler in this animation used to compute the value for the target, e.g., a node’s translation, rotation, or scale (TRS).
     * @param target The descriptor of the animated property.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record Channel(
            int sampler,
            Target target,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<Channel> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int sampler = ElementDeserializer.integer("sampler")
                        .constraint(Constraints.nonNegative)
                        .required()
                        .apply(object);
                Target target = ElementDeserializer.defined("target", Target.class)
                        .required()
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions")
                        .apply(object);
                JsonObject extras = ElementDeserializer.object("extras")
                        .apply(object);

                return new Channel(sampler, target, extensions, extras);
            };
        }
    }

    /**
     * The descriptor of the animated property.
     * @param node The index of the node to animate. When undefined, the animated object MAY be defined by an extension.
     * @param path The name of the node’s TRS property to animate, or the "weights" of the Morph Targets it instantiates.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record Target(
            Integer node,
            TargetPath path,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<Target> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                Integer node = ElementDeserializer.integer("node")
                        .constraint(Constraints.nonNegative)
                        .apply(object);
                TargetPath path = ElementDeserializer.enumString("path", TargetPath::valueOf)
                        .required()
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions")
                        .apply(object);
                JsonObject extras = ElementDeserializer.object("extras")
                        .apply(object);

                return new Target(node, path, extensions, extras);
            };
        }
    }

    /**
     * An animation sampler combines timestamps with a sequence of output values and defines an interpolation algorithm.
     * @param input The index of an accessor containing keyframe timestamps.
     * @param interpolation Interpolation algorithm.
     * @param output The index of an accessor, containing keyframe output values.
     * @param extensions JSON object with extension-specific objects.
     * @param extras Application-specific data.
     */
    public record Sampler(
            int input,
            Interpolation interpolation,
            int output,
            JsonObject extensions,
            JsonObject extras
    ) {
        public static JsonDeserializer<Sampler> deserializer() throws JsonParseException {
            return (json, type, context) -> {
                JsonObject object = json.getAsJsonObject();

                int input = ElementDeserializer.integer("input")
                        .constraint(Constraints.nonNegative)
                        .required()
                        .apply(object);
                Interpolation interpolation = ElementDeserializer.enumString("interpolation", Interpolation::valueOf)
                        .defaultValue(Interpolation.LINEAR)
                        .apply(object);
                int output = ElementDeserializer.integer("output")
                        .constraint(Constraints.nonNegative)
                        .required()
                        .apply(object);
                JsonObject extensions = ElementDeserializer.object("extensions")
                        .apply(object);
                JsonObject extras = ElementDeserializer.object("extras")
                        .apply(object);

                return new Sampler(input, interpolation, output, extensions, extras);
            };
        }
    }
}
