package com.genericbadname.ayanami.client.renderer;

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

    public ReiRenderer(Identifier modelLocation) {
        this.modelLocation = modelLocation;
        if (modelLocation == null) throw new IllegalArgumentException("modelLocation must not be null!");
    }

    @Override
    public final void render(ItemStack itemStack, ModelTransformationMode modelTransformationMode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay) {
        Matrix4f transformationMatrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // Initialize the buffer using the specified format and draw mode.
        buffer.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        buffer.vertex(transformationMatrix, 2, 0, 0.5F).color(0xFF414141).next();
        buffer.vertex(transformationMatrix, 0.5F, 2, 0.5F).color(0xFF000000).next();
        buffer.vertex(transformationMatrix, 3.5F, 2, 0.5F).color(0xFF000000).next();
        buffer.vertex(transformationMatrix, 2, 4, 0.5F).color(0xFF414141).next();

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.draw();

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
    }
}
