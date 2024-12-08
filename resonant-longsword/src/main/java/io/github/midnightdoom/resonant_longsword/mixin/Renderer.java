package io.github.midnightdoom.resonant_longsword.mixin;

import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class Renderer {

    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void amethystSword$renderSword(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
        ItemStack stack = player.getMainHandStack();
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ResonantSword) {
                if (!player.handSwinging && player.isUsingItem()) {
                    cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_HOLD);
                } else {
                    cir.setReturnValue(BipedEntityModel.ArmPose.CROSSBOW_CHARGE);
                }
            }
        }
    }
}
