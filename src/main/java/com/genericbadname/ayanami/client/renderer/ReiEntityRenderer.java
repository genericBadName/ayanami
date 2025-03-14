package com.genericbadname.ayanami.client.renderer;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;
import com.genericbadname.ayanami.client.processing.processed.*;
import com.genericbadname.ayanami.entity.ReiAnimatable;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

public abstract class ReiEntityRenderer<T extends Entity & ReiAnimatable> extends EntityRenderer<T> {
    private final Identifier modelId;
    private final Identifier textureLocation;
    private ProcessedAsset model;
    private final Vector3d modelOffset;
    private boolean safeToRender;

    private AnimationStateManager manager;
    private Int2ObjectMap<Matrix4d> jointMatrices;

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
        computeJointMatrices(transformationMatrix.get(new Matrix4d()));
        renderFromRoots(transformationMatrix);
        matrices.pop();

        if (entity.getSelectedAnimation() > -1 && !manager.isPlayingAnimation()) manager.setupForAnimation(model.animations()[entity.getSelectedAnimation()]);
        manager.tick(tickDelta);
    }

    private void computeJointMatrices(Matrix4d globalTransform) {
        if (model.skeletonNodes().isEmpty()) return;

        Int2ObjectMap<JointNode> skeleton = model.skeletonNodes().get(0);

        for (Int2ObjectMap.Entry<JointNode> joint : skeleton.int2ObjectEntrySet()) {
            JointNode jointNode = joint.getValue();
            Matrix4d jointMatrix = new Matrix4d();
            globalTransform.invert(jointMatrix).mul(jointNode.globalJointTransform(), jointMatrix).mul(jointNode.inverseBindMatrix(), jointMatrix);
            jointMatrices.put(joint.getIntKey(), jointMatrix);
        }
    }

    PrimitiveMode mode = PrimitiveMode.TRIANGLES;
    private void renderFromRoots(Matrix4f baseTransform) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        for (int root : model.roots()) {
            recursiveRender(model.meshNodes()[root], baseTransform.get(new Matrix4d()), tessellator, root);
        }

        if (buffer.isBuilding()) tessellator.draw();
    }

    private void recursiveRender(MeshNode self, Matrix4d parentTransform, Tessellator tessellator, int node) {
        Matrix4d localTransform = self.transform();
        Matrix4d globalTransform = parentTransform.mul(localTransform, new Matrix4d());
        Matrix4d animMatrix = new Matrix4d();

        if (manager.isPlayingAnimation()) {
            Vector3d translation = (manager.getCurrentTranslation(node) == null) ? new Vector3d() : manager.getCurrentTranslation(node);
            Quaterniond rotation = (manager.getCurrentRotation(node) == null) ? new Quaterniond() : manager.getCurrentRotation(node);
            Vector3d scale = (manager.getCurrentScale(node) == null) ? new Vector3d(1, 1, 1) : manager.getCurrentScale(node);

            animMatrix = animMatrix.translationRotateScale(translation, rotation, scale);
        }

        BufferBuilder buffer = tessellator.getBuffer();

        for (ProcessedPrimitive primitive : self.processedPrimitives()) {
            if (!mode.equals(primitive.mode())) {
                mode = primitive.mode();
                tessellator.draw();
                buffer = tessellator.getBuffer();
                buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            }

            for (Vertex vertex : primitive.vertices()) {
                Vector4d pos = new Vector4d(vertex.position(), 1);

                if (vertex.joints() != null && vertex.weights() != null) {
                    Vector4i j = vertex.joints();
                    Vector4d w = vertex.weights();
                    w.mul(0.0, w);

                    Matrix4d skinMatrix = jointMatrices.get(j.x).scale(w.x, new Matrix4d())
                            .add(jointMatrices.get(j.y).scale(w.y, new Matrix4d()))
                            .add(jointMatrices.get(j.z).scale(w.z, new Matrix4d()))
                            .add(jointMatrices.get(j.w).scale(w.w, new Matrix4d()));

                    Vec3d camWorldPos = MinecraftClient.getInstance().getCameraEntity().getPos();
                    Vector4d cameraPos = new Vector4d(camWorldPos.x, camWorldPos.y, camWorldPos.z, 1);
                    Vector4d worldPos = pos.mul(skinMatrix, new Vector4d());
                    cameraPos.mulProject(new Matrix4d(RenderSystem.getModelViewMatrix()), cameraPos)
                                    .mul(worldPos, cameraPos);

                    cameraPos.mul(new Matrix4d(RenderSystem.getProjectionMatrix()), pos);
                }

                buffer.vertex(new Matrix4f().set(globalTransform), (float) pos.x, (float) pos.y, (float) pos.z)
                        .texture((float) vertex.texCoord().coord().x, (float) vertex.texCoord().coord().y)
                        .color(0xFFFFFFFF)
                        .normal((float) vertex.normal().x, (float) vertex.normal().y, (float) vertex.normal().z)
                        .next();
            }
        }

        if (self.children() != null) {
            for (int child : self.children()) {
                recursiveRender(model.meshNodes()[child], globalTransform, tessellator, child);
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
            jointMatrices = new Int2ObjectArrayMap<>();
            safeToRender = true;
        } else {
            safeToRender = false;
            Ayanami.LOGGER.error("Model {} failed to load due to missing components", modelId);
        }
    }
}
