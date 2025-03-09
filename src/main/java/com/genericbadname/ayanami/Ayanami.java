package com.genericbadname.ayanami;

import com.genericbadname.ayanami.entity.RobloxianEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ayanami implements ModInitializer {
    public static final String MOD_ID = "ayanami";
    public static final String RESOURCES_DIR = "rei";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final Item CHAINSAW = Registry.register(Registries.ITEM, asID("chainsaw"), new Item(new Item.Settings()));
    public static final EntityType<RobloxianEntity> ROBLOXIAN = Registry.register(Registries.ENTITY_TYPE, asID("robloxian"),
            EntityType.Builder.create(RobloxianEntity::new, SpawnGroup.MISC)
                    .setDimensions(2F, 2F)
                    .build("robloxian")
    );

    @Override
    public void onInitialize() {
        FabricDefaultAttributeRegistry.register(ROBLOXIAN, RobloxianEntity.createMobAttributes());
    }

    public static Identifier asID(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Identifier trim(Identifier id, String end) {
        String str = id.getPath().replace(end, "");

        return new Identifier(id.getNamespace(), str);
    }
}
