package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.gltf.properties.*;
import com.genericbadname.ayanami.client.processing.processed.ProcessedPrimitive;
import com.genericbadname.ayanami.client.processing.processed.Vertex;
import com.github.ooxi.jdatauri.DataUri;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;

public class AssetProcesser {
    private final GltfAsset model;
    private Scene activeScene;
    private Int2ObjectMap<ByteBuffer> loadedBuffers;

    public AssetProcesser(GltfAsset model) {
        this.model = model;
    }

    public void process() {
        if (model.scenes() == null) return;
        if (model.scene() == null) return;

        activeScene = model.scenes()[model.scene()];
        loadedBuffers = new Int2ObjectArrayMap<>();
        traverseRootNodes();
    }

    private ByteBuffer getBuffer(int index) {
        if (!loadedBuffers.containsKey(index)) {
            byte[] data = DataUri.parse(model.buffers()[index].uri(), Charset.defaultCharset()).getData();
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length).order(ByteOrder.LITTLE_ENDIAN);
            loadedBuffers.put(index, buffer);
        }

        return loadedBuffers.get(index);
    }

    private void traverseRootNodes() {
        if (activeScene == null) return;
        if (activeScene.nodes() == null) return;

        for (int i : activeScene.nodes()) {
            traverseChildren(model.nodes()[i], null);
        }
    }

    private void traverseChildren(Node self, Node parent) {
        processMeshes(self);

        if (self.children() != null) {
            for (int child : self.children()) { // I don't like this.
                traverseChildren(model.nodes()[child], self);
            }
        }
    }

    private void processMeshes(Node self) {
        if (self.mesh() == null) return;
        Mesh mesh = model.meshes()[self.mesh()];
        List<ProcessedPrimitive> processedPrimitives = new ArrayList<>();

        for (Mesh.Primitive primitive : mesh.primitives()) {
            MeshAttributes processedAttributes = MeshAttributes.create();

            // process attributes
            for (Map.Entry<String, Integer> attribute : primitive.attributes().entrySet()) {
                // access data from the buffer
                Accessor accessor = model.accessors()[attribute.getValue()];
                BufferView view = model.bufferViews()[accessor.bufferView()];
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset());

                // add processed attributes
                for (int e=0;e<accessor.count();e++) {
                    double[] components = new double[accessor.count()];
                    int initialPosition = viewBuffer.position();
                    for (int c = 0; c<accessor.type().components; c++) {
                        components[c] = accessor.componentType().converter.apply(viewBuffer).doubleValue();
                        if (view.byteStride() != null) viewBuffer.position(initialPosition + view.byteStride());
                    }
                    processedAttributes.add(attribute.getKey(), components);
                }
            }

            // process indices
            if (primitive.indices() != null) {
                // access indices, to determine which of the process vertices to use
                Accessor accessor = model.accessors()[primitive.indices()];
                BufferView view = model.bufferViews()[accessor.bufferView()];
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset());

                List<Vertex> vertices = new ArrayList<>();
                for (int i=0;i<accessor.count();i++) {
                    int index = accessor.componentType().converter.apply(viewBuffer).intValue();
                    vertices.add(new Vertex(processedAttributes.positions().get(index), processedAttributes.normals().get(index), processedAttributes.texcoords().get(index)));
                }

                processedPrimitives.add(new ProcessedPrimitive(vertices, primitive.mode()));
            }
        }
    }
}
