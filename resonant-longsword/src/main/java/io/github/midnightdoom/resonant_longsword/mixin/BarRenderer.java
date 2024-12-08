package io.github.midnightdoom.resonant_longsword.mixin;

import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Colors;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public abstract class BarRenderer {

    @Shadow public abstract MatrixStack getMatrices();
    @Shadow public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color) {}

    @Inject(at = @At("TAIL"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V")
    public void resonantLongsword$chargeBar(TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countOverride, CallbackInfo info)
    {
        if (stack.isOf(ModItems.RESONANT_LONGSWORD) && stack.hasNbt() && stack.getNbt().contains("Charge")) {

            int charge = ResonantSword.getCharge(stack);

            int bar1 = charge >= 30 ? 13 : getBarSize(charge, 30);
            int bar2 = charge > 30 ? getBarSize(charge - 30, 30) : 0;

            // Calculate bar width based on the custom value
            int barColor = charge >= 30 ? 0x6c25da : 0x3620c5;
            int barColor2 = charge >= 60 ? 0xc72ffe : 0x9a2aed;

            int k = x + 2;
            int l = y + 13;

            // Draw the custom bar
            this.fill(RenderLayer.getGuiOverlay(), k, l + 1, k + 13, l + 3, Colors.BLACK);
            this.fill(RenderLayer.getGuiOverlay(), k, l + 1, k + bar1, l + 2, barColor | Colors.BLACK);
            this.fill(RenderLayer.getGuiOverlay(), k, l + 1, k + bar2, l + 2, barColor2 | Colors.BLACK);
        }
    }

    @Unique
    private static int getBarSize(int value, int maxValue) {
        return  Math.round((float) value * 13.0F / (float) maxValue);
    }
}