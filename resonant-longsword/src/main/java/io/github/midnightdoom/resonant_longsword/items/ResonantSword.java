package io.github.midnightdoom.resonant_longsword.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class ResonantSword extends SwordItem {

    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    private final float attackDamage;

    public ResonantSword(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);

        this.attackDamage = (float)attackDamage + material.getAttackDamage();
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)attackSpeed, EntityAttributeModifier.Operation.ADDITION)
        );

        this.attributeModifiers = builder.build();
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    private static final int maxCharge = 60;

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    // modify stats based on charge
    // deals 8 damage with 1.2 attack speed at charge < 30
    // deals 12 damage with 1.6 attack speed at charge >= 30
    // deals 16 damage with 2 attack speed at max charge
    public void applyEffects(LivingEntity entity) {
        ItemStack stack = entity.getEquippedStack(EquipmentSlot.MAINHAND);

        // Retrieve the NBT from the stack
        NbtCompound nbt = stack.getNbt();
        int charge = nbt != null ? nbt.getInt("Charge") : 0;

        // Base stats
        float baseDamage = this.getAttackDamage();
        float baseAttackSpeed = -2.8f;

        float extraDamage;
        float extraAttackSpeed;

        // modifiers
        if (charge >= 60) {
            extraDamage = 8;
            extraAttackSpeed = 0.8F;
        } else if (charge >= 30) {
            extraDamage = 4;
            extraAttackSpeed = 0.4F;
        } else {
            extraDamage = 0;
            extraAttackSpeed = 0;
        }

        // Build attribute modifiers
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "extra_damage_from_resonant_longsword", baseDamage + extraDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "extra_attack_speed_from_resonant_longsword", baseAttackSpeed + extraAttackSpeed, EntityAttributeModifier.Operation.ADDITION)
        );
        this.attributeModifiers = builder.build();
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.literal("Can absorb some incoming energy by blocking,").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("and can use the energy to strengthen it's own attacks, ").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("or unleash it in one quick burst of speed.").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.literal("Right click to block. Sneak or punch while blocking to dash.").formatted(Formatting.DARK_AQUA));

        if (stack.hasNbt() && stack.getOrCreateNbt().contains("Charge") && context.isAdvanced()) {
            int charge = stack.getOrCreateNbt().getInt("Charge");

            tooltip.add(Text.literal("").formatted(Formatting.BLACK));
            tooltip.add(Text.literal("Charge: " + charge + "/60").formatted(Formatting.WHITE));
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        if (target.getWorld() instanceof ServerWorld world) {
            world.playSoundFromEntity(null, attacker, RegistryEntry.of(SoundEvents.BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS, 0.5F, 1.0F, world.random.nextLong());
            world.playSoundFromEntity(null, attacker, RegistryEntry.of(SoundEvents.BLOCK_CONDUIT_ATTACK_TARGET), SoundCategory.PLAYERS, 0.5F, 1.0F, world.random.nextLong());

            Random random = target.getRandom();
            world.spawnParticles(ParticleTypes.END_ROD, target.getX(), target.getEyeY(), target.getZ(), 5, random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1), random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1),
                    random.nextDouble() / 20.0 * (random.nextBoolean() ? 1 : -1), 0.1);
        }

        if (getDashTime(stack) <= 0) {
            addCharge(stack, -3);
        }

        return super.postHit(stack, target, attacker);
    }

    // handles blocking
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        user.setCurrentHand(hand);

        return TypedActionResult.consume(stack);
    }

    @Override
    public int getMaxUseTime(ItemStack stack)
    {
        return 72000; // Allows holding right-click indefinitely
    }

    public static synchronized int getCharge(ItemStack stack)
    {
        return stack.getOrCreateNbt().getInt("Charge");
    }

    public static synchronized void setCharge(ItemStack stack, int level) {
        if (level < 0) {
            stack.getOrCreateNbt().putInt("Charge", 0);
        }
        else if (level > maxCharge) {
            stack.getOrCreateNbt().putInt("Charge", 60);
        }
        else {
            stack.getOrCreateNbt().putInt("Charge", level);
        }
    }

    public static synchronized void addCharge(ItemStack stack, int level) {
        setCharge(stack, getCharge(stack) + level);
    }

    public static synchronized void addCharge(ItemStack stack, int level, int max) {
        if (getCharge(stack) < max) {
            setCharge(stack, Math.min(getCharge(stack) + level, max));
        }
    }

    public static synchronized int getDashTime(ItemStack stack)
    {
        return stack.getOrCreateNbt().getInt("Dash_Time");
    }

    public static synchronized void setDashTime(ItemStack stack, int level) {
        stack.getOrCreateNbt().putInt("Dash_Time", level);
    }

    public static synchronized void decrementDashTime(ItemStack stack)
    {
        setDashTime(stack, getDashTime(stack) - 1);
    }

    public static synchronized void resetDashTime(ItemStack stack)
    {
        stack.getOrCreateNbt().putInt("Dash_Time", 0);
    }
}
