package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.MatrixUtil;
import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.gltf.properties.*;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.genericbadname.ayanami.client.processing.processed.ProcessedMesh;
import com.genericbadname.ayanami.client.processing.processed.ProcessedPrimitive;
import com.genericbadname.ayanami.client.processing.processed.Vertex;
import com.github.ooxi.jdatauri.DataUri;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.joml.Matrix4d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;

public class AssetProcesser {
    private final GltfAsset model;
    private Scene activeScene;
    private Int2ObjectMap<ByteBuffer> loadedBuffers;
    private ProcessedMesh[] processedMeshes;
    private int[] roots;

    public AssetProcesser(GltfAsset model) {
        this.model = model;
    }

    public ProcessedAsset process() {
        if (model.scenes() == null) return null;
        if (model.scene() == null) return null;

        activeScene = model.scenes()[model.scene()];
        loadedBuffers = new Int2ObjectArrayMap<>();
        processAll();

        return new ProcessedAsset(processedMeshes, roots, true);
    }

    private ByteBuffer getBuffer(int index) {
        if (!loadedBuffers.containsKey(index)) {
            byte[] data = DataUri.parse(model.buffers()[index].uri(), Charset.defaultCharset()).getData();
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

            loadedBuffers.put(index, buffer);
        }

        return loadedBuffers.get(index);
    }

    private void processAll() {
        if (activeScene == null) return;
        if (activeScene.nodes() == null) return;
        if (model.nodes() == null) return;

        roots = Arrays.stream(activeScene.nodes()).mapToInt(Integer::intValue).toArray();
        processedMeshes = new ProcessedMesh[model.nodes().length];

        for (int i=0;i<processedMeshes.length;i++) {
            processMesh(model.nodes()[i], i);
        }
    }

    private void processMesh(Node self, int selfIndex) {
        if (self.mesh() == null) {
            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), new ObjectArrayList<>(), MatrixUtil.identity());
            return;
        }

        Mesh mesh = model.meshes()[self.mesh()];
        ObjectList<ProcessedPrimitive> processedPrimitives = new ObjectArrayList<>();

        for (Mesh.Primitive primitive : mesh.primitives()) {
            MeshAttributes processedAttributes = MeshAttributes.create();

            // process attributes
            for (Map.Entry<String, Integer> attribute : primitive.attributes().entrySet()) {
                // access data from the buffer
                Accessor accessor = model.accessors()[attribute.getValue()];
                BufferView view = model.bufferViews()[accessor.bufferView()];
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset());

                // add processed attributes
                for (int e = 0; e < accessor.count(); e++) {
                    double[] components = new double[accessor.count()];
                    int valueStart = viewBuffer.position();
                    for (int c = 0; c < accessor.type().components; c++) {
                        components[c] = accessor.componentType().converter.apply(viewBuffer).doubleValue();
                    }

                    if (view.byteStride() != null) viewBuffer.position(valueStart + view.byteStride());
                    processedAttributes.add(attribute.getKey(), components);
                }
            }

            // TODO: fix vertices not rendering in the correct order
            // process indices
            if (primitive.indices() != null) {
                // access indices, to determine which of the process vertices to use
                Accessor accessor = model.accessors()[primitive.indices()];
                BufferView view = model.bufferViews()[accessor.bufferView()];
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset());

                List<Vertex> vertices = new ArrayList<>();
                for (int i = 0; i < accessor.count(); i++) {
                    int index = accessor.componentType().converter.apply(viewBuffer).intValue();
                    vertices.add(new Vertex(processedAttributes.positions().get(index), processedAttributes.normals().get(index), processedAttributes.texcoords().get(index)));
                }

                processedPrimitives.add(new ProcessedPrimitive(vertices, primitive.mode()));
            } else {
                List<Vertex> vertices = new ArrayList<>(); // TODO: handle possible misformatting from unequal list sizes
                for (int i = 0; i < processedAttributes.positions().size(); i++) {
                    vertices.add(new Vertex(processedAttributes.positions().get(i), processedAttributes.normals().get(i), processedAttributes.texcoords().get(i)));
                }

                processedPrimitives.add(new ProcessedPrimitive(vertices, primitive.mode()));
            }
        }

        // add processed mesh
        if (self.matrix() != null) {
            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), processedPrimitives, self.matrix());
        } else if (self.translation() != null || self.rotation() != null || self.scale() != null) {
            Matrix4d translation = (self.translation() != null) ? MatrixUtil.translation(self.translation()) : MatrixUtil.identity();
            Matrix4d rotation = (self.rotation() != null) ? MatrixUtil.rotation(self.rotation()) : MatrixUtil.identity();
            Matrix4d scale = (self.scale() != null) ? MatrixUtil.scale(self.scale()) : MatrixUtil.identity();
            Matrix4d transform = translation.mul(rotation).mul(scale);

            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), processedPrimitives, transform);
        } else {
            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), processedPrimitives, MatrixUtil.identity());
        }
    }
}
