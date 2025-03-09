package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.client.gltf.GltfAsset;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.EnumMap;

public record ModelResources(Object2ObjectArrayMap<Identifier, GltfAsset> assets, Int2ObjectArrayMap<ByteBuffer> externalBuffers, Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>> displaySettings) {
}
