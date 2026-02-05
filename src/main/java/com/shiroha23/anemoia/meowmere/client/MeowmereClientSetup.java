package com.shiroha23.anemoia.meowmere.client;

import com.shiroha23.anemoia.Anemoia;
import com.shiroha23.anemoia.meowmere.entity.CatModel;
import com.shiroha23.anemoia.meowmere.entity.CatRender;
import com.shiroha23.anemoia.meowmere.particle.PlayerRainbowParticle;
import com.shiroha23.anemoia.meowmere.particle.RainbowParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Anemoia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MeowmereClientSetup {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Anemoia.CAT_PROJECTILE.get(), CatRender::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CatModel.LAYER_LOCATION, CatModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void setupParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(Anemoia.PLAYER_RAINBOW.get(), new PlayerRainbowParticle.Factory());
        event.registerSpecial(Anemoia.RAINBOW.get(), new RainbowParticle.Factory());
    }
}
