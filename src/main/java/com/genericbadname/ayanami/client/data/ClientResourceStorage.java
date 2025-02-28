package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.util.Identifier;

public class ClientResourceStorage {
    protected static Int2ObjectArrayMap<ProcessedAsset> modelAssets = new Int2ObjectArrayMap<>();

    public static ProcessedAsset getModel(Identifier id) {
        return modelAssets.get(id.hashCode());
    }
}
