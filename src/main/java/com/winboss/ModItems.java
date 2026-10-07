package com.winboss;

import com.winboss.item.WinSwordItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
    // 铜火把（仿新版铜火把）
    public static final Item COPPER_TORCH = new BlockItem(ModBlocks.COPPER_TORCH, new Item.Settings());
    // 微软遗物（召唤台核心，也是 Boss 的形态本体）
    public static final Item WINDOWS_RELIC = new BlockItem(ModBlocks.WINDOWS_RELIC, new Item.Settings());
    // 微软剑（整活武器）
    public static final Item MICROSOFT_SWORD = new WinSwordItem();
    // 硬核徽章（掉落材料）
    public static final Item HARD_MEDAL = new Item(new Item.Settings());
    // Boss 刷怪蛋：四色
    public static final Item MICROSOFT_SPAWN_EGG = new SpawnEggItem(
        ModEntities.MICROSOFT_BOSS, 0xF25022, 0xFFB900, new Item.Settings());
    // 「旺牛」刷怪蛋
    public static final Item WIN_COW_SPAWN_EGG = new SpawnEggItem(
        ModEntities.WIN_COW, 0x8a6f3c, 0xcfc39a, new Item.Settings());

    public static final ItemGroup WINBOSS_GROUP = FabricItemGroup.builder()
        .displayName(Text.translatable("itemGroup.winboss"))
        .icon(() -> new ItemStack(WINDOWS_RELIC))
        .build();

    public static final RegistryKey<ItemGroup> WINBOSS_GROUP_KEY =
        RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("winboss", "main"));

    public static void init() {
        Registry.register(Registries.ITEM, new Identifier("winboss", "copper_torch"), COPPER_TORCH);
        Registry.register(Registries.ITEM, new Identifier("winboss", "windows_relic"), WINDOWS_RELIC);
        Registry.register(Registries.ITEM, new Identifier("winboss", "microsoft_sword"), MICROSOFT_SWORD);
        Registry.register(Registries.ITEM, new Identifier("winboss", "hard_medal"), HARD_MEDAL);
        Registry.register(Registries.ITEM, new Identifier("winboss", "microsoft_spawn_egg"), MICROSOFT_SPAWN_EGG);
        Registry.register(Registries.ITEM, new Identifier("winboss", "win_cow_spawn_egg"), WIN_COW_SPAWN_EGG);
        Registry.register(Registries.ITEM_GROUP, WINBOSS_GROUP_KEY, WINBOSS_GROUP);

        ItemGroupEvents.modifyEntriesEvent(WINBOSS_GROUP_KEY).register(entries -> {
            entries.add(COPPER_TORCH);
            entries.add(WINDOWS_RELIC);
            entries.add(MICROSOFT_SWORD);
            entries.add(HARD_MEDAL);
            entries.add(MICROSOFT_SPAWN_EGG);
            entries.add(WIN_COW_SPAWN_EGG);
        });
    }
}
