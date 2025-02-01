package com.genericbadname.ayanami.client.gltf;

import com.genericbadname.ayanami.client.gltf.properties.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public record GltfAsset(
    Asset asset,
    String[] extensionsUsed,
    String[] extensionsRequired,

    Scene[] scenes,
    Integer scene,
    Node[] nodes,
    Mesh[] meshes,

    BufferView[] bufferViews,
    Buffer[] buffers,

    Accessor[] accessors
    // animations
    // cameras
    // images
    // materials
    // samplers
    // skins
    // textures

) {
    public static final Gson ASSET_GSON = new GsonBuilder()
            .registerTypeAdapter(Asset.class, Asset.deserializer())
            .registerTypeAdapter(Scene.class, Scene.deserializer())
            .registerTypeAdapter(Node.class, Node.deserializer())
            .registerTypeAdapter(Mesh.class, Mesh.deserializer())
            .registerTypeAdapter(BufferView.class, BufferView.deserializer())
            .registerTypeAdapter(Buffer.class, Buffer.deserializer())
            .registerTypeAdapter(Accessor.class, Accessor.deserializer())
            .create();
}
