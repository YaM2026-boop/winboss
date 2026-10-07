package com.winboss;

import com.winboss.entity.MicrosoftBossEntity;
import com.winboss.entity.WinCowEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<MicrosoftBossEntity> MICROSOFT_BOSS = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("winboss", "microsoft_boss"),
        FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, MicrosoftBossEntity::new)
            .dimensions(EntityDimensions.fixed(1.2f, 1.6f))
            .fireImmune()
            .build()
    );

    // 「旺牛」—— 简单友好生物,用于验证模组构建链路
    public static final EntityType<WinCowEntity> WIN_COW = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier("winboss", "win_cow"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, WinCowEntity::new)
            .dimensions(EntityDimensions.fixed(0.9f, 1.4f))
            .build()
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(MICROSOFT_BOSS, MicrosoftBossEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(WIN_COW, WinCowEntity.createAttributes());
    }
}
