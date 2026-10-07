package com.winboss;

import net.fabricmc.api.ModInitializer;

public class WinBossMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        TorchSummon.init();
    }
}