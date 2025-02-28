package com.genericbadname.ayanami.client.renderer;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public abstract class ReiRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final Identifier modelLocation;
    private ProcessedAsset model;

    public ReiRenderer(Identifier modelLocation) {
        this.modelLocation = modelLocation;
        if (modelLocation == null) throw new IllegalArgumentException("modelLocation must not be null!");
    }

    @Override
    public final void render(ItemStack itemStack, ModelTransformationMode modelTransformationMode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay) {
        if (model == null) model = ClientResourceStorage.getModel(modelLocation);

        matrices.push();
        if (modelTransformationMode.equals(ModelTransformationMode.GUI)) {
            matrices.scale(5, 5, 5);
        }

        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, Ayanami.asID("textures/rei/chainsaw.png"));
        RenderSystem.bindTexture(0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.enableBlend();

        model.renderFromRoots(transformationMatrix);
        matrices.pop();
    }
}
