package com.shiroha23.anemoia.meowmere.network;

import com.shiroha23.anemoia.Anemoia;
import com.shiroha23.anemoia.AnemoiaConfig;
import com.shiroha23.anemoia.meowmere.item.MeowmereSword;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSwingArm {
    public static void encode(MessageSwingArm message, FriendlyByteBuf buf) {
    }

    public static MessageSwingArm decode(FriendlyByteBuf buf) {
        return new MessageSwingArm();
    }

    public static void handle(MessageSwingArm message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer == null) {
                return;
            }
            if (serverPlayer.getMainHandItem().getItem() == Anemoia.MEOWMERE.get()
                && !serverPlayer.getCooldowns().isOnCooldown(Anemoia.MEOWMERE.get())) {
                if (AnemoiaConfig.ENABLE_MEOWMERE_SOUND.get()) {
                    serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                        SoundEvents.CAT_AMBIENT, SoundSource.PLAYERS, 3.0f, 1.0f);
                }
                MeowmereSword.addProjectile(serverPlayer);
            }
        });
        context.setPacketHandled(true);
    }
}
