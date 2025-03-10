package com.genericbadname.ayanami.client.renderer;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public abstract class ReiEntityRenderer<T extends Entity> extends EntityRenderer<T> {
    private final Identifier modelId;
    private final Identifier textureLocation;
    private ProcessedAsset model;
    private final Vector3d modelOffset;
    private boolean safeToRender;

    public ReiEntityRenderer(EntityRendererFactory.Context ctx, Identifier modelId, Identifier textureLocation) {
        this(ctx, modelId, textureLocation, new Vector3d());
    }

    public ReiEntityRenderer(EntityRendererFactory.Context ctx, Identifier modelId, Identifier textureLocation, Vector3d modelOffset) {
        super(ctx);
        this.modelId = modelId;
        this.textureLocation = textureLocation;
        this.modelOffset = modelOffset;

        reload();
    }

    @Override
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        if (!safeToRender) return;

        matrices.push();
        matrices.translate(modelOffset.x, modelOffset.y, modelOffset.z);
        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();

        // shader setup
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, textureLocation);
        RenderSystem.bindTexture(0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.enableBlend();

        // render and cleanup
        model.renderFromRoots(transformationMatrix);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(T entity) {
        return textureLocation;
    }

    public void reload() {
        model = ClientResourceStorage.getModel(modelId);

        if (model != null) {
            safeToRender = true;
        } else {
            safeToRender = false;
            Ayanami.LOGGER.error("Model {} failed to load due to missing components", modelId);
        }
    }
}
