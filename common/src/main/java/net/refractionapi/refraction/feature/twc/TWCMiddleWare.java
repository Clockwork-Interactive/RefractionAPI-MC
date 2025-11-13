package net.refractionapi.refraction.feature.twc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.channel.SyncConfig;
import net.refractionapi.refraction.feature.data.Syncable;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.TWCPacket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TWCMiddleWare implements Syncable<TWCMiddleWare> {
    private static final ThreadLocal<TWCMiddleWare> INSTANCE = ThreadLocal.withInitial(TWCMiddleWare::new);
    protected final ConcurrentHashMap<UUID, Optional<TWC>> ROUTERS = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<ResourceLocation, UUID> NAMED = new ConcurrentHashMap<>();
    private final SyncConfig config = new SyncConfig().setSyncer(this::sync);

    public TWCMiddleWare() {

    }

    protected void open(TWC channel) {
        ROUTERS.put(channel.id(), Optional.of(channel));
        if (channel.apiKey != null) NAMED.put(channel.apiKey, channel.id());
        sync(channel);
    }

    protected void close(TWC channel) {
        ROUTERS.remove(channel.id());
        if (channel.apiKey != null) NAMED.remove(channel.apiKey);
        sync(channel);
    }

    private void sync(TWC channel) {
        if (!channel.level().isClientSide) config.syncAll((ServerLevel) channel.level());
    }

    public void sendMessage(
            TWC twc,
            String routerID,
            TWC.Message message,
            List<? extends Player> targets
    ) {
        boolean clientBound = !twc.level().isClientSide;
        message = twc.preSendHook.apply(message);
        if (message == null) return;
        if (clientBound) {
            sendToPlayer(twc, routerID, message, targets.stream()
                    .map(p -> (ServerPlayer) p)
                    .toList());
        } else {
            sendToServer(twc, routerID, message);
        }
    }

    public void receiveMessage(
            Player player,
            UUID twcID,
            FriendlyByteBuf buf
    ) {
        Optional<TWC> twcOpt = ROUTERS.get(twcID);
        if (twcOpt == null || twcOpt.isEmpty()) return;
        TWC twc = twcOpt.get();
        TWC.Message message = TWC.Message.fromBytes(twc, buf, player);
        message = twc.preReceiveHook.apply(message);
        if (message == null) return;
        twc.routeMessage(message);
    }

    private void sendToServer(
            TWC twc,
            String routerID,
            TWC.Message message
    ) {
        var twcID = twc.id();
        FriendlyByteBuf buf = message.toBytes(routerID);
        RefractionMessages.sendToServer(new TWCPacket(twcID, buf));
    }

    private void sendToPlayer(
            TWC twc,
            String routerID,
            TWC.Message message,
            List<ServerPlayer> targets
    ) {
        var twcID = twc.id();
        FriendlyByteBuf buf = message.toBytes(routerID);
        for (ServerPlayer target : targets) RefractionMessages.sendToPlayer(new TWCPacket(twcID, buf), target);
    }

    private void writeRouters(FriendlyByteBuf buf) {
        buf.writeInt(ROUTERS.size());
        for (var entry : ROUTERS.entrySet()) buf.writeUUID(entry.getKey());
    }

    private void writeNamed(FriendlyByteBuf buf) {
        NAMED.entrySet().removeIf((entry) -> entry.getKey() == null || entry.getValue() == null);
        buf.writeInt(NAMED.size());
        for (var entry : NAMED.entrySet()) {
            buf.writeResourceLocation(entry.getKey());
            buf.writeUUID(entry.getValue());
        }
    }

    private void readRouters(FriendlyByteBuf buf) {
        int routerSize = buf.readInt();
        for (int i = 0; i < routerSize; i++) ROUTERS.put(buf.readUUID(), Optional.empty());
    }

    private void readNamed(FriendlyByteBuf buf) {
        var mapOld = new ConcurrentHashMap<>(NAMED);
        NAMED.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) NAMED.put(buf.readResourceLocation(), buf.readUUID());
        NAMED.entrySet().stream()
                .filter((entry) -> !mapOld.containsKey(entry.getKey()))
                .forEach((entry) -> RefractionClientEvents.TWC_NAMED_OPEN.invoker()
                        .onOpen(entry.getKey(), entry.getValue())
                );
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        writeRouters(buf);
        writeNamed(buf);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        readRouters(buf);
        readNamed(buf);
    }

    public static TWCMiddleWare instance() {
        return INSTANCE.get();
    }
}
