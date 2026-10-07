package com.winboss;

import com.winboss.entity.MicrosoftBossEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 召唤仪式：四色火把按微软 Logo 2×2 摆放。
 *
 * 微软 Logo 布局（面向北视角）：
 *   ┌─────┬─────┐
 *   │  红  │  绿  │   红 = 红石火把
 *   ├─────┼─────┤   绿 = 铜火把(模组)
 *   │  蓝  │  黄  │   蓝 = 灵魂火把
 *   └─────┴─────┘   黄 = 普通火把
 *
 * 摆好后右键任意一把火把即召唤「微软」。
 */
public class TorchSummon {

    /** 四个角期望的火把物品（与位置顺序对应：左上/右上/左下/右下，宽容匹配） */
    private static final Item[] REQUIRED = {
        Items.REDSTONE_TORCH, ModItems.COPPER_TORCH, Items.SOUL_TORCH, Items.TORCH
    };

    public static void init() {
        UseBlockCallback.EVENT.register(TorchSummon::onUseBlock);
    }

    private static ActionResult onUseBlock(PlayerEntity player, World world,
                                           Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        Item clicked = world.getBlockState(hit.getBlockPos()).getBlock().asItem();
        if (!isTorch(clicked)) return ActionResult.PASS;

        BlockPos base = hit.getBlockPos();
        // 2x2 中 base 可能是任意一个角：枚举 4 个候选原点（左上角）
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                BlockPos origin = base.add(dx, 0, dz);
                if (tryMatch(world, origin)) {
                    summon(world, player, origin);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
    }

    /** 检测以 origin 为左上角的 2x2 是否正好是四种颜色火把（宽容：位置不强制配色顺序） */
    private static boolean tryMatch(World world, BlockPos origin) {
        BlockPos[] positions = {
            origin, origin.add(1, 0, 0), origin.add(0, 0, 1), origin.add(1, 0, 1)
        };
        boolean[] used = new boolean[4];
        for (BlockPos pos : positions) {
            Item item = world.getBlockState(pos).getBlock().asItem();
            if (!isTorch(item)) return false;
            boolean matched = false;
            for (int r = 0; r < 4; r++) {
                if (!used[r] && item == REQUIRED[r]) {
                    used[r] = true;
                    matched = true;
                    break;
                }
            }
            if (!matched) return false; // 四种都集齐了但多了重复 → 失败
        }
        return true;
    }

    private static void summon(World world, PlayerEntity player, BlockPos center) {
        // 移除四把火把
        world.removeBlock(center, false);
        world.removeBlock(center.add(1, 0, 0), false);
        world.removeBlock(center.add(0, 0, 1), false);
        world.removeBlock(center.add(1, 0, 1), false);

        // 生成 Boss（悬浮在中心上方）
        ServerWorld sw = (ServerWorld) world;
        MicrosoftBossEntity boss = new MicrosoftBossEntity(ModEntities.MICROSOFT_BOSS, sw);
        if (boss != null) {
            boss.refreshPositionAndAngles(
                center.getX() + 0.5, center.getY() + 1.2, center.getZ() + 0.5, 0, 0);
            sw.spawnEntity(boss);
        }
        // 仪式音效
        sw.playSound(null, center, SoundEvents.BLOCK_BEACON_ACTIVATE,
            SoundCategory.HOSTILE, 1.0f, 1.0f);
        sw.playSound(null, center, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
            SoundCategory.HOSTILE, 1.0f, 1.2f);

        // 极光粒子
        for (int i = 0; i < 60; i++) {
            sw.addParticle(ParticleTypes.EXPLOSION_EMITTER,
                center.getX() + 0.5, center.getY() + 1.5, center.getZ() + 0.5, 0, 0.1, 0);
        }

        // 微软式中文
        if (player != null) {
            player.sendMessage(Text.literal("Microsoft 微软 正在启动…"), false);
        }
    }

    private static boolean isTorch(Item item) {
        return item == Items.REDSTONE_TORCH || item == Items.TORCH || item == Items.SOUL_TORCH
            || item == ModItems.COPPER_TORCH;
    }
}
