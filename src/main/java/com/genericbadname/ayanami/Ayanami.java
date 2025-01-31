package com.genericbadname.ayanami;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ayanami implements ModInitializer {
    public static final String MOD_ID = "ayanami";
    public static final String RESOURCES_DIR = "aya";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

    }

    public static Identifier asID(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
