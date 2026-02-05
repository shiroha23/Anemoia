package com.shiroha23.anemoia;

import net.minecraftforge.common.ForgeConfigSpec;

public class AnemoiaConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_CRAZY_GEAR_DIFFICULTY;
    public static final ForgeConfigSpec.BooleanValue ENABLE_WEATHER_CORE_WEATHER;
    public static final ForgeConfigSpec.BooleanValue ENABLE_BLACK_SOUL_RESPAWN_GRANT;
    public static final ForgeConfigSpec.DoubleValue BLACK_SOUL_MAX_HEALTH_BONUS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("features");
        ENABLE_CRAZY_GEAR_DIFFICULTY = builder
            .comment("Whether Crazy Gear can change difficulty")
            .define("enableCrazyGearDifficulty", true);
        ENABLE_WEATHER_CORE_WEATHER = builder
            .comment("Whether Weather Core can change weather")
            .define("enableWeatherCoreWeather", true);
        ENABLE_BLACK_SOUL_RESPAWN_GRANT = builder
            .comment("Whether Black Soul is granted on respawn")
            .define("enableBlackSoulRespawnGrant", true);
        BLACK_SOUL_MAX_HEALTH_BONUS = builder
            .comment("Black Soul max health bonus (0 = disabled)")
            .defineInRange("blackSoulMaxHealthBonus", 20.0D, 0.0D, 1024.0D);
        builder.pop();

        SPEC = builder.build();
    }

    private AnemoiaConfig() {
    }
}
