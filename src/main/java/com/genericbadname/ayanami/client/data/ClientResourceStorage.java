package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.genericbadname.ayanami.client.renderer.ReiItemRenderer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.EnumMap;

public class ClientResourceStorage {
    protected static final Int2ObjectArrayMap<ProcessedAsset> modelAssets = new Int2ObjectArrayMap<>();
    protected static final Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>> displaySettings = new Int2ObjectArrayMap<>();
    protected static final Int2ObjectArrayMap<ByteBuffer> externalBuffers = new Int2ObjectArrayMap<>();

    protected static final ObjectList<ItemConvertible> itemRenderers = new ObjectArrayList<>();

    public static ProcessedAsset getModel(Identifier id) {
        return modelAssets.get(id.hashCode());
    }

    public static EnumMap<ModelTransformationMode, Matrix4f> getDisplaySettings(Identifier id) {
        return displaySettings.get(id.hashCode());
    }

    public static ByteBuffer getExternalBuffer(Identifier id) {
        return externalBuffers.get(id.hashCode());
    }

    public static void registerItemRenderer(ItemConvertible item, ReiItemRenderer renderer) {
        BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
        itemRenderers.add(item);
    }
}
