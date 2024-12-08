package io.github.midnightdoom.resonant_longsword.utils;

import io.github.midnightdoom.resonant_longsword.ResonantLongsword;
import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.UUID;

public class CustomNetworking {
    public static final Identifier DASH_PACKET_ID = Identifier.of(ResonantLongsword.MOD_ID, "dash_event");
    public static final Identifier DASH_TAG_PACKET_ID = Identifier.of(ResonantLongsword.MOD_ID, "dash_tags_event");

    public static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(DASH_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Server-side handling of dash event

                double x = buf.readDouble();
                double y = buf.readDouble();
                double z = buf.readDouble();
                UUID uuid = buf.readUuid();

                handleDash(player, x, y, z, uuid);
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(DASH_TAG_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                // Server-side handling of dash event
                UUID uuid = buf.readUuid();
                handleDashTags(player, uuid);
            });
        });
    }

    private static void handleDashTags(ServerPlayerEntity player, UUID uuid) {

        if (player.getUuid().equals(uuid)) {

            //sets sword to cooldown
            player.getItemCooldownManager().set(ModItems.RESONANT_LONGSWORD, 20);

            //lower charge, increase dash
            ItemStack stack = player.getMainHandStack();
            ResonantSword.addCharge(stack, -30);
            ResonantSword.setDashTime(stack, 20);
        }
    }

    private static void handleDash(ServerPlayerEntity player, double x, double y, double z, UUID uuid) {
        ServerWorld world = (ServerWorld) player.getWorld();
        Random random = player.getRandom();

        //particles and sounds
        world.spawnParticles(
                ParticleTypes.END_ROD,
                x,
                y,
                z,
                5,
                random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                0.1
        );

        world.playSound(
                null,
                x,
                y,
                z,
                SoundEvents.BLOCK_AMETHYST_BLOCK_HIT,
                SoundCategory.PLAYERS,
                1.0F,
                0.5F
        );
        world.playSound(
                null,
                x,
                y,
                z,
                SoundEvents.BLOCK_BEACON_ACTIVATE,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
        );

        //checks if player receiving packet is packet sender
        //if so, deal damage
        if (player.getUuid().equals(uuid)) {

            ItemStack stack = player.getMainHandStack();
            if (stack.getItem() instanceof ResonantSword sword) {

                Box boundingBox = player.getBoundingBox().expand(0.5); // Slightly expand to increase detection accuracy
                List<Entity> collidedEntities = player.getWorld().getOtherEntities(
                        player, boundingBox, entity -> entity instanceof LivingEntity && entity.isAlive()
                );
                for (Entity entity : collidedEntities) {
                    if (entity instanceof LivingEntity target) {
                        dashDamage(player, target, sword);
                    }
                }
            }
            ResonantSword.decrementDashTime(stack);
        }
    }

    //deal damage and knockback with dash
    @Unique
    private static void dashDamage(PlayerEntity player, LivingEntity target, ResonantSword sword) {
        ItemStack swordStack = player.getMainHandStack();

        // Apply sword damage
        player.attack(target);

        // Apply knockback
        Vec3d knockbackDir = target.getPos().subtract(player.getPos()).normalize().multiply(0.5);
        target.addVelocity(knockbackDir.x, 0.2, knockbackDir.z);
        target.velocityModified = true;

        // Apply sword-specific effects (like enchantments)
        sword.postHit(swordStack, target, player);

        // Play sound
        player.playSound(SoundEvents.ITEM_TRIDENT_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    public static void sendDashPacket(PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeDouble(player.getX());
        buf.writeDouble(player.getY());
        buf.writeDouble(player.getZ());
        buf.writeUuid(player.getUuid());

        ClientPlayNetworking.send(DASH_PACKET_ID, buf);
    }

    public static void sendDashTagPacket(PlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeUuid(player.getUuid());

        ClientPlayNetworking.send(DASH_TAG_PACKET_ID, buf);
    }
}