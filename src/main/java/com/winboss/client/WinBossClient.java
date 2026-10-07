package com.winboss.client;

import com.winboss.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.CowEntityRenderer;

public class WinBossClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.MICROSOFT_BOSS,
            MicrosoftBossRenderer::new);
        EntityRendererRegistry.register(ModEntities.WIN_COW,
            CowEntityRenderer::new);
    }
}
