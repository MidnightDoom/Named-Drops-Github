package io.github.midnightdoom.resonant_longsword.mixin;

import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.utils.CustomNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    //particles and sounds on block
    @ModifyVariable(method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", at = @At("HEAD"), argsOnly = true)
    private float resonantLongsword$blockDamage(float amount, DamageSource source) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof ResonantSword && player.isUsingItem() && player.hurtTime <= 0 && !player.isInvulnerableTo(source)) {

            ResonantSword.addCharge(stack, Math.round(amount));

            //reduce fall damage more because parrying the ground is fun
            if (source.isOf(DamageTypes.FALL)) {
                amount *= 0.2F;
            } else {
                amount *= 0.5F;
            }

            //sounds and particles server side
            if (player.getWorld() instanceof ServerWorld world) {
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
            }
        }
        return amount;
    }

    @Unique private final Map<Entity, Boolean> sneakingStates = new HashMap<>();

    @Environment(EnvType.CLIENT)
    @Inject(method="tick", at = @At(value="HEAD"))
    public void resonantLongsword$abilities(CallbackInfo ci) {
        PlayerEntity player = PlayerEntity.class.cast(this);
        boolean isSneaking = player.isSneaking();
        boolean wasSneaking = sneakingStates.getOrDefault(player, false);
        ItemStack stack = player.getMainHandStack();

        //trigger blocking sound, charging, and check if the player is attacking
        passiveBlockSound(player);
        passiveCharge(player);

        boolean attacking = false;

        //checks if player is attacking
        if (player.getWorld().isClient && player.isMainPlayer()) {
            MinecraftClient client = MinecraftClient.getInstance();
            attacking = client.options.attackKey.isPressed();

            //dash
            if (player.getMainHandStack().getItem() instanceof ResonantSword && ResonantSword.getDashTime(stack) > 0) {
                //movement
                dash(player);
                CustomNetworking.sendDashPacket(player);
            }
            //when player starts sneaking, if blocking, charged and not dashing, dash
            //also dash if blocking and attacking
            else if (stack.getItem() instanceof ResonantSword
                    && ResonantSword.getCharge(stack) >= 30
                    && player.isUsingItem()
                    && ResonantSword.getDashTime(stack) <= 0
                    && !player.getItemCooldownManager().isCoolingDown(ModItems.RESONANT_LONGSWORD)
                    && ((isSneaking && !wasSneaking) || attacking)
            ) {
                CustomNetworking.sendDashTagPacket(player);
            }

            sneakingStates.put(player, isSneaking);
        }
    }

    //sounds that passively play when blocking
    @Unique
    private void passiveBlockSound(PlayerEntity player) {
        if (player.isUsingItem() && player.getMainHandStack().isOf(ModItems.RESONANT_LONGSWORD) && ResonantSword.getDashTime(player.getMainHandStack()) <= 0 && player.getWorld() instanceof ServerWorld world) {
            world.playSoundFromEntity(
                    null,
                    player,
                    RegistryEntry.of(SoundEvents.BLOCK_BEACON_AMBIENT),
                    SoundCategory.PLAYERS,
                    0.1F,
                    1.0F,
                    world.random.nextLong()
            );
            world.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    RegistryEntry.of(SoundEvents.BLOCK_CONDUIT_AMBIENT),
                    SoundCategory.PLAYERS,
                    0.1F,
                    1.0F,
                    world.random.nextLong()
            );
        }
    }

    @Unique private int chargeTime = 0;

    //charge the sword overtime in player inventories
    @Unique
    private void passiveCharge(PlayerEntity player) {
        if (chargeTime >= 20) {
            //charge all items to a max of 30
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);

                if (stack.getItem() instanceof ResonantSword) {
                    ResonantSword.addCharge(stack, 1, 30);
                    if (!inventory.getMainHandStack().equals(stack)) {
                        ResonantSword.resetDashTime(stack);
                    }
                }
            }
            chargeTime = 0;
        } else {
            chargeTime++;
        }
    }

    //dash movement and particles
    @Unique
    private void dash(PlayerEntity player) {
        //movement
        if (ResonantSword.getDashTime(player.getMainHandStack()) % 4 == 0) {
            Vec3d dashDirection = getDirectionFromYawPitch(player.getYaw(), player.getPitch()).multiply(.65); // Dash speed
            player.addVelocity(dashDirection.x, dashDirection.y, dashDirection.z);
            player.velocityModified = true;
        }
    }

    //get movement direction for dashing
    @Unique
    private Vec3d getDirectionFromYawPitch(float yaw, float pitch) {
        double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double y = -Math.sin(Math.toRadians(pitch));
        double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        return new Vec3d(x, y, z).normalize();
    }
}