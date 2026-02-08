package com.shiroha23.anemoia;

import com.mojang.logging.LogUtils;
import com.shiroha23.anemoia.meowmere.MeowmereContent;
import com.shiroha23.anemoia.meowmere.entity.CatProjectile;
import com.shiroha23.anemoia.meowmere.item.MeowmereSword;
import com.shiroha23.anemoia.effect.PhaseEffect;
import me.ramidzkh.mekae2.AMItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import appeng.items.storage.StorageTier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Anemoia.MODID)
public class Anemoia {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "anemoia";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    // Create a Deferred Register to hold Items which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Create a Deferred Register to hold Mob Effects which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    // Create a Deferred Register to hold Potions which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    // Create a Deferred Register to hold Entities which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    // Create a Deferred Register to hold Particle Types which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "anemoia" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    // Register the Crazy Gear item
    public static final RegistryObject<Item> CRAZY_GEAR = ITEMS.register("crazy_gear", 
        () -> new CrazyGearItem(new Item.Properties().stacksTo(1)));

    // Register the Weather Core item
    public static final RegistryObject<Item> WEATHER_CORE = ITEMS.register("weather_core", 
        () -> new WeatherCoreItem(new Item.Properties().stacksTo(1)));
    
    // Register the Universal Press item
    public static final RegistryObject<Item> UNIVERSAL_PRESS = ITEMS.register("universal_press", 
        () -> new UniversalPressItem(new Item.Properties().stacksTo(1)));
    
    // Register the Black Soul item
    public static final RegistryObject<Item> BLACK_SOUL = ITEMS.register("black_soul", 
        () -> new BlackSoulItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> RADIOACTIVE_CHEMICAL_STORAGE_CELL = ITEMS.register(
        "radioactive_chemical_storage_cell",
        () -> new RadioactiveChemicalStorageCellItem(new Item.Properties().stacksTo(1), StorageTier.SIZE_1K,
            AMItems.CHEMICAL_CELL_HOUSING.get()));

    // Register the Meowmere items
    public static final RegistryObject<Item> MEOWMERE = ITEMS.register("meowmere",
        () -> new MeowmereSword(MeowmereContent.MEOWMERE_TIER));


    // Register the Meowmere entity
    public static final RegistryObject<EntityType<CatProjectile>> CAT_PROJECTILE = ENTITIES.register("cat_projectile",
        () -> EntityType.Builder.<CatProjectile>of((type, level) -> new CatProjectile(type, level), MobCategory.MISC)
            .sized(0.5f, 0.5f)
            .clientTrackingRange(32)
            .fireImmune()
            .build("cat_projectile"));

    // Register the Meowmere particle types
    public static final RegistryObject<SimpleParticleType> RAINBOW = PARTICLE_TYPES.register("rainbow", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> PLAYER_RAINBOW = PARTICLE_TYPES.register("player_rainbow", () -> new SimpleParticleType(false));

    // Register the Phase effect and potion
    public static final RegistryObject<MobEffect> PHASE = EFFECTS.register("phase", PhaseEffect::new);
    public static final RegistryObject<Potion> PHASE_POTION = POTIONS.register("phase",
        () -> new Potion(new MobEffectInstance(PHASE.get(), 20 * 60 * 3)));
    public static final RegistryObject<Potion> LONG_PHASE_POTION = POTIONS.register("long_phase",
        () -> new Potion(new MobEffectInstance(PHASE.get(), 20 * 60 * 8)));
    
    // Register the Creative Mode Tab
    public static final RegistryObject<CreativeModeTab> ANEMOIA_TAB = CREATIVE_MODE_TABS.register("anemoia_tab", 
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.anemoia"))
            .icon(() -> CRAZY_GEAR.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(CRAZY_GEAR.get());
                output.accept(WEATHER_CORE.get());
                output.accept(UNIVERSAL_PRESS.get());
                output.accept(BLACK_SOUL.get());
                output.accept(MEOWMERE.get());
                output.accept(RADIOACTIVE_CHEMICAL_STORAGE_CELL.get());
                output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), PHASE_POTION.get()));
                output.accept(PotionUtils.setPotion(new ItemStack(Items.POTION), LONG_PHASE_POTION.get()));
            })
            .build());

    public Anemoia() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so effects get registered
        EFFECTS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so potions get registered
        POTIONS.register(modEventBus);

        // Register the Deferred Register to the mod event bus so entities get registered
        ENTITIES.register(modEventBus);

        // Register the Deferred Register to the mod event bus so particle types get registered
        PARTICLE_TYPES.register(modEventBus);
        
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AnemoiaConfig.SPEC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        AnemoiaNetwork.register();

        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
            Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
            Ingredient.of(Items.ENDER_PEARL),
            PotionUtils.setPotion(new ItemStack(Items.POTION), PHASE_POTION.get())
        )));

        event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new BrewingRecipe(
            Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), PHASE_POTION.get())),
            Ingredient.of(Items.REDSTONE),
            PotionUtils.setPotion(new ItemStack(Items.POTION), LONG_PHASE_POTION.get())
        )));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (AnemoiaConfig.ENABLE_BLACK_SOUL_RESPAWN_GRANT.get()) {
            player.getInventory().add(new ItemStack(BLACK_SOUL.get()));
        }
        if (BlackSoulItem.isMaxHealthEnabled()) {
            BlackSoulItem.applyStoredBonus(player);
            BlackSoulItem.restoreHealthToMax(player);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }

        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();
        if (BlackSoulItem.isMaxHealthEnabled()) {
            newPlayer.getPersistentData().putDouble(
                BlackSoulItem.HEALTH_BONUS_KEY,
                oldPlayer.getPersistentData().getDouble(BlackSoulItem.HEALTH_BONUS_KEY)
            );
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }

        if (BlackSoulItem.isMaxHealthEnabled()) {
            BlackSoulItem.applyStoredBonus(player);
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
