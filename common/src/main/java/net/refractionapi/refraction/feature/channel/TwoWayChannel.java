package net.refractionapi.refraction.feature.channel;

import jdk.jfr.Experimental;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.TwoWayS2CPacket;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A two-way channel that allows for communication between the server and client. <br>
 * Example usage: <br>
 * - {@link net.refractionapi.refraction.feature.examples.channel.ServerObject} <br>
 * - {@link net.refractionapi.refraction.feature.examples.channel.ClientObject} <br>
 * I haven't fully tested this for vulnerabilities, so marked as experimental. <br>
 * + I wrote most of this at 2am, so it might be a bit messy :P <br>
 */
@Experimental
public class TwoWayChannel {
    private static final String DEFAULT = "default";
    protected final UUID listenerID;
    protected Status status = Status.CLOSED;
    protected Rule rule = Rule.ALL;
    protected Header HEADER = (router, buf) -> {
    };
    protected OnRecieve onRecieve = (player, routerID, header, buf) -> {
    };
    protected ConcurrentHashMap<String, Router> ROUTERS = new ConcurrentHashMap<>();
    protected ServerPlayer owner = null;
    protected TriFunction<Player, FriendlyByteBuf, String, Boolean> valid = (player, buf, router) -> true;
    protected Function<ServerPlayer, Boolean> canCommunicate = (player) -> owner == null || player == owner; // non-owner set instances can communicate with anyone
    protected boolean closeOnTerminate = false;
    protected final Level level;
    @Nullable
    protected TwoWayChannel.ReceivedHeader receivedHeader;

    public TwoWayChannel(Level level) {
        this(level, UUID.randomUUID());
    }

    public TwoWayChannel(Level level, UUID listenerID) {
        this.listenerID = listenerID;
        this.level = level;
    }

    public UUID id() {
        return this.listenerID;
    }

    public TwoWayChannel owner(Entity owner) {
        if (!(owner instanceof ServerPlayer player)) throw new IllegalArgumentException("Owner must be a player");
        this.owner = player;
        this.rule(Rule.OWNER);
        return this;
    }

    public TwoWayChannel router(String id, @Nullable Listener listener, @Nullable Sender sender) {
        this.ROUTERS.put(id, new Router(id, listener, sender));
        return this;
    }

    public TwoWayChannel registerListener(String id, Listener listener) {
        this.ROUTERS.compute(id, (k, v) -> v == null ? new Router(id, listener, null) : new Router(id, listener, v.sender));
        return this;
    }

    public TwoWayChannel registerSender(String id, Sender sender) {
        this.ROUTERS.compute(id, (k, v) -> v == null ? new Router(id, null, sender) : new Router(id, v.listener, sender));
        return this;
    }

    public TwoWayChannel registerListener(Listener listener) {
        return this.registerListener(DEFAULT, listener);
    }

    public TwoWayChannel registerSender(Sender sender) {
        return this.registerSender(DEFAULT, sender);
    }

    public TwoWayChannel router(Listener listener, Sender sender) {
        return this.router(DEFAULT, listener, sender);
    }

    public TwoWayChannel router(String id, Listener listener) {
        return this.router(id, listener, null);
    }

    public TwoWayChannel valid(TriFunction<Player, FriendlyByteBuf, String, Boolean> valid) {
        this.valid = valid;
        return this;
    }

    public TwoWayChannel canCommunicate(Function<ServerPlayer, Boolean> canCommunicate) {
        this.canCommunicate = canCommunicate;
        return this;
    }

    public TwoWayChannel header(Header header) {
        this.HEADER = header;
        return this;
    }

    public TwoWayChannel onRecieve(OnRecieve onRecieve) {
        this.onRecieve = onRecieve;
        return this;
    }

    public ReceivedHeader header() {
        return this.receivedHeader;
    }

    public TwoWayChannel rule(Rule rule) {
        if (rule.equals(Rule.OWNER) && this.owner == null)
            throw new IllegalStateException("Owner must be set before setting rule to OWNER");
        this.rule = rule;
        return this;
    }

    public ServerPlayer owner() {
        return this.owner;
    }

    public TwoWayChannel open() {
        if (this.isOpen()) throw new IllegalStateException("Channel is already open");
        if (this.listenerID == null) throw new IllegalStateException("Listener ID must be set before opening channel");
        this.status = this.owner == null ? Status.OPEN : Status.COMMUNICATING; // we know a player will communicate
        this.instance().addChannel(this);
        return this;
    }

    public TwoWayChannel close() {
        if (this.isClosed()) throw new IllegalStateException("Channel is already closed");
        this.status = Status.CLOSED;
        TwoWayIntermediary instance = instance();
        instance.terminate(this.listenerID);
        instance.CHANNELS.remove(this.listenerID);
        return this;
    }

    public TwoWayIntermediary instance() {
        return TwoWayIntermediary.instance(!level.isClientSide);
    }

    public void terminate() {
        if (this.level.isClientSide) throw new UnsupportedOperationException("Cannot terminate channel on client side");
        this.instance().terminate(this.listenerID);
    }

    public TwoWayChannel closeOnTerminate() {
        this.closeOnTerminate = true;
        return this;
    }

    protected TwoWayChannel setCommunicating() {
        if (this.isClosed()) throw new IllegalStateException("Channel is closed");
        this.status = Status.COMMUNICATING;
        return this;
    }

    public boolean isOpen() {
        return this.status == Status.OPEN;
    }

    public boolean isCommunicating() {
        return this.status == Status.COMMUNICATING;
    }

    public boolean isClosed() {
        return this.status == Status.CLOSED;
    }

    public boolean send(String routerID, TwoWayChannel.Extra extra, TwoWayChannel.Header header) {
        if (this.isClosed()) return false;
        this.instance().sendTo(!this.level.isClientSide, routerID, this.listenerID, extra, header);
        return true;
    }

    public boolean send(String routerID, TwoWayChannel.Extra extra) {
        return this.send(routerID, extra, this.HEADER);
    }

    public boolean send(String routerID, TwoWayChannel.Header header) {
        return this.send(routerID, null, header);
    }

    public boolean send(String routerID) {
        return this.send(routerID, null, null);
    }

    public boolean send() {
        return this.send("default");
    }

    public void receive(@Nullable Player player, String routerID, FriendlyByteBuf header, FriendlyByteBuf buf) {
        this.receivedHeader = new ReceivedHeader(routerID, header);
        if (this.isClosed() || !this.isCommunicating() || !this.valid.apply(player, new FriendlyByteBuf(buf.copy()), routerID)) return;
        Router router = this.ROUTERS.get(routerID);
        if (router == null) {
            Refraction.LOGGER.warn("Received message for unknown router: {}", routerID);
            return;
        }
        if (router.listener == null) return;
        this.onRecieve.message(player, routerID, this.receivedHeader, new FriendlyByteBuf(buf.copy()));
        router.listener.handle(player, buf);
    }

    public boolean message(String routerID, FriendlyByteBuf buf) {
        if (this.isClosed()) return false;
        Router router = this.ROUTERS.get(routerID);
        if (router == null || router.sender == null) return false;
        router.sender.message(buf);
        return true;
    }

    @FunctionalInterface
    public interface Listener {
        int handle(Player player, FriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface Sender {
        void message(FriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface Header {
        void message(String router, FriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface Extra {
        void message(FriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface OnRecieve {
        void message(Player player, String routerID, ReceivedHeader header, FriendlyByteBuf buf);
    }

    public record ReceivedHeader(String router, FriendlyByteBuf header) {
        public FriendlyByteBuf header() {
            return new FriendlyByteBuf(this.header.copy());
        }
    }

    public record Router(String id, Listener listener, Sender sender) {
    }

    public enum Status {
        OPEN,
        COMMUNICATING,
        CLOSED
    }

    public enum Rule {
        ALL((channel, uuid, header, buf) -> channel.level.getServer().getPlayerList().getPlayers().forEach(p -> RefractionMessages.sendToPlayer(new TwoWayS2CPacket(uuid, header, buf), p))),
        OWNER((channel, uuid, header, buf) -> RefractionMessages.sendToPlayer(new TwoWayS2CPacket(uuid, header, buf), channel.owner));
        final RuleConsumer syncer;

        Rule(RuleConsumer syncer) {
            this.syncer = syncer;
        }

        @FunctionalInterface
        public interface RuleConsumer {
            void accept(TwoWayChannel channel, UUID uuid, FriendlyByteBuf header, FriendlyByteBuf buf);
        }
    }
}
