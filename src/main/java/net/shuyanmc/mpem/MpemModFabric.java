package net.shuyanmc.mpem;

import net.fabricmc.api.ModInitializer;

public class MpemModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        new MpemMod();
    }
}
