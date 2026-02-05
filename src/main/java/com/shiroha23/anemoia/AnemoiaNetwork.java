package com.shiroha23.anemoia;

import com.shiroha23.anemoia.meowmere.network.MessageSwingArm;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class AnemoiaNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Anemoia.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private AnemoiaNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(MessageSwingArm.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
            .encoder(MessageSwingArm::encode)
            .decoder(MessageSwingArm::decode)
            .consumerMainThread(MessageSwingArm::handle)
            .add();
    }
}
