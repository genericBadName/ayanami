package com.genericbadname.ayanami.client.processing.processed;

import com.genericbadname.ayanami.client.gltf.properties.types.PrimitiveMode;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;

/**
 * Fully processed, ready-to-render asset, only containing necessary data.
 */
public class ProcessedAsset {
    private final ProcessedMesh[] meshes;
    private final int[] roots;
    private final boolean staticMesh;

    public ProcessedAsset(ProcessedMesh[] meshes, int[] roots, boolean staticMesh) {
        this.meshes = meshes;
        this.roots = roots;
        this.staticMesh = staticMesh;
    }

    private PrimitiveMode mode = PrimitiveMode.TRIANGLES;
    public void renderFromRoots(Matrix4f baseTransform) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        for (int root : roots) {
            recursiveRender(meshes[root], baseTransform, tessellator);
        }

        if (buffer.isBuilding()) tessellator.draw();
    }

    private void recursiveRender(ProcessedMesh self, Matrix4f parentTransform, Tessellator tessellator) {
        Matrix4f transform = parentTransform.mulAffine(new Matrix4f(self.transform()), new Matrix4f());
        BufferBuilder buffer = tessellator.getBuffer();

        for (ProcessedPrimitive primitive : self.processedPrimitives()) {
            if (!mode.equals(primitive.mode())) {
                mode = primitive.mode();
                tessellator.draw();
                buffer = tessellator.getBuffer();
                buffer.begin(mode.drawMode, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
            }

            for (Vertex vertex : primitive.vertices()) {
                buffer.vertex(transform, (float) vertex.position().x, (float) vertex.position().y, (float) vertex.position().z)
                        .texture((float) vertex.texCoord().coord().x, (float) vertex.texCoord().coord().y)
                        .color(0xFFFFFFFF)
                        .normal((float) vertex.normal().x, (float) vertex.normal().y, (float) vertex.normal().z)
                        .next();
            }
        }

        if (self.children() != null) {
            for (int child : self.children()) {
                recursiveRender(meshes[child], transform, tessellator);
            }
        }
    }
}
