package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.gltf.GltfAsset;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ClientResourceReloadListener implements SimpleResourceReloadListener<Int2ObjectArrayMap<GltfAsset>> {
    @Override
    public CompletableFuture<Int2ObjectArrayMap<GltfAsset>> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".gltf")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new OutputDataPair(entry.getKey(), loadAsset(entry.getKey(), entry.getValue()))))
                        .toList()
                        .stream().map(CompletableFuture::join)
                                .collect(Collectors.toMap((odp) -> odp.identifier.hashCode(), OutputDataPair::asset, (t1, t2) -> t1, Int2ObjectArrayMap::new)), executor);
    }

    private static GltfAsset loadAsset(Identifier id, Resource resource) {
        String assetString = loadAssetFile(id, resource);

        return (assetString != null) ? JsonHelper.deserialize(GltfAsset.ASSET_GSON, assetString, GltfAsset.class) : null;
    }

    private static String loadAssetFile(Identifier id, Resource resource) {
        try (InputStream stream = resource.getInputStream()) {
            return IOUtils.toString(stream, Charset.defaultCharset());
        } catch (IOException e) {
            Ayanami.LOGGER.error("Failed to read .gltf file {}", id);

            return null;
        }
    }

    @Override
    public CompletableFuture<Void> apply(Int2ObjectArrayMap<GltfAsset> modelAssets, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> ClientResourceStorage.modelAssets = modelAssets, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Ayanami.asID("ayanami_resources");
    }

    private record OutputDataPair(Identifier identifier, GltfAsset asset) {}
}
