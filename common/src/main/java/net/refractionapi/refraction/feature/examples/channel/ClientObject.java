package net.refractionapi.refraction.feature.examples.channel;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// example client object
public class ClientObject {
    public static TwoWayChannel channel;

    public static int listen(@Nullable Player player, FriendlyByteBuf buf) {
        Refraction.LOGGER.info("{} sent by server", buf.readUtf());
        channel.send("api", (byteBuf) -> byteBuf.writeUtf("ExtraData"));
        return 1;
    }

    public static void send(FriendlyByteBuf buf) {
        buf.writeUtf("ClientObject");
    }

    public static void init(UUID id) {
        channel = new TwoWayChannel(Minecraft.getInstance().level, id)
                .router("api", ClientObject::listen, ClientObject::send)
                .open();
    }
}
