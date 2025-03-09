package com.genericbadname.ayanami.client;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.data.ModelResourcesReloader;
import com.genericbadname.ayanami.client.renderer.test.ChainsawRenderer;
import com.genericbadname.ayanami.client.renderer.test.RobloxianRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;

public class AyanamiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ModelResourcesReloader());

        ClientResourceStorage.registerItemRenderer(Ayanami.CHAINSAW, new ChainsawRenderer());
        EntityRendererRegistry.register(Ayanami.ROBLOXIAN, RobloxianRenderer::new);
    }
}
