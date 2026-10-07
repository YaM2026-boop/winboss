package com.winboss.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;

/**
 * 微软剑：整活武器。
 * 命中时有概率给目标施加「蓝屏冻结」（缓慢+失明）。
 */
public class WinSwordItem extends SwordItem {
    public WinSwordItem() {
        super(ToolMaterials.DIAMOND, 4, -2.0f, new Settings());
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean hit = super.postHit(stack, target, attacker);
        if (hit && !target.getWorld().isClient && target.getWorld().random.nextFloat() < 0.3f) {
            // 蓝屏冻结
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 2));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
        }
        return hit;
    }
}
