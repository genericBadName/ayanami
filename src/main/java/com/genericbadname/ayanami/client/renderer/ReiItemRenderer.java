package com.genericbadname.ayanami.client.renderer;

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

import java.util.EnumMap;

public abstract class ReiItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private final Identifier modelLocation;
    private final Identifier textureLocation;
    private ProcessedAsset model;
    private EnumMap<ModelTransformationMode, Matrix4f> displaySettings;

    public ReiItemRenderer(Identifier modelId, Identifier textureLocation) {
        this.modelLocation = modelId;
        this.textureLocation = textureLocation;

        if (modelId == null) throw new IllegalArgumentException("modelId must not be null!");
        if (textureLocation == null) throw new IllegalArgumentException("textureLocation must not be null!");
    }

    @Override
    public final void render(ItemStack itemStack, ModelTransformationMode modelTransformationMode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay) {
        if (model == null) model = ClientResourceStorage.getModel(modelLocation);
        if (displaySettings == null) displaySettings = ClientResourceStorage.getDisplaySettings(modelLocation);

        matrices.push();
        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();

        // display transforms
        Matrix4f settingTransform = displaySettings.get(modelTransformationMode);
        if (settingTransform != null) transformationMatrix.mulAffine(settingTransform);

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
}
