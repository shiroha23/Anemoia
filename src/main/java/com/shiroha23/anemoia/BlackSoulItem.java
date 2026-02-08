package com.shiroha23.anemoia;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class BlackSoulItem extends Item {

    public static final String HEALTH_BONUS_KEY = "anemoia.black_soul_health_bonus";
    public static final double HEALTH_BONUS_STEP = 2.0D;
    public static final double HEALTH_BONUS_DEFAULT_MAX = 20.0D;
    private static final UUID HEALTH_BONUS_UUID = UUID.fromString("5e4a0a9c-9b59-4b9e-8b34-0f1a9f0b6a1f");
    
    public BlackSoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        if (!isMaxHealthEnabled()) {
            return InteractionResultHolder.fail(stack);
        }
        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        double currentBonus = getStoredBonus(player);
        double maxBonus = getMaxBonus();
        if (currentBonus >= maxBonus) {
            return InteractionResultHolder.fail(stack);
        }

        double newBonus = Math.min(maxBonus, currentBonus + HEALTH_BONUS_STEP);
        setStoredBonus(player, newBonus);
        applyHealthBonus(player, newBonus);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void applyStoredBonus(Player player) {
        applyHealthBonus(player, getStoredBonus(player));
    }

    public static boolean isMaxHealthEnabled() {
        return getMaxBonus() > 0.0D;
    }

    public static void restoreHealthToMax(Player player) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }

        player.setHealth((float) attribute.getValue());
    }

    private static void applyHealthBonus(Player player, double bonus) {
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute == null) {
            return;
        }

        AttributeModifier existing = attribute.getModifier(HEALTH_BONUS_UUID);
        if (existing != null) {
            attribute.removeModifier(existing);
        }

        if (bonus > 0) {
            AttributeModifier modifier = new AttributeModifier(
                HEALTH_BONUS_UUID,
                "BlackSoulHealthBonus",
                bonus,
                AttributeModifier.Operation.ADDITION
            );
            attribute.addPermanentModifier(modifier);
        }

        float maxHealth = (float) attribute.getValue();
        if (player.getHealth() > maxHealth) {
            player.setHealth(maxHealth);
        }
    }

    private static double getStoredBonus(Player player) {
        CompoundTag data = player.getPersistentData();
        return data.getDouble(HEALTH_BONUS_KEY);
    }

    private static double getMaxBonus() {
        if (AnemoiaConfig.BLACK_SOUL_MAX_HEALTH_BONUS != null) {
            return Math.max(0.0D, AnemoiaConfig.BLACK_SOUL_MAX_HEALTH_BONUS.get());
        }
        return HEALTH_BONUS_DEFAULT_MAX;
    }

    private static void setStoredBonus(Player player, double bonus) {
        CompoundTag data = player.getPersistentData();
        data.putDouble(HEALTH_BONUS_KEY, bonus);
    }
}