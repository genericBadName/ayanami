package com.genericbadname.ayanami.client.data;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.display.DisplaySettings;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class DisplaySettingsReloader implements SimpleResourceReloadListener<Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>>> {

    @Override
    public CompletableFuture<Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>>> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture
                .supplyAsync(() -> resourceManager.findResources(Ayanami.RESOURCES_DIR, (fileId) -> fileId.toString().endsWith(".json")), executor)
                .thenApplyAsync(resources -> resources.entrySet().stream()
                        .map(entry -> CompletableFuture.supplyAsync(() -> new Pair<>(entry.getKey(), loadDisplaySettings(entry.getKey(), entry.getValue())), executor))
                        .map(CompletableFuture::join)
                        .filter(odp -> odp.getRight() != null)
                        .collect(Collectors.toMap((odp) -> Ayanami.trim(odp.getLeft(), ".json").hashCode(), Pair::getRight, (t1, t2) -> t1, Int2ObjectArrayMap::new)), executor);
    }

    private static EnumMap<ModelTransformationMode, Matrix4f> loadDisplaySettings(Identifier id, Resource resource) {
        try (InputStream stream = resource.getInputStream()) {
            JsonReader reader = new JsonReader(new InputStreamReader(stream));

            EnumMap<ModelTransformationMode, Matrix4f> output = DisplaySettings.DISPLAY_SETTINGS_GSON.fromJson(reader, DisplaySettings.ENUM_MAP_TYPE);
            reader.close();

            return output;
        } catch (IOException e) {
            Ayanami.LOGGER.error("Failed to read .gltf file {}", id);

            return null;
        }
    }

    @Override
    public CompletableFuture<Void> apply(Int2ObjectArrayMap<EnumMap<ModelTransformationMode, Matrix4f>> settings, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            Ayanami.LOGGER.info("Loaded {} display settings", settings.size());
            ClientResourceStorage.displaySettings.putAll(settings);
        }, executor);
    }

    @Override
    public Identifier getFabricId() {
        return Ayanami.asID("display_settings_resources");
    }
}
