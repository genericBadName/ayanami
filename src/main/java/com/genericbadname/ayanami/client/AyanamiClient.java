package com.genericbadname.ayanami.client;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceReloadListener;
import com.genericbadname.ayanami.client.renderer.ChainsawRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class AyanamiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ClientResourceReloadListener());
        BuiltinItemRendererRegistry.INSTANCE.register(Ayanami.CHAINSAW, new ChainsawRenderer());
    }
}
