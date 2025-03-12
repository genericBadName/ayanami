package com.genericbadname.ayanami.client.renderer;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.genericbadname.ayanami.client.processing.processed.ProcessedMesh;
import com.genericbadname.ayanami.client.processing.processed.ProcessedPrimitive;
import com.genericbadname.ayanami.client.processing.processed.Vertex;
import com.genericbadname.ayanami.entity.ReiAnimatable;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.joml.*;

public abstract class ReiEntityRenderer<T extends Entity & ReiAnimatable> extends EntityRenderer<T> {
    private final Identifier modelId;
    private final Identifier textureLocation;
    private ProcessedAsset model;
    private final Vector3d modelOffset;
    private boolean safeToRender;

    private AnimationStateManager manager;

    public ReiEntityRenderer(EntityRendererFactory.Context ctx, Identifier modelId, Identifier textureLocation) {
        this(ctx, modelId, textureLocation, new Vector3d());
    }

    public ReiEntityRenderer(EntityRendererFactory.Context ctx, Identifier modelId, Identifier textureLocation, Vector3d modelOffset) {
        super(ctx);
        this.modelId = modelId;
        this.textureLocation = textureLocation;
        this.modelOffset = modelOffset;
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
        renderFromRoots(transformationMatrix);
        matrices.pop();

        if (entity.getSelectedAnimation() > -1 && !manager.isPlayingAnimation()) manager.setupForAnimation(model.animations()[entity.getSelectedAnimation()]);
        manager.tick(tickDelta);
    }

    PrimitiveMode mode = PrimitiveMode.TRIANGLES;
    private void renderFromRoots(Matrix4f baseTransform) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        for (int root : model.roots()) {
            recursiveRender(model.meshes()[root], baseTransform, tessellator, root);
        }

        if (buffer.isBuilding()) tessellator.draw();
    }

    private void recursiveRender(ProcessedMesh self, Matrix4f parentTransform, Tessellator tessellator, int node) {
        Matrix4d localTransform = self.transform();

        if (manager.isPlayingAnimation()) {
            Vector3d translation = (manager.getCurrentTranslation(node) == null) ? new Vector3d() : manager.getCurrentTranslation(node);
            Quaterniond rotation = (manager.getCurrentRotation(node) == null) ? new Quaterniond() : manager.getCurrentRotation(node);
            Vector3d scale = (manager.getCurrentScale(node) == null) ? new Vector3d(1, 1, 1) : manager.getCurrentScale(node);

            localTransform = new Matrix4d().translationRotateScale(new Vector3f().set(translation), new Quaternionf().set(rotation), new Vector3f().set(scale));
        }

        Matrix4f globalTransform = parentTransform.mulAffine(new Matrix4f().set(localTransform), new Matrix4f());
        BufferBuilder buffer = tessellator.getBuffer();

        for (ProcessedPrimitive primitive : self.processedPrimitives()) {
            if (!mode.equals(primitive.mode())) {
                mode = primitive.mode();
                tessellator.draw();
                buffer = tessellator.getBuffer();
                buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            }

            for (Vertex vertex : primitive.vertices()) {
                Vector3d pos = vertex.position();

                buffer.vertex(globalTransform, (float) pos.x, (float) pos.y, (float) pos.z)
                        .texture((float) vertex.texCoord().coord().x, (float) vertex.texCoord().coord().y)
                        .color(0xFFFFFFFF)
                        .normal((float) vertex.normal().x, (float) vertex.normal().y, (float) vertex.normal().z)
                        .next();
            }
        }

        if (self.children() != null) {
            for (int child : self.children()) {
                recursiveRender(model.meshes()[child], globalTransform, tessellator, child);
            }
        }
    }

    @Override
    public Identifier getTexture(T entity) {
        return textureLocation;
    }

    public void reload() {
        model = ClientResourceStorage.getModel(modelId);

        if (model != null) {
            manager = new AnimationStateManager(model.totalNodes());
            safeToRender = true;
        } else {
            safeToRender = false;
            Ayanami.LOGGER.error("Model {} failed to load due to missing components", modelId);
        }
    }
}
