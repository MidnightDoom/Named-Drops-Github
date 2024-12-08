package io.github.midnightdoom.resonant_longsword.mixin;

import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.utils.ModDamageTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyVariable(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/entity/Entity;)Z",
            at = @At(value = "HEAD"), argsOnly = true)
    public StatusEffectInstance resonantLongsword$halveDurationOnBlock(StatusEffectInstance effect) {

        if (LivingEntity.class.cast(this) instanceof PlayerEntity player) {

            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof ResonantSword && player.isUsingItem() && player.getWorld() instanceof ServerWorld world) {

                world.playSoundFromEntity(
                        null,
                        player,
                        RegistryEntry.of(SoundEvents.ITEM_SHIELD_BLOCK),
                        SoundCategory.PLAYERS,
                        0.5F,
                        1.5F,
                        world.random.nextLong()
                );
                world.playSoundFromEntity(
                        null,
                        player,
                        RegistryEntry.of(SoundEvents.BLOCK_AMETHYST_BLOCK_HIT),
                        SoundCategory.PLAYERS,
                        1.5F,
                        0.5F,
                        world.random.nextLong()
                );

                Random random = player.getRandom();
                world.spawnParticles(
                        ParticleTypes.END_ROD,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        5,
                        random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                        random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                        random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                        0.1
                );

                ResonantSword.addCharge(stack, 3, 60);

                return new StatusEffectInstance(effect.getEffectType(), effect.getDuration() / 2, effect.getAmplifier());
            }
        }
        return effect;
    }

    @Inject(method="kill", at = @At(value = "HEAD"), cancellable = true)
    public void resonantLongsword$funnyKillMessage(CallbackInfo ci) {
        try {
            PlayerEntity player = PlayerEntity.class.cast(this);
            if (player.isUsingItem() && player.getMainHandStack().isOf(ModItems.RESONANT_LONGSWORD)) {
                this.damage(ModDamageTypes.of(player.getWorld(), ModDamageTypes.RESONANT_LONGSWORD_BLOCK_GOD), Float.MAX_VALUE);
                ci.cancel();
            }
        } catch (Exception ignored) {}
    }

    @Shadow public boolean damage(DamageSource source, float amount) {
        return false;
    }
}
