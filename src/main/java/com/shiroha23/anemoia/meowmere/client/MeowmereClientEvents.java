package com.shiroha23.anemoia.meowmere.client;

import com.shiroha23.anemoia.Anemoia;
import com.shiroha23.anemoia.AnemoiaNetwork;
import com.shiroha23.anemoia.meowmere.network.MessageSwingArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Anemoia.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MeowmereClientEvents {
    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
        if (!event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity().getMainHandItem().getItem() == Anemoia.MEOWMERE.get()
            && !event.getEntity().getCooldowns().isOnCooldown(Anemoia.MEOWMERE.get())) {
            AnemoiaNetwork.CHANNEL.sendToServer(new MessageSwingArm());
        }
    }
}
