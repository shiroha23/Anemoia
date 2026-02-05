package com.shiroha23.anemoia.meowmere;

import com.shiroha23.anemoia.Anemoia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;

public final class MeowmereContent {
    public static final RandomSource RANDOM = RandomSource.create();

    public static final Tier MEOWMERE_TIER = new ForgeTier(
        4,
        2000,
        -4.0f,
        -1.0f,
        10,
        BlockTags.NEEDS_DIAMOND_TOOL,
        () -> Ingredient.of(Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH)
    );

    private MeowmereContent() {
    }

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(Anemoia.MODID, path);
    }
}
