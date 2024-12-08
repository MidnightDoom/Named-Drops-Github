package io.github.midnightdoom.resonant_longsword.mixin;

import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import io.github.midnightdoom.resonant_longsword.utils.ModDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSources.class)
public class DamageSourcesMixin {

    @Shadow
    public final DamageSource create(RegistryKey<DamageType> key, @Nullable Entity attacker) {
        return null;
    }

    @Inject(method="playerAttack", at = @At(value="HEAD"), cancellable = true)
    public void resonantLongsword$customAttacks(PlayerEntity attacker, CallbackInfoReturnable<DamageSource> cir) {

        ItemStack stack = attacker.getMainHandStack();

        if (stack.isOf(ModItems.RESONANT_LONGSWORD)) {
            if (ResonantSword.getDashTime(stack) > 0) {
                cir.setReturnValue(create(ModDamageTypes.RESONANT_LONGSWORD_DASH, attacker));
            } else {
                cir.setReturnValue(create(ModDamageTypes.RESONANT_LONGSWORD_STRIKE, attacker));
            }
        }
    }

    @Inject(method="mobAttack", at = @At(value="HEAD"), cancellable = true)
    public void resonantLongsword$customAttacks(LivingEntity attacker, CallbackInfoReturnable<DamageSource> cir) {

        ItemStack stack = attacker.getMainHandStack();

        if (stack.isOf(ModItems.RESONANT_LONGSWORD)) {
            if (ResonantSword.getDashTime(stack) > 0) {
                cir.setReturnValue(create(ModDamageTypes.RESONANT_LONGSWORD_DASH, attacker));
            } else {
                cir.setReturnValue(create(ModDamageTypes.RESONANT_LONGSWORD_STRIKE, attacker));
            }
        }
    }
}
