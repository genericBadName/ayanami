package com.genericbadname.ayanami.client.renderer.test;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.renderer.ReiEntityRenderer;
import com.genericbadname.ayanami.entity.RobloxianEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import org.joml.Vector3d;

public class RobloxianRenderer extends ReiEntityRenderer<RobloxianEntity> {
    public RobloxianRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, Ayanami.asID("rei/robloxian"), Ayanami.asID("textures/rei/robloxian.png"), new Vector3d(2, -2, -7));
    }
}
