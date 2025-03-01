package com.genericbadname.ayanami;

import net.fabricmc.api.ModInitializer;
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

    @Override
    public void onInitialize() {

    }

    public static Identifier asID(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Identifier trim(Identifier id, String end) {
        String str = id.getPath().replace(end, "");

        return new Identifier(id.getNamespace(), str);
    }
}
