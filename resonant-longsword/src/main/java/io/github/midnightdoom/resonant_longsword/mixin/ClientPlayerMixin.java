package io.github.midnightdoom.resonant_longsword.mixin;

import com.mojang.authlib.GameProfile;
import io.github.midnightdoom.resonant_longsword.items.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerMixin extends AbstractClientPlayerEntity {

    @Shadow public abstract boolean isUsingItem();

    public ClientPlayerMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean resonantLongsword$cancelWeaponSlowdown(ClientPlayerEntity instance) {
        return isUsingResonantLongsword(instance);
    }

    @Redirect(method = "canStartSprinting", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
    private boolean resonantLongsword$allowWeaponSprint(ClientPlayerEntity instance) {
        return isUsingResonantLongsword(instance);
    }

    @Unique
    private static boolean isUsingResonantLongsword(ClientPlayerEntity instance) {
        if (instance.getActiveItem().isOf(ModItems.RESONANT_LONGSWORD)) {
            return false;
        } else {
            return instance.isUsingItem();
        }
    }
}
