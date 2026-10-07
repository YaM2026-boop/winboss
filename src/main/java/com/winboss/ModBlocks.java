package com.winboss;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ModBlocks {
    // 铜火把：绿光，仿 1.21 铜火把
    public static final Block COPPER_TORCH = new TorchBlock(
        AbstractBlock.Settings.create().noCollision().breakInstantly()
            .luminance(state -> 13).sounds(BlockSoundGroup.WOOD),
        ParticleTypes.FLAME) {
        @Override
        public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
            // 绿色火焰粒子（铜火把特征）
            world.addParticle(ParticleTypes.WAX_ON,
                pos.getX() + 0.5, pos.getY() + 0.65, pos.getZ() + 0.5,
                0.02 * (random.nextDouble() - 0.5), 0.04, 0.02 * (random.nextDouble() - 0.5));
        }
    };

    // 微软遗物：四色方块（微软 Logo 展开体）
    public static final Block WINDOWS_RELIC = new Block(
        AbstractBlock.Settings.create().strength(2.0f, 6.0f).luminance(state -> 11));

    public static void init() {
        Registry.register(Registries.BLOCK, new Identifier("winboss", "copper_torch"), COPPER_TORCH);
        Registry.register(Registries.BLOCK, new Identifier("winboss", "windows_relic"), WINDOWS_RELIC);
    }
}
