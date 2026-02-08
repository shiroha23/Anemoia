package com.shiroha23.anemoia;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrazyGearItem extends Item {
    
    private static final Difficulty[] DIFFICULTIES = {
        Difficulty.PEACEFUL,
        Difficulty.EASY,
        Difficulty.NORMAL,
        Difficulty.HARD
    };
    
    public CrazyGearItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(itemStack);
        }

        if (!AnemoiaConfig.ENABLE_CRAZY_GEAR_DIFFICULTY.get()) {
            return InteractionResultHolder.fail(itemStack);
        }
        
        // 只在服务器端执行
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Difficulty currentDifficulty = serverLevel.getDifficulty();
            Difficulty nextDifficulty = getNextDifficulty(currentDifficulty);
            
            // 设置新的难度
            serverLevel.getServer().setDifficulty(nextDifficulty, true);
            
            // 发送消息给玩家
            Component difficultyName = Component.translatable("options.difficulty." + nextDifficulty.getKey());
            player.sendSystemMessage(Component.translatable("message.anemoia.difficulty_changed", difficultyName));
            
            return InteractionResultHolder.success(itemStack);
        }
        
        return InteractionResultHolder.pass(itemStack);
    }
    
    private Difficulty getNextDifficulty(Difficulty current) {
        for (int i = 0; i < DIFFICULTIES.length; i++) {
            if (DIFFICULTIES[i] == current) {
                // 返回下一个难度，如果到达末尾则循环到开头
                return DIFFICULTIES[(i + 1) % DIFFICULTIES.length];
            }
        }
        // 如果找不到当前难度，默认返回下一个（从和平开始）
        return DIFFICULTIES[0];
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.anemoia.crazy_gear"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}

