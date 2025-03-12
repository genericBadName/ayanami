package com.genericbadname.ayanami.client.processing;

import com.genericbadname.ayanami.client.data.ClientResourceStorage;
import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.gltf.properties.*;
import com.genericbadname.ayanami.client.gltf.properties.types.AccessorType;
import com.genericbadname.ayanami.client.processing.processed.*;
import com.genericbadname.ayanami.client.processing.processed.animation.NodeChannel;
import com.genericbadname.ayanami.client.processing.processed.animation.ProcessedAnimation;
import com.genericbadname.ayanami.client.processing.processed.animation.SamplerData;
import com.github.ooxi.jdatauri.DataUri;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.util.Identifier;
import org.joml.Matrix4d;
import org.joml.Vector4d;

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
    private ProcessedAnimation[] processedAnimations;
    private int[] roots;

    public AssetProcesser(Identifier modelLocation, GltfAsset model) {
        this.modelLocation = modelLocation;
        this.model = model;
    }

    public ProcessedAsset process() {
        if (model.scenes() == null || model.scene() == null || model.nodes() == null) return null;

        activeScene = model.scenes()[model.scene()];
        loadedBuffers = new Int2ObjectArrayMap<>();

        if (activeScene == null) return null;
        if (activeScene.nodes() == null) return null;

        processMeshes();
        processAnimations();

        return new ProcessedAsset(processedMeshes, roots, processedAnimations, model.nodes().length);
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

    private void processMeshes() {
        roots = Arrays.stream(activeScene.nodes()).mapToInt(Integer::intValue).toArray();
        processedMeshes = new ProcessedMesh[model.nodes().length];

        for (int i=0;i<processedMeshes.length;i++) {
            processChildMesh(model.nodes()[i], i);
        }
    }

    private void processChildMesh(Node self, int selfIndex) {
        if (self.mesh() == null) {
            processedMeshes[selfIndex] = new ProcessedMesh(self.children(), new ObjectArrayList<>(), new Matrix4d());
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
                    double[] components = new double[accessor.type().components];
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

    private void processAnimations() {
        if (model.animations() == null) return;

        Animation[] rawAnimations = model.animations();
        processedAnimations = new ProcessedAnimation[rawAnimations.length];

        for (int i=0;i< rawAnimations.length;i++) {
            processAnimation(rawAnimations[i], i);
        }
    }

    private void processAnimation(Animation animation, int animationNum) {
        Int2ObjectMap<NodeChannel> nodeChannels = new Int2ObjectArrayMap<>();
        ObjectList<SamplerData> samplers = new ObjectArrayList<>();

        // get raw sampler data
        for (Animation.Sampler sampler : animation.samplers()) {
            // get timestamp data
            Accessor timeAccessor = model.accessors()[sampler.input()];
            BufferView timeView = model.bufferViews()[timeAccessor.bufferView()];
            ByteBuffer timeBuffer = getBuffer(timeView.buffer()).position(timeView.byteOffset() + timeAccessor.byteOffset());
            double[] times = new double[timeAccessor.count()];

            for (int e = 0; e < timeAccessor.count(); e++) {
                times[e] = timeAccessor.componentType().converter.apply(timeBuffer).doubleValue();
            }

            // get output change data
            Accessor dataAccessor = model.accessors()[sampler.output()];
            BufferView dataView = model.bufferViews()[dataAccessor.bufferView()];
            ByteBuffer dataBuffer = getBuffer(dataView.buffer()).position(dataView.byteOffset() + dataAccessor.byteOffset());
            Vector4d[] vector4ds = new Vector4d[dataAccessor.count()]; // used to handle multiple types of vector data (TS or R)

            for (int e = 0; e < dataAccessor.count(); e++) {
                double[] components = new double[dataAccessor.type().components];
                int valueStart = dataBuffer.position();
                for (int c = 0; c < dataAccessor.type().components; c++) {
                    components[c] = dataAccessor.componentType().converter.apply(dataBuffer).doubleValue();
                }

                if (dataView.byteStride() != null) dataBuffer.position(valueStart + dataView.byteStride());
                if (dataAccessor.type().equals(AccessorType.VEC3)) {
                    vector4ds[e] = new Vector4d(components[0], components[1], components[2], 0);
                } else if (dataAccessor.type().equals(AccessorType.VEC4)){
                    vector4ds[e] = new Vector4d(components[0], components[1], components[2], components[3]);
                }
            }

            samplers.add(new SamplerData(times, vector4ds, sampler.interpolation()));
        }

        // process sampler data in context of channels
        for (Animation.Channel channel : animation.channels()) {
            int node = channel.target().node();
            if (!nodeChannels.containsKey(node)) nodeChannels.put(node, new NodeChannel());

            SamplerData samplerData = samplers.get(channel.sampler());
            switch (channel.target().path()) {
                case TRANSLATION -> nodeChannels.get(node).addTranslation(samplerData.times(), samplerData.vector4ds(), samplerData.interpolation());
                case ROTATION -> nodeChannels.get(node).addRotation(samplerData.times(), samplerData.vector4ds(), samplerData.interpolation());
                case SCALE -> nodeChannels.get(node).addScale(samplerData.times(), samplerData.vector4ds(), samplerData.interpolation());
                case WEIGHTS -> nodeChannels.get(node).addWeight(samplerData.times(), samplerData.vector4ds(), samplerData.interpolation());
            }
        }

        // calculate total animation runtime and sort
        double largest = 0;
        for (NodeChannel nc : nodeChannels.values()) {
            if (nc.getLastTime() > largest) largest = nc.getLastTime();
            nc.sortAll();
        }

        processedAnimations[animationNum] = new ProcessedAnimation(animation.name(), nodeChannels, largest);
    }
}
