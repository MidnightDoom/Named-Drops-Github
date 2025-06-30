package io.github.midnightdoom.named_drops.mixin;

import io.github.midnightdoom.named_drops.ModConfig;
import io.github.midnightdoom.named_drops.RenameRule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerDrops {

    @Inject(at = @At("HEAD"), method = "dropInventory")
    private void renameItems(CallbackInfo info) {

        PlayerEntity player = (PlayerEntity) (Object) this;

        for (ItemStack item : player.getInventory().main) {
            renameWithRule(item, player);
        }

        for (ItemStack item : player.getInventory().armor) {
            rename(item, player);
        }

        if (!player.getOffHandStack().isStackable()) renameWithRule(player.getOffHandStack(), player);
    }

    @Unique
    private void rename(ItemStack stack, PlayerEntity player) {

        if (stack.hasCustomName()) {
            return;
        }
        MutableText name = Text.literal(player.getName().getLiteralString());

        System.out.println("name");
        MutableText translation = Text.translatable(stack.getTranslationKey());
        stack.setCustomName(name.append(Text.of("'s ")).append(translation));
    }

    @Unique
    private void renameWithRule(ItemStack stack, PlayerEntity player) {

        UUID uuid = player.getUuid();
        ModConfig.PlayerRenameConfig config = ModConfig.getConfigFor(uuid);

        if (ModConfig.rule(config) == RenameRule.ALL) {
            rename(stack, player);
        } else if (ModConfig.rule(config) == RenameRule.UNSTACKABLES && !stack.isStackable()) {
            rename(stack, player);
        } else if (ModConfig.rule(config) == RenameRule.LIST_ONLY && ModConfig.isIncluded(stack.getItem(), config)) {
            rename(stack, player);
        } else if (ModConfig.rule(config) == RenameRule.UNSTACKABLES_PLUS_LIST && (!stack.isStackable() || ModConfig.isIncluded(stack.getItem(), config))) {
            rename(stack, player);
        }
    }
}
