package com.winboss.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.world.World;

/**
 * 「旺牛」—— 一种简单的友好生物。
 * 继承 vanilla 牛复用其模型、属性与 AI,便于验证模组构建链路。
 */
public class WinCowEntity extends CowEntity {
    public WinCowEntity(EntityType<? extends CowEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return CowEntity.createCowAttributes();
    }
}
