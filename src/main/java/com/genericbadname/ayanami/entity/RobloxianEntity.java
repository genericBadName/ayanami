package com.genericbadname.ayanami.entity;

import com.genericbadname.ayanami.network.AyanamiNetworkingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class RobloxianEntity extends PathAwareEntity implements ReiAnimatable {
    int timer = 0;
    boolean animated = false;
    int animation = -1;

    public RobloxianEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();
        timer++;

        if (!animated && timer > 40 && !getEntityWorld().isClient) {
            getWorld().getPlayers().forEach(player -> ServerPlayNetworking.send(
                    (ServerPlayerEntity) player,
                    AyanamiNetworkingConstants.SETUP_ANIMATION_ENTITY_PACKET,
                    PacketByteBufs.create().writeVarInt(getId()).writeVarInt(0)
                    )
            );
            animated = true;
        }
    }

    @Override
    public void setAnimation(int animation) {
        this.animation = animation;
    }

    @Override
    public int getSelectedAnimation() {
        return animation;
    }
}
