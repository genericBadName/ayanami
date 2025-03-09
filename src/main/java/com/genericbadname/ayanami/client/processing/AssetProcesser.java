package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.client.data.ClientResourceStorage;
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
import net.minecraft.util.Identifier;
import org.joml.Matrix4d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.*;

public class AssetProcesser {
    private final Identifier modelLocation;
    private final GltfAsset model;
    private Scene activeScene;
    private Int2ObjectMap<ByteBuffer> loadedBuffers;
    private ProcessedMesh[] processedMeshes;
    private int[] roots;

    private static final Matrix4d IDENTITY = new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

    public AssetProcesser(Identifier modelLocation, GltfAsset model) {
        this.modelLocation = modelLocation;
        this.model = model;
    }

    public ProcessedAsset process() {
        if (model.scenes() == null) return null;
        if (model.scene() == null) return null;

        activeScene = model.scenes()[model.scene()];
        loadedBuffers = new Int2ObjectArrayMap<>();
        processAll();

        return new ProcessedAsset(processedMeshes, roots);
    }

    private ByteBuffer getBuffer(int index) {
        if (!loadedBuffers.containsKey(index)) {
            ByteBuffer buffer = null;
            String uri = model.buffers()[index].uri();
            
            if (uri.startsWith("data:application/octet-stream")) {
                buffer = ByteBuffer.wrap(DataUri.parse(uri, Charset.defaultCharset()).getData()).order(ByteOrder.LITTLE_ENDIAN);
            } else if (uri.endsWith(".bin")) {
                String path = modelLocation.getPath();
                buffer = ClientResourceStorage.getExternalBuffer(new Identifier(modelLocation.getNamespace(), path.substring(0, path.lastIndexOf("/") + 1) + uri));
            }

            if (buffer == null) throw new RuntimeException("Tried loading "+modelLocation+" but a loaded buffer was not found!"); // just in case something weird happens

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
            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), new ObjectArrayList<>(), IDENTITY);
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
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset() + accessor.byteOffset());

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

            // process indices
            if (primitive.indices() != null) {
                // access indices, to determine which of the process vertices to use
                Accessor accessor = model.accessors()[primitive.indices()];
                BufferView view = model.bufferViews()[accessor.bufferView()];
                ByteBuffer viewBuffer = getBuffer(view.buffer()).position(view.byteOffset() + accessor.byteOffset());

                List<Vertex> vertices = new ArrayList<>();
                for (int i = 0; i < accessor.count(); i++) {
                    int index = accessor.componentType().converter.apply(viewBuffer).intValue();
                    vertices.add(new Vertex(processedAttributes.positions().get(index), processedAttributes.normals().get(index), processedAttributes.texcoords().get(index)));
                }

                processedPrimitives.add(new ProcessedPrimitive(vertices, primitive.mode()));
            } else {
                List<Vertex> vertices = new ArrayList<>();
                for (int i = 0; i < processedAttributes.positions().size(); i++) {
                    vertices.add(new Vertex(processedAttributes.positions().get(i), processedAttributes.normals().get(i), processedAttributes.texcoords().get(i)));
                }

                processedPrimitives.add(new ProcessedPrimitive(vertices, primitive.mode()));
            }
        }

        // add processed mesh
        Matrix4d transform = new Matrix4d().translationRotateScale(self.translation(), self.rotation(), self.scale());

        processedMeshes[selfIndex] = new ProcessedMesh(self.children(), processedPrimitives, transform);
    }
}
