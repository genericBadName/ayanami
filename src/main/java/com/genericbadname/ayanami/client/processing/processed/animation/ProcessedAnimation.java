package com.genericbadname.ayanami.client.processing.processed.animation;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public record ProcessedAnimation(
        String name,
        Int2ObjectMap<NodeChannel> nodeChannels,
        double totalTime
) {
}
