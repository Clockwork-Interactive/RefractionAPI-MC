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
                .router(this::receiveDefault, this::sendDefault)
                .router("api", this::receiveDefault, this::sendAPI)
                .open();
        this.setSynced();
        this.sync(owner);
        // without an owner set, we're sending a message into the abyss
        // every player will receive this message,
        // and every player can send messages to this object
        this.channel.send("api");
    }

    public ServerObject() {
        this.channel = null;
    }

    public void sendDefault(FriendlyByteBuf buf) {
        buf.writeUtf("ServerObject");
    }

    public int receiveDefault(Player player, FriendlyByteBuf buf) {
        Refraction.LOGGER.info("{} sent by {}", buf.readUtf(), player.getName().getString());
        return 1;
    }

    public void sendAPI(FriendlyByteBuf buf) {
        buf.writeUtf("ServerObject API");
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
