package net.refractionapi.refraction.feature.channel;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.TwoWayS2CPacket;
import net.refractionapi.refraction.util.Mutable;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A two-way channel that allows for communication between the server and client. <br>
 * Example usage: <br>
 * - {@link net.refractionapi.refraction.feature.examples.channel.ServerObject} <br>
 * - {@link net.refractionapi.refraction.feature.examples.channel.ClientObject} <br>
 */
public class TwoWayChannel {
    private static final String DEFAULT = "default";
    protected final UUID channelID;
    protected Status status = Status.CLOSED;
    protected Rule rule = Rule.ALL;
    protected Header HEADER = (router, buf) -> {
    };
    protected OnReceive onReceive = (player, routerID, header, buf) -> {
    };
    protected ConcurrentHashMap<String, Router> ROUTERS = new ConcurrentHashMap<>();
    protected ServerPlayer owner = null;
    protected TriFunction<Player, FriendlyByteBuf, String, Boolean> valid = (player, buf, router) -> true;
    protected Function<ServerPlayer, Boolean> canCommunicate = (player) -> owner == null || player == owner; // non-owner set instances can communicate with anyone
    protected boolean closeOnTerminate = false;
    protected final Level level;
    @Nullable
    protected TwoWayChannel.ReceivedHeader receivedHeader;
    protected Predicate<ServerPlayer> canSendTo = (player) -> true;
    protected boolean sendOnly = false;
    protected int id; // sent messages count
    protected final Int2ObjectArrayMap<Message> messages = new Int2ObjectArrayMap<>(); // sent messages

    public TwoWayChannel(Level level) {
        this(level, UUID.randomUUID());
    }

    /**
     * Usually used for client init
     */
    public TwoWayChannel(Level level, UUID channelID) {
        this.channelID = channelID;
        this.level = level;
    }

    public UUID id() {
        return this.channelID;
    }

    /**
     * If an owner set, the channel will only accept and send packets <br>
     * to the set owner
     */
    public TwoWayChannel owner(Entity owner) {
        if (!(owner instanceof ServerPlayer player)) throw new IllegalArgumentException("Owner must be a player");
        this.owner = player;
        this.rule(Rule.OWNER);
        return this;
    }

    /**
     * Register a sender and listener for this channel
     */
    public TwoWayChannel router(String id, @Nullable Listener listener, @Nullable Sender sender) {
        this.ROUTERS.put(id, new Router(id, listener, sender));
        return this;
    }

    /**
     * Register a listener with an id
     */
    public TwoWayChannel registerListener(String id, Listener listener) {
        this.ROUTERS.compute(id, (k, v) -> v == null ? new Router(id, listener, null) : new Router(id, listener, v.sender));
        return this;
    }

    /**
     * Register a sender with an id
     */
    public TwoWayChannel registerSender(String id, Sender sender) {
        this.ROUTERS.compute(id, (k, v) -> v == null ? new Router(id, null, sender) : new Router(id, v.listener, sender));
        return this;
    }

    /**
     * Register a listener on default id
     */
    public TwoWayChannel registerListener(Listener listener) {
        return this.registerListener(DEFAULT, listener);
    }

    /**
     * Register a sender on default id
     */
    public TwoWayChannel registerSender(Sender sender) {
        return this.registerSender(DEFAULT, sender);
    }

    /**
     * Register a router on default id
     */
    public TwoWayChannel router(Listener listener, Sender sender) {
        return this.router(DEFAULT, listener, sender);
    }

    public TwoWayChannel router(String id, Listener listener) {
        return this.router(id, listener, null);
    }

    /**
     * If a channel can receive packets
     */
    public TwoWayChannel valid(TriFunction<Player, FriendlyByteBuf, String, Boolean> valid) {
        this.valid = valid;
        return this;
    }

    /**
     * If a channel can send packets
     */
    public TwoWayChannel canSendTo(Predicate<ServerPlayer> canSendTo) {
        this.canSendTo = canSendTo;
        return this;
    }

    /**
     * If a channel can communicate with the given player
     */
    public TwoWayChannel canCommunicate(Function<ServerPlayer, Boolean> canCommunicate) {
        this.canCommunicate = canCommunicate;
        return this;
    }

    /**
     * Specify the header for sent packets
     */
    public TwoWayChannel header(Header header) {
        this.HEADER = header;
        return this;
    }

    public TwoWayChannel onRecieve(OnReceive onReceive) {
        this.onReceive = onReceive;
        return this;
    }

    /**
     * Get the header of the current packet
     */
    public ReceivedHeader header() {
        return this.receivedHeader;
    }

    public TwoWayChannel rule(Rule rule) {
        if (rule.equals(Rule.OWNER) && this.owner == null)
            throw new IllegalStateException("Owner must be set before setting rule to OWNER");
        this.rule = rule;
        return this;
    }

    public TwoWayChannel setSendOnly() {
        this.sendOnly = true;
        return this;
    }

    public ServerPlayer owner() {
        return this.owner;
    }

    public TwoWayChannel open() {
        if (this.isOpen()) throw new IllegalStateException("Channel is already open");
        if (this.channelID == null) throw new IllegalStateException("Listener ID must be set before opening channel");
        this.status = this.owner == null ? Status.OPEN : Status.COMMUNICATING; // we know a player will communicate
        this.instance().addChannel(this);
        return this;
    }

    public TwoWayChannel close() {
        if (this.isClosed()) throw new IllegalStateException("Channel is already closed");
        this.status = Status.CLOSED;
        TwoWayIntermediary instance = instance();
        if (isServer())
            terminate();
        instance.CHANNELS.remove(this.channelID);
        return this;
    }

    public TwoWayIntermediary instance() {
        return TwoWayIntermediary.instance(!level.isClientSide);
    }

    public void terminate() {
        if (this.level.isClientSide) throw new UnsupportedOperationException("Cannot terminate channel on client side");
        this.instance().terminate(this.channelID);
        NamedAPI.getChannel(this.channelID).ifPresent(NamedAPI::removeChannel);
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

    public boolean isServer() {
        return !this.level.isClientSide;
    }

    public boolean send(String routerID, Data data, TwoWayChannel.Header header, Rule.RuleConsumer rule) {
        if (this.isClosed()) return false;
        this.instance().sendTo(!this.level.isClientSide, routerID, this.channelID, data, header, rule);
        id++;
        return true;
    }

    public boolean send(Player player, String routerID, Data data, Header header) {
        if (!(player instanceof ServerPlayer s)) return false;
        return send(routerID, data, header, (c, uuid, headerBuf, dataBuf, ignored) -> RefractionMessages.sendToPlayer(new TwoWayS2CPacket(uuid, headerBuf, dataBuf), s));
    }

    public boolean send(String routerID, Data data, Header header) {
        return send(routerID, data, header, null);
    }

    public boolean send(String routerID, Data data) {
        return this.send(routerID, data, this.HEADER);
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

    /**
     * Allows for callback-able messages <br>
     * Useful for requesting data from the server
     */
    public void post(String routerID, Header header, Data data, OnReceive onRespond) {
        if (!this.level.isClientSide) {
            Refraction.LOGGER.error("Server usage of POST packets is explicitly forbidden!");
            return;
        }
        int sentID = this.id;
        Header idAble = (id, h) -> { // modify header to pass the packet id
            h.putString("post-r-set", "post-r-set");
            h.putInt("post-r-id", this.id);
            header.message(routerID, h);
        };
        this.send(routerID, data, idAble);
        if (++sentID != this.id) return; // in-case of id mismatch
        this.messages.get(--sentID).onRespond.set(Optional.of(onRespond));
    }

    /**
     * Allows for post-message callback
     * @param header pass {@link TwoWayChannel#header()} as a packet ID
     */
    public void respond(ReceivedHeader header, Data data) {
        CompoundTag headerBuf = header.header();
        String post = headerBuf.getString("post-r-set");
        int id = headerBuf.getInt("post-r-id");
        if (!post.equals("post-r-set")) return; // mis-input handling for post-packets
        this.send(header.player, header.router, data, (i, buf) -> {
            buf.putBoolean("post-r-res", true);
            buf.putInt("post-r-id", id); // packet id stored on the client
        });
    }

    private boolean handleRespond(Player player, String routerID, FriendlyByteBuf buf, CompoundTag receivedHeaderTag) {
        int id = receivedHeaderTag.getInt("post-r-id");
        if (receivedHeaderTag.contains("post-r-res")) { // POST-protocol handling
            if (player instanceof ServerPlayer s) { // a player shouldn't be able to send respond packets
                s.connection.disconnect(Component.literal("Illegal packet"));
                Refraction.LOGGER.info("Player {} sent an illegal packet (POST protocol)", player.getDisplayName().getString());
                return true;
            }
            assert this.messages.containsKey(id) : "Message ID doesn't exist %d".formatted(id);
            this.messages.get(id).onRespond().get().ifPresent((c) -> c.message(player, routerID, this.receivedHeader, new FriendlyByteBuf(buf.copy())));
            this.messages.remove(id); // prevent double-callbacks
            return true;
        }
        return false;
    }

    public void receive(@Nullable Player player, String routerID, FriendlyByteBuf header, FriendlyByteBuf buf) {
        if (this.isServer() && sendOnly && player != null) {
            Refraction.LOGGER.warn("Player {} tried sending a packet to a send-only channel", player.getDisplayName().getString());
            return;
        }
        this.receivedHeader = new ReceivedHeader(routerID, player, header.readNbt());
        if (this.isClosed() || !this.isCommunicating() || !this.valid.apply(player, new FriendlyByteBuf(buf.copy()), routerID))
            return;
        if (handleRespond(player, routerID, buf, receivedHeader.header)) return;
        Router router = this.ROUTERS.get(routerID);
        if (router == null) {
            Refraction.LOGGER.warn("Received message for unknown router: {}", routerID);
            return;
        }
        if (router.listener == null) return;
        this.onReceive.message(player, routerID, this.receivedHeader, new FriendlyByteBuf(buf.copy()));
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
        void message(String router, CompoundTag ct);
    }

    @FunctionalInterface
    public interface Data {
        void message(FriendlyByteBuf buf);
    }

    @FunctionalInterface
    public interface OnReceive {
        void message(Player player, String routerID, ReceivedHeader header, FriendlyByteBuf buf);
    }

    public record ReceivedHeader(String router, Player player, CompoundTag header) {
    }

    public record Router(String id, Listener listener, Sender sender) {
    }

    public record Message(int id, String router, FriendlyByteBuf data, FriendlyByteBuf header,
                          Mutable<Optional<OnReceive>> onRespond) {
        @Override
        public FriendlyByteBuf data() {
            return new FriendlyByteBuf(data.copy());
        }

        @Override
        public FriendlyByteBuf header() {
            return new FriendlyByteBuf(header.copy());
        }
    }

    public enum Status {
        OPEN,
        COMMUNICATING,
        CLOSED
    }

    public enum Rule {
        ALL((channel, uuid, header, buf, predicate) -> channel.level.getServer().getPlayerList().getPlayers().stream().filter(predicate).forEach(p -> RefractionMessages.sendToPlayer(new TwoWayS2CPacket(uuid, header, buf), p))),
        OWNER((channel, uuid, header, buf, predicate) -> RefractionMessages.sendToPlayer(new TwoWayS2CPacket(uuid, header, buf), predicate.test(channel.owner) ? channel.owner : null));
        final RuleConsumer syncer;

        Rule(RuleConsumer syncer) {
            this.syncer = syncer;
        }

        @FunctionalInterface
        public interface RuleConsumer {
            void accept(TwoWayChannel channel, UUID uuid, FriendlyByteBuf header, FriendlyByteBuf buf, Predicate<ServerPlayer> canSendTo);
        }
    }
}