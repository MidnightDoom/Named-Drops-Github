package io.github.midnightdoom.resonant_longsword;

import io.github.midnightdoom.resonant_longsword.items.ModItems;
import io.github.midnightdoom.resonant_longsword.items.ResonantSword;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ResonantLongswordClient implements ClientModInitializer {

    public static void registerModelPredicateProviders() {

        ModelPredicateProviderRegistry.register(ModItems.RESONANT_LONGSWORD, Identifier.of(ResonantLongsword.MOD_ID, "charge"), (stack, world, entity, seed) -> {
            if (stack.hasNbt() && stack.getItem() instanceof ResonantSword) {
                int charge = ResonantSword.getCharge(stack);

                if (charge >= 60) {
                    return 1.0F;
                } else if (charge >= 30) {
                    return .5F;
                }
            }
            return 0;
        });

        ModelPredicateProviderRegistry.register(ModItems.RESONANT_LONGSWORD, Identifier.of(ResonantLongsword.MOD_ID, "blocking"), (stack, world, entity, seed) -> {
            if (entity instanceof PlayerEntity player && player.getMainHandStack().isOf(ModItems.RESONANT_LONGSWORD) && player.isUsingItem()) {
                return 1;
            }
            return 0;
        });
    }

    @Override
    public void onInitializeClient() {
        registerModelPredicateProviders();
    }
}