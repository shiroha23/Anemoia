package com.shiroha23.anemoia;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WeatherCoreItem extends Item {
    
    // 天气类型枚举
    private enum WeatherType {
        CLEAR("clear", 0, 0),      // 晴天
        RAIN("rain", 24000, 0),      // 雨天（持续较长时间）
        THUNDER("thunder", 24000, 24000); // 雷暴（持续较长时间）
        
        private final String key;
        private final int rainTime;
        private final int thunderTime;
        
        WeatherType(String key, int rainTime, int thunderTime) {
            this.key = key;
            this.rainTime = rainTime;
            this.thunderTime = thunderTime;
        }
        
        public String getKey() {
            return key;
        }
        
        public int getRainTime() {
            return rainTime;
        }
        
        public int getThunderTime() {
            return thunderTime;
        }
    }
    
    private static final WeatherType[] WEATHER_TYPES = {
        WeatherType.CLEAR,
        WeatherType.RAIN,
        WeatherType.THUNDER
    };
    
    public WeatherCoreItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(itemStack);
        }

        if (!AnemoiaConfig.ENABLE_WEATHER_CORE_WEATHER.get()) {
            return InteractionResultHolder.fail(itemStack);
        }
        
        // 只在服务器端执行
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            WeatherType currentWeather = getCurrentWeather(serverLevel);
            WeatherType nextWeather = getNextWeather(currentWeather);
            
            // 使用命令执行器执行天气命令（最可靠的方法）
            var server = serverLevel.getServer();
            String weatherCommand = switch (nextWeather) {
                case CLEAR -> "weather clear";
                case RAIN -> "weather rain";
                case THUNDER -> "weather thunder";
            };
            
            // 执行天气命令
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput().withPermission(4),
                weatherCommand
            );
            
            // 发送消息给玩家
            Component weatherName = Component.translatable("message.anemoia.weather." + nextWeather.getKey());
            player.sendSystemMessage(Component.translatable("message.anemoia.weather_changed", weatherName));
            
            return InteractionResultHolder.success(itemStack);
        }
        
        // 客户端也需要返回 success 以显示使用动画
        return InteractionResultHolder.success(itemStack);
    }
    
    private WeatherType getCurrentWeather(ServerLevel level) {
        if (level.isThundering()) {
            return WeatherType.THUNDER;
        } else if (level.isRaining()) {
            return WeatherType.RAIN;
        } else {
            return WeatherType.CLEAR;
        }
    }
    
    private WeatherType getNextWeather(WeatherType current) {
        for (int i = 0; i < WEATHER_TYPES.length; i++) {
            if (WEATHER_TYPES[i] == current) {
                // 返回下一个天气，如果到达末尾则循环到开头
                return WEATHER_TYPES[(i + 1) % WEATHER_TYPES.length];
            }
        }
        // 如果找不到当前天气，默认返回下一个（从晴天开始）
        return WEATHER_TYPES[0];
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.anemoia.weather_core"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}

