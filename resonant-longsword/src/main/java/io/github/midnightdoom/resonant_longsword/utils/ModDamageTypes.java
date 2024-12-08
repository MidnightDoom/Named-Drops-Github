package io.github.midnightdoom.resonant_longsword.utils;

import io.github.midnightdoom.resonant_longsword.ResonantLongsword;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ModDamageTypes {

    /*
     * Store the RegistryKey of our DamageType into a new constant called CUSTOM_DAMAGE_TYPE
     * The Identifier in use here points to our JSON file we created earlier.
     */
    public static final RegistryKey<DamageType> RESONANT_LONGSWORD_STRIKE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ResonantLongsword.MOD_ID, "resonant_longsword_damage_type"));
    public static final RegistryKey<DamageType> RESONANT_LONGSWORD_DASH = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ResonantLongsword.MOD_ID, "resonant_longsword_dash_damage_type"));
    public static final RegistryKey<DamageType> RESONANT_LONGSWORD_BLOCK_GOD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(ResonantLongsword.MOD_ID, "resonant_longsword_block_god"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static final DamageSource of(World world, RegistryKey<DamageType> key, @Nullable Entity source, @Nullable Entity attacker) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), source, attacker);
    }
}