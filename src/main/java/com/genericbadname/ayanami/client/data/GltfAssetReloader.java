package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.processing.AssetProcesser;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class GltfAssetReloader implements SimpleResourceReloadListener<Int2ObjectArrayMap<GltfAsset>> {
    @Override
    public CompletableFuture<Int2ObjectArrayMap<GltfAsset>> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".gltf")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new Pair<>(entry.getKey(), loadAsset(entry.getKey(), entry.getValue())), executor))
                        .map(CompletableFuture::join)
                        .filter(odp -> odp.getRight() != null)
                        .collect(Collectors.toMap((odp) -> Ayanami.trim(odp.getLeft(), ".gltf").hashCode(), Pair::getRight, (t1, t2) -> t1, Int2ObjectArrayMap::new)), executor);
    }

    private static GltfAsset loadAsset(Identifier id, Resource resource) {
        try (InputStream stream = resource.getInputStream()) {
            JsonReader reader = new JsonReader(new InputStreamReader(stream));

            GltfAsset output = GltfAsset.ASSET_GSON.fromJson(reader, GltfAsset.class);
            reader.close();

            return output;
        } catch (IOException e) {
            Ayanami.LOGGER.error("Failed to read .gltf file {}", id);

            return null;
        }
    }

    @Override
    public CompletableFuture<Void> apply(Int2ObjectArrayMap<GltfAsset> modelAssets, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            Ayanami.LOGGER.info("Loaded {} model assets", modelAssets.size());

            for (Int2ObjectMap.Entry<GltfAsset> entry : modelAssets.int2ObjectEntrySet()) {
                ProcessedAsset asset = new AssetProcesser(entry.getValue()).process();
                if (asset != null) ClientResourceStorage.modelAssets.put(entry.getIntKey(), asset);
            }

            Ayanami.LOGGER.info("Processed {} model assets", modelAssets.size());
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Ayanami.asID("gltf_resources");
    }
}
