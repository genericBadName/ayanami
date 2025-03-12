package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.display.DisplaySettings;
import com.genericbadname.ayanami.client.gltf.GltfAsset;
import com.genericbadname.ayanami.client.processing.AssetProcesser;
import com.genericbadname.ayanami.client.processing.processed.ProcessedAsset;
import com.genericbadname.ayanami.client.renderer.ReiEntityRenderer;
import com.genericbadname.ayanami.client.renderer.ReiItemRenderer;
import com.genericbadname.ayanami.mixin.EntityRendererAccessor;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ModelResourcesReloader implements SimpleResourceReloadListener<ModelResources> {

    @Override
    public CompletableFuture<ModelResources> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        CompletableFuture<Object2ObjectArrayMap<Identifier, GltfAsset>> assetLoader = CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".gltf")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new Pair<>(entry.getKey(), loadAsset(entry.getKey(), entry.getValue())), executor))
                        .map(CompletableFuture::join)
                        .filter(pair -> pair.getRight() != null)
                        .collect(Collectors.toMap((pair) -> Ayanami.trim(pair.getLeft(), ".gltf"), Pair::getRight, (t1, t2) -> t1, Object2ObjectArrayMap::new)), executor);

        CompletableFuture<Int2ObjectArrayMap<ByteBuffer>> externalBufferLoader = CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".bin")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new Pair<>(entry.getKey(), loadExternalBuffer(entry.getKey(), entry.getValue())), executor))
                        .map(CompletableFuture::join)
                        .filter(pair -> pair.getRight() != null)
                        .collect(Collectors.toMap((pair) -> pair.getLeft().hashCode(), Pair::getRight, (t1, t2) -> t1, Int2ObjectArrayMap::new)), executor);

        CompletableFuture<Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>>> displaySettingsLoader = CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".json")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new Pair<>(entry.getKey(), loadDisplaySettings(entry.getKey(), entry.getValue())), executor))
                        .map(CompletableFuture::join)
                        .filter(pair -> pair.getRight() != null)
                        .collect(Collectors.toMap((pair) -> Ayanami.trim(pair.getLeft(), ".json").hashCode(), Pair::getRight, (t1, t2) -> t1, Int2ObjectArrayMap::new)), executor);

        // load all resources (.gltf, .bin, .json) at the same timestamp, to guarantee that the application process
        // doesn't accidentally try to read a nonexistent buffer
        return CompletableFuture.allOf(assetLoader, externalBufferLoader, displaySettingsLoader)
                .thenApplyAsync(v -> {
                    Object2ObjectArrayMap<Identifier, GltfAsset> assets = assetLoader.join();
                    Int2ObjectArrayMap<ByteBuffer> externalBuffers = externalBufferLoader.join();
                    Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>> displaySettings = displaySettingsLoader.join();

                    return new ModelResources(assets, externalBuffers, displaySettings);
                }, executor);
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

    private static ByteBuffer loadExternalBuffer(Identifier id, Resource resource) {
        try (InputStream stream = resource.getInputStream()) {
            ByteBuffer buffer = ByteBuffer.wrap(stream.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
            stream.close();

            return buffer;
        } catch (IOException e) {
            Ayanami.LOGGER.error("Failed to read .bin file {}", id);

            return null;
        }
    }

    private static EnumMap<ModelTransformationMode, Matrix4f> loadDisplaySettings(Identifier id, Resource resource) {
        try (InputStream stream = resource.getInputStream()) {
            JsonReader reader = new JsonReader(new InputStreamReader(stream));

            EnumMap<ModelTransformationMode, Matrix4f> output = DisplaySettings.DISPLAY_SETTINGS_GSON.fromJson(reader, DisplaySettings.ENUM_MAP_TYPE);
            reader.close();

            return output;
        } catch (IOException e) {
            Ayanami.LOGGER.error("Failed to read .json file {}", id);

            return null;
        }
    }

    @Override
    public CompletableFuture<Void> apply(ModelResources modelResources, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        // setup async asset processors
        Object2ObjectArrayMap<Identifier, GltfAsset> assets = modelResources.assets();
        List<CompletableFuture<Void>> processors = new ArrayList<>();

        for (Object2ObjectMap.Entry<Identifier, GltfAsset> entry : assets.object2ObjectEntrySet()) {
            processors.add(
                    CompletableFuture.runAsync(() -> {
                        ProcessedAsset asset = new AssetProcesser(entry.getKey(), entry.getValue()).process();
                        if (asset != null) ClientResourceStorage.modelAssets.put(entry.getKey().hashCode(), asset);
                        }, executor
                    )
            );
        }

        // synchronously apply resources
        Ayanami.LOGGER.info("Loaded {} external buffers", modelResources.externalBuffers().size());
        ClientResourceStorage.externalBuffers.clear();
        ClientResourceStorage.externalBuffers.putAll(modelResources.externalBuffers());

        Ayanami.LOGGER.info("Loaded {} model assets", assets.size());
        ClientResourceStorage.modelAssets.clear();

        Ayanami.LOGGER.info("Loaded {} display settings", modelResources.displaySettings().size());
        ClientResourceStorage.displaySettings.clear();
        ClientResourceStorage.displaySettings.putAll(modelResources.displaySettings());

        // asynchronously process assets
        return CompletableFuture.allOf(processors.toArray(new CompletableFuture[0])).thenRunAsync(() -> {
            Ayanami.LOGGER.info("Processed {} model assets", assets.size());

            // reload item renderers
            for (ItemConvertible item : ClientResourceStorage.itemRenderers) {
                Object renderer = BuiltinItemRendererRegistry.INSTANCE.get(item);
                if (renderer instanceof ReiItemRenderer) {
                    ((ReiItemRenderer) renderer).reload();
                }
            }
            Ayanami.LOGGER.info("Reloaded {} item renderers", ClientResourceStorage.itemRenderers.size());

            // reload entity renderers
            Set<EntityType<?>> reiRenderers = ((EntityRendererAccessor)MinecraftClient.getInstance().getEntityRenderDispatcher())
                    .getRenderers()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() instanceof ReiEntityRenderer<?>)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            for (EntityType<?> type : reiRenderers) {
                // atrocious. i care not.
                ((ReiEntityRenderer<?>)((EntityRendererAccessor)MinecraftClient.getInstance().getEntityRenderDispatcher()).getRenderers().get(type)).reload();
            }
            Ayanami.LOGGER.info("Reloaded {} entity renderers", reiRenderers.size());

        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Ayanami.asID("model_resources");
    }
}
