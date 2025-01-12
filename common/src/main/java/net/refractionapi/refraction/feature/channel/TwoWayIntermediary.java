package net.refractionapi.refraction.feature.channel;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.data.Syncable;
import net.refractionapi.refraction.networking.C2S.TwoWayC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that manages two-way communication between the client and server. <br>
 *  - {@link TwoWayChannel} is used to create a channel <br>
 */
public class TwoWayIntermediary implements Syncable<TwoWayIntermediary> {
    protected static TwoWayIntermediary[] INSTANCE = new TwoWayIntermediary[2]; // I'm not making client and server classes for this
    protected SyncConfig syncConfig = new SyncConfig()
            .setSyncer(this::sync);
    protected final ConcurrentHashMap<UUID, Optional<TwoWayChannel>> CHANNELS = new ConcurrentHashMap<>();

    public TwoWayIntermediary() {
        this(0);
    }

    public TwoWayIntermediary(int index) {
        if (INSTANCE[index] == null)
            INSTANCE[index] = this;
        this.setSynced();
    }

    public void addChannel(TwoWayChannel channel) {
        this.CHANNELS.put(channel.id(), Optional.of(channel));
        if (!channel.level.isClientSide)
            this.syncConfig.syncAll((ServerLevel) channel.level);
    }

    public void sendTo(boolean isServer, String routerID, UUID uuid, TwoWayChannel.Extra extra, TwoWayChannel.Header header, boolean terminated) {
        Optional<TwoWayChannel> channel = CHANNELS.get(uuid);
        if (channel == null) return;
        channel.ifPresent(c -> {
            FriendlyByteBuf headerBuf = new FriendlyByteBuf(Unpooled.buffer());
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeBoolean(terminated);
            buf.writeUtf(routerID);
            boolean msg = c.message(routerID, buf) || extra != null;
            if (extra != null) extra.message(buf);
            if (!msg) return;
            if (header != null) header.message(routerID, headerBuf);
            else c.HEADER.message(routerID, headerBuf);
            if (!isServer)
                RefractionMessages.sendToServer(new TwoWayC2SPacket(uuid, headerBuf, buf));
            else
                c.rule.syncer.accept(c, uuid, headerBuf, buf);
        });
    }

    public void sendTo(boolean isServer, String router, TwoWayChannel.Extra extra, TwoWayChannel.Header header, UUID uuid) {
        sendTo(isServer, router, uuid, extra, header, false);
    }

    public void sendTo(boolean isServer, String router, UUID uuid) {
        sendTo(isServer, router, uuid, null, null, false);
    }

    protected void terminate(UUID uuid) {
        sendTo(true, "", uuid, null, null, true);
    }

    public void read(Player player, UUID uuid, FriendlyByteBuf header, FriendlyByteBuf buf) {
        Optional<TwoWayChannel> channel = CHANNELS.get(uuid);
        if (channel == null) return;
        channel.ifPresent(c -> {
            boolean terminated = buf.readBoolean();
            String routerID = buf.readUtf();
            // terminations only happen on the client side
            if (terminated && player.level().isClientSide) {
                if (c.closeOnTerminate)
                    c.close();
                CHANNELS.remove(uuid);
                return;
            }
            if (player instanceof ServerPlayer serverPlayer) {
                if (!c.canCommunicate.apply(serverPlayer)) return;
            }
            c.setCommunicating();
            c.receive(player, routerID, header, buf);
            c.receivedHeader = null;
        });
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        for (UUID uuid : CHANNELS.keySet()) buf.writeUUID(uuid);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        while (buf.isReadable()) CHANNELS.computeIfAbsent(buf.readUUID(), uuid -> Optional.empty());
    }

    public void reset() {
        CHANNELS.clear();
    }

    public static TwoWayIntermediary instance(boolean isServer) {
        int index = isServer ? 0 : 1;
        return INSTANCE[index] == null ? INSTANCE[index] = new TwoWayIntermediary(index) : INSTANCE[index];
    }

    public static void init(MinecraftServer server) {
        if (server.isReady())
            new TwoWayIntermediary();
    }
}
