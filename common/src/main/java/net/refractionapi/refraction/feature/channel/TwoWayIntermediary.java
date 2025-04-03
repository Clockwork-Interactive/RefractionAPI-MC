package net.refractionapi.refraction.feature.channel;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.data.Syncable;
import net.refractionapi.refraction.networking.C2S.TwoWayC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.util.Mutable;
import net.refractionapi.refraction.util.Pair;

import java.util.HashMap;
import java.util.List;
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

    public void sendTo(boolean isServer, String routerID, UUID uuid, TwoWayChannel.Data data, TwoWayChannel.Header header, TwoWayChannel.Rule.RuleConsumer rule, boolean terminated) {
        Optional<TwoWayChannel> channel = CHANNELS.get(uuid);
        if (channel == null) return;
        channel.ifPresent(c -> {
            FriendlyByteBuf headerBuf = new FriendlyByteBuf(Unpooled.buffer());
            CompoundTag headerTag = new CompoundTag();
            FriendlyByteBuf msgBuf = new FriendlyByteBuf(Unpooled.buffer());
            msgBuf.writeBoolean(terminated);
            msgBuf.writeUtf(routerID);
            boolean msg = c.message(routerID, msgBuf) || data != null;
            if (data != null) data.message(msgBuf);
            if (!msg) return;
            if (header != null) header.message(routerID, headerTag);
            else c.HEADER.message(routerID, headerTag);
            headerBuf.writeNbt(headerTag);
            c.messages.put(c.id, new TwoWayChannel.Message(c.id, routerID, new FriendlyByteBuf(msgBuf.copy()), new FriendlyByteBuf(headerBuf.copy()), new Mutable<>(Optional.empty())));
            if (!isServer)
                RefractionMessages.sendToServer(new TwoWayC2SPacket(uuid, headerBuf, msgBuf));
            else if (rule == null) c.rule.syncer.accept(c, uuid, headerBuf, msgBuf, c.canSendTo);
            else rule.accept(c, uuid, headerBuf, msgBuf, c.canSendTo);
        });
    }

    public void read(Player player, UUID uuid, FriendlyByteBuf header, FriendlyByteBuf buf) {
        Optional<TwoWayChannel> channel = CHANNELS.get(uuid);
        if (channel == null) return;
        channel.ifPresent(c -> {
            boolean terminated = buf.readBoolean();
            String routerID = buf.readUtf();
            // terminations only happen on the client side
            if (terminated && !c.isServer()) {
                if (c.closeOnTerminate)
                    c.close();
                CHANNELS.remove(uuid);
                NamedAPI.getChannel(uuid).ifPresent(NamedAPI::removeChannel);
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

    public void sendTo(boolean isServer, String router, UUID uuid, TwoWayChannel.Data data, TwoWayChannel.Header header, TwoWayChannel.Rule.RuleConsumer rule) {
        sendTo(isServer, router, uuid, data, header, rule, false);
    }

    public void sendTo(boolean isServer, String router, UUID uuid) {
        sendTo(isServer, router, uuid, null, null, null, false);
    }

    protected void terminate(UUID uuid) {
        sendTo(true, "", uuid, null, null, null, true);
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
        NamedAPI.clear();
    }

    public List<Pair<UUID, Optional<TwoWayChannel>>> channels() {
        return List.copyOf(CHANNELS.entrySet().stream().map(entry -> new Pair<>(entry.getKey(), entry.getValue())).toList());
    }

    public HashMap<UUID, Optional<TwoWayChannel>> hashChannels() {
        return new HashMap<>(this.CHANNELS);
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