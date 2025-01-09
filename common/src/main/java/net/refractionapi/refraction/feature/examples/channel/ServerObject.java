package net.refractionapi.refraction.feature.examples.channel;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.feature.data.Syncable;

// example server object
// using syncable for easier client creation
public class ServerObject implements Syncable<ServerObject> {
    public final TwoWayChannel channel;

    public ServerObject(Player owner) {
        this.channel = new TwoWayChannel(owner.level())
                .sender(this::send)
                .receiver((player, buf) -> player != null ? receive(player, buf) : 0)
                .owner(owner)
                .rule(TwoWayChannel.Rule.OWNER)
                .open();
        this.setSynced();
        this.sync(owner);
        this.channel.send();
    }

    public ServerObject() {
        this.channel = null;
    }

    public void send(FriendlyByteBuf buf) {
        buf.writeUtf("ServerObject");
    }

    public int receive(Player player, FriendlyByteBuf buf) {
        Refraction.LOGGER.info("{} sent by {}", buf.readUtf(), player.getName().getString());
        return 1;
    }

    public static void init() {
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(this.channel.id());
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        ClientObject.init(buf.readUUID());
    }
}
