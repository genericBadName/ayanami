package com.genericbadname.ayanami.client.renderer.test;

import com.genericbadname.ayanami.Ayanami;
import com.genericbadname.ayanami.client.renderer.ReiItemRenderer;

public class ChainsawRenderer extends ReiItemRenderer {
    public ChainsawRenderer() {
        super(
                Ayanami.asID("rei/chainsaw"),
                Ayanami.asID("textures/rei/chainsaw.png")
        );
    }
}
