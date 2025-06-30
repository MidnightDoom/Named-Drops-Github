package io.github.midnightdoom.named_drops.mixin;

import io.github.midnightdoom.named_drops.ModConfig;
import io.github.midnightdoom.named_drops.RenameRule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class NamedMobLoot {

	@Inject(at = @At("HEAD"), method = "dropStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/ItemEntity;")
	private void renameItems(ItemStack stack, CallbackInfoReturnable<ItemEntity> cir) {

		if (!((Object) this instanceof LivingEntity entity)) return;
		if (entity instanceof PlayerEntity) return;

		if (entity.getWorld() instanceof ServerWorld) {

			if (entity.getCustomName() != null) {

				renameWithRule(stack, entity);

			}
		}
	}

	@Unique
	private void rename(ItemStack stack, LivingEntity entity) {
		if (stack.hasCustomName()) {
			System.out.println("Custom name");
			return;
		}
		MutableText name = Text.literal(entity.getCustomName().getLiteralString());
		System.out.println("name");
		MutableText translation = Text.translatable(stack.getTranslationKey());
		stack.setCustomName(name.append(Text.of("'s ")).append(translation));
	}

	@Unique
	private void renameWithRule(ItemStack stack, LivingEntity entity) {
		if (ModConfig.mobRenameRule() == RenameRule.ALL) {
			rename(stack, entity);
		} else if (ModConfig.mobRenameRule() == RenameRule.UNSTACKABLES && !stack.isStackable()) {
			rename(stack, entity);
		} else if (ModConfig.mobRenameRule() == RenameRule.LIST_ONLY && ModConfig.isIncluded(stack.getItem(), ModConfig.getDefaultConfig())) {
			rename(stack, entity);
		} else if (ModConfig.mobRenameRule() == RenameRule.UNSTACKABLES_PLUS_LIST && (!stack.isStackable() || ModConfig.isIncluded(stack.getItem(), ModConfig.getDefaultConfig()))) {
			rename(stack, entity);
		}
	}
}