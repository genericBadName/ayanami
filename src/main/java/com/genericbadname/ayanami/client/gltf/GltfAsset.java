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
    Accessor[] accessors,
    ImageData[] images,
    Material[] materials,
    Sampler[] samplers,
    Texture[] textures
    // animations
    // cameras
    // skins

) {
    public static final Gson ASSET_GSON = new GsonBuilder()
            .registerTypeAdapter(Asset.class, Asset.deserializer())
            .registerTypeAdapter(Scene.class, Scene.deserializer())
            .registerTypeAdapter(Node.class, Node.deserializer())
            .registerTypeAdapter(Mesh.class, Mesh.deserializer())
            .registerTypeAdapter(Mesh.Primitive.class, Mesh.Primitive.deserializer())
            .registerTypeAdapter(BufferView.class, BufferView.deserializer())
            .registerTypeAdapter(Buffer.class, Buffer.deserializer())
            .registerTypeAdapter(Accessor.class, Accessor.deserializer())
            .registerTypeAdapter(Accessor.Sparse.class, Accessor.Sparse.deserializer())
            .registerTypeAdapter(Accessor.SparseIndices.class, Accessor.SparseIndices.deserializer())
            .registerTypeAdapter(Accessor.SparseValues.class, Accessor.SparseValues.deserializer())
            .registerTypeAdapter(Sampler.class, Sampler.deserializer())
            .registerTypeAdapter(Texture.class, Texture.deserializer())
            .registerTypeAdapter(TextureInfo.class, TextureInfo.deserializer())
            .registerTypeAdapter(Material.class, Material.deserializer())
            .registerTypeAdapter(Material.PBRMetallicRoughness.class, Material.PBRMetallicRoughness.deserializer())
            .registerTypeAdapter(Material.NormalTextureInfo.class, Material.NormalTextureInfo.deserializer())
            .registerTypeAdapter(Material.OcclusionTextureInfo.class, Material.OcclusionTextureInfo.deserializer())
            .registerTypeAdapter(ImageData.class, ImageData.deserializer())
            .create();
}
