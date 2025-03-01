package com.genericbadname.ayanami.client.display;

import com.genericbadname.ayanami.client.gson.ElementDeserializer;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.joml.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;

public class DisplaySettings {
    private static JsonDeserializer<Matrix4f> matDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            JsonObject object = json.getAsJsonObject();

            Float[] translationArray = ElementDeserializer.array("translation", JsonElement::getAsFloat, Float[]::new)
                    .constraint(arr -> arr.length == 3)
                    .defaultValue(new Float[]{0F, 0F, 0F})
                    .apply(object);
            Vector3f translation = new Vector3f(translationArray[0], translationArray[1], translationArray[2]);

            Float[] rotationArray = ElementDeserializer.array("rotation", JsonElement::getAsFloat,Float[]::new)
                    .constraint(arr -> arr.length == 3)
                    .defaultValue(new Float[]{0F, 0F, 0F})
                    .apply(object);
            Quaternionf rotation = new Quaternionf().rotateXYZ(rotationArray[0], rotationArray[1], rotationArray[2]);

            Float[] scaleArray = ElementDeserializer.array("scale", JsonElement::getAsFloat, Float[]::new)
                    .constraint(arr -> arr.length == 3)
                    .defaultValue(new Float[]{1F, 1F, 1F})
                    .apply(object);
            Vector3f scale = new Vector3f(scaleArray[0], scaleArray[1], scaleArray[2]);

            return new Matrix4f().translationRotateScale(translation, rotation, scale);
        };
    }

    private static JsonDeserializer<ModelTransformationMode> mtmDeserializer() throws JsonParseException {
        return (json, type, context) -> {
            String name = json.getAsString();
            for (ModelTransformationMode mode : ModelTransformationMode.values()) {
                if (mode.asString().equals(name)) return mode;
            }

            return null;
        };
    }

    public static final Gson DISPLAY_SETTINGS_GSON = new GsonBuilder()
            .registerTypeAdapter(Matrix4f.class, DisplaySettings.matDeserializer())
            .registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> {
                Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                return new EnumMap((Class<?>) types[0]);
            })
            .registerTypeAdapter(ModelTransformationMode.class, mtmDeserializer())
            .create();

    public static final Type ENUM_MAP_TYPE = new TypeToken<EnumMap<ModelTransformationMode, Matrix4f>>() {}.getType();
}
