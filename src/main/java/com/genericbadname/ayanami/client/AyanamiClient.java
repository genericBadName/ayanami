package com.genericbadname.ayanami.client;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.data.ModelResourcesReloader;
import com.genericbadname.ayanami.client.renderer.test.ChainsawRenderer;
import com.genericbadname.ayanami.client.renderer.test.RobloxianRenderer;
import com.genericbadname.ayanami.entity.RobloxianEntity;
import com.genericbadname.ayanami.network.AyanamiNetworkingConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceType;

public class AyanamiClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ModelResourcesReloader());

        ClientResourceStorage.registerItemRenderer(Ayanami.CHAINSAW, new ChainsawRenderer());
        EntityRendererRegistry.register(Ayanami.ROBLOXIAN, RobloxianRenderer::new);

        // networking packets
        ClientPlayNetworking.registerGlobalReceiver(AyanamiNetworkingConstants.SETUP_ANIMATION_ENTITY_PACKET, (client, handler, buf, sender) -> {
            int id = buf.readVarInt();
            int animation = buf.readVarInt();

            Entity entity = client.world.getEntityById(id);

            if (entity instanceof RobloxianEntity) ((RobloxianEntity)entity).setAnimation(animation);
        });
    }
}
