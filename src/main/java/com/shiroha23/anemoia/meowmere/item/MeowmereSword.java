package com.shiroha23.anemoia.meowmere.item;

import com.shiroha23.anemoia.Anemoia;
import com.shiroha23.anemoia.AnemoiaConfig;
import com.shiroha23.anemoia.meowmere.entity.CatProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MeowmereSword extends SwordItem {
    public MeowmereSword(Tier tier) {
        super(tier, 10, -2.0f, new Item.Properties().fireResistant().durability(tier.getUses()));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (!attacker.level().isClientSide && attacker instanceof Player player
            && !player.getCooldowns().isOnCooldown(Anemoia.MEOWMERE.get())) {
            addProjectile(player);
        }
        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                boolean enabled = AnemoiaConfig.ENABLE_MEOWMERE_SOUND.get();
                AnemoiaConfig.ENABLE_MEOWMERE_SOUND.set(!enabled);
                Component message = Component.translatable(enabled
                    ? "message.anemoia.meowmere_sound_disabled"
                    : "message.anemoia.meowmere_sound_enabled");
                player.displayClientMessage(message, true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    public static void addProjectile(Player player) {
        CatProjectile catProjectile = new CatProjectile(player.level(), player);
        Vec3 start = player.getEyePosition().add(player.getLookAngle().normalize());
        catProjectile.setPos(start.x, start.y, start.z);
        catProjectile.shoot(player.getLookAngle(), 1.0f);
        catProjectile.setDamageMultiplier(1.25f);
        player.level().addFreshEntity(catProjectile);
        player.getCooldowns().addCooldown(Anemoia.MEOWMERE.get(), getAttackSpeed(player));
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == Anemoia.MEOWMERE.get()) {
            stack.hurtAndBreak(1, player, item -> item.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
    }

    public static int getAttackSpeed(LivingEntity living) {
        AttributeInstance attributeInstance = living.getAttribute(Attributes.ATTACK_SPEED);
        if (attributeInstance != null) {
            double speed = attributeInstance.getValue();
            int time = (int) (20.0 / speed) - 1;
            return Math.max(0, time);
        }
        return 0;
    }
}
