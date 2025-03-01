package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.EnumMap;

public class ClientResourceStorage {
    protected static Int2ObjectArrayMap<ProcessedAsset> modelAssets = new Int2ObjectArrayMap<>();
    protected static Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>> displaySettings = new Int2ObjectArrayMap<>();

    public static ProcessedAsset getModel(Identifier id) {
        return modelAssets.get(id.hashCode());
    }

    public static EnumMap<ModelTransformationMode, Matrix4f> getDisplaySettings(Identifier id) {
        return displaySettings.get(id.hashCode());
    }
}
