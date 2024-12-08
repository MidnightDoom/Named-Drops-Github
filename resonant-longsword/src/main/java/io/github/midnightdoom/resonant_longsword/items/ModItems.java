package io.github.midnightdoom.resonant_longsword.items;

import io.github.midnightdoom.resonant_longsword.ResonantLongsword;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item RESONANT_LONGSWORD = registerItem("resonant_longsword",
            new ResonantSword(ToolMaterials.NETHERITE, 3, -2.8f, new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(ResonantLongsword.MOD_ID, name), item);
    }

    private static void addToCombat(FabricItemGroupEntries entries) {
        entries.add(RESONANT_LONGSWORD);
    }

    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(ModItems::addToCombat);
    }
}
