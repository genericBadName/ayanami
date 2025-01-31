package com.genericbadname.ayanami.client;

import com.genericbadname.ayanami.client.data.ClientResourceReloadListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class AyanamiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ClientResourceReloadListener());
    }
}
