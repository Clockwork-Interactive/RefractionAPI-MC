package net.refractionapi.refraction.feature.twc;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.util.Side;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * TODO:
 * logging
 */
public class TWC implements ITWC<TWC> {
    protected final UUID routerID;
    protected final ThreadLocal<ThreadSafeData> data = new ThreadLocal<>();
    protected final @Nullable ResourceLocation apiKey;
    private List<TWC.SidedConfigurer> configurer = new ArrayList<>();
    private Consumer<TWC> preConfigure = (nap) -> {
    };
    protected Function<Message, Message> preSendHook = (msg) -> msg;
    protected Function<Message, Message> preReceiveHook = (msg) -> msg;

    protected TWC(Level level) {
        this(level, UUID.randomUUID(), null);
    }

    protected TWC(Level level, UUID channelID) {
        this(level, channelID, null);
    }

    protected TWC(Level level, ResourceLocation apiKey) {
        this(level, UUID.randomUUID(), apiKey);
    }

    protected TWC(Level level, UUID routerID, @Nullable ResourceLocation apiKey) {
        this.routerID = routerID;
        this.apiKey = apiKey;
        data.set(new ThreadSafeData(level));
    }

    public static TWC named(Level level, ResourceLocation apiKey) {
        return new TWC(level, apiKey);
    }

    public static TWC unnamed(Level level) {
        return new TWC(level);
    }

    public static Sided unnamed() {
        return new Sided(null);
    }

    public static Sided named(ResourceLocation apiKey) {
        return new Sided(apiKey);
    }

    public TWC preSendHook(Function<Message, Message> hook) {
        this.preSendHook = hook;
        return this;
    }

    public TWC preReceiveHook(Function<Message, Message> hook) {
        this.preReceiveHook = hook;
        return this;
    }

    public TWC configure(Consumer<TWC> consumer) {
        this.configurer.add(new TWC.SidedConfigurer(Side.COMMON, consumer));
        return this;
    }

    public TWC configureServer(Consumer<TWC> consumer) {
        this.configurer.add(new TWC.SidedConfigurer(Side.SERVER, consumer));
        return this;
    }

    public TWC configureClient(Consumer<TWC> consumer) {
        this.configurer.add(new TWC.SidedConfigurer(Side.CLIENT, consumer));
        return this;
    }

    public TWC preConfigure(Consumer<TWC> consumer) {
        this.preConfigure = consumer;
        return this;
    }

    protected ThreadSafeData data() {
        return data.get();
    }

    public State channelState() {
        return data().channelState;
    }

    private void setChannelState(State state) {
        data().channelState = state;
    }

    public boolean isOpen() {
        return channelState().equals(State.OPEN);
    }

    public TWC open() {
        if (isOpen()) return this;
        setChannelState(State.OPEN);
        middleWare().open(this);
        postOpen();
        return this;
    }

    protected void postOpen() {
        this.preConfigure.accept(this);
        this.configurer.forEach((sidedConfigurer) -> {
            if (sidedConfigurer.side.equals(Side.COMMON)) {
                sidedConfigurer.configurer.accept(this);
                return;
            }
            if (level().isClientSide && sidedConfigurer.side.equals(Side.CLIENT))
                sidedConfigurer.configurer.accept(this);
            else if (!level().isClientSide && sidedConfigurer.side.equals(Side.SERVER))
                sidedConfigurer.configurer.accept(this);
        });
    }

    public TWC close() {
        if (!isOpen()) return this;
        setChannelState(State.CLOSED);
        middleWare().close(this);
        return this;
    }

    public TWC canReceiveFrom(Predicate<Player> predicate) {
        data().canReceiveFrom = predicate;
        return this;
    }

    public TWC markSendOnly() {
        data().canReceiveFrom = (plr) -> false;
        return this;
    }

    public TWC listener(String routerID, String listenerID, Listener listener) {
        router(routerID).listeners.put(listenerID, listener);
        return this;
    }

    /**
     * TWC allows for multiple listeners on the same router
     */
    public TWC listener(String id, Listener listener) {
        return listener(id, UUID.randomUUID().toString(), listener);
    }

    public TWC listener(Listener listener) {
        return listener("default", listener);
    }

    public TWC unregisterListener(String router, String id) {
        router(router).listeners.remove(id);
        return this;
    }

    public static Message message() {
        return new Message();
    }

    public void sendMessage(String routerID, ServerPlayer player, Message message) {
        message.twc = this;
        message.player = player;
        router(routerID).send(player, message);
    }

    public void sendMessage(String routerID, Message message) {
        message.twc = this;
        router(routerID).send(message);
    }

    public void post(String routerID, Message message, Consumer<Message> callback) {
        if (!level().isClientSide) throw new IllegalStateException("TWC#post can only be called from the client side.");
        safeRouter(routerID, (router) -> {
            String callbackID = routerID + "_response_" + data().msgID;
            // might want to unregister this after some n ticks --Zeus
            listener(callbackID, (response) -> {
                callback.accept(response);
                router.listeners.remove(callbackID);
            });
            message.header((header) -> {
                header.nbt().putString("response_router", callbackID);
            });
            router.send(message);
        });
    }

    protected void routeMessage(Message message) {
        if (!data().canReceiveFrom.test(message.player)) return;
        safeRouter(message.router, (router) -> router.receive(message));
    }

    private void safeRouter(String id, Consumer<Router> consumer) {
        safeRouter(id).ifPresentOrElse(
                consumer,
                () -> Refraction.LOGGER.warn("Unknown TWC Router ID [{}]", id)
        );
    }

    private Optional<Router> safeRouter(String id) {
        return Optional.ofNullable(data().ROUTERS.get(id));
    }

    private Router router(String id) {
        return data().ROUTERS.computeIfAbsent(id, (a) -> new Router(this, id));
    }

    public Level level() {
        return data().level;
    }

    public UUID id() {
        return routerID;
    }

    public TWCMiddleWare middleWare() {
        return TWCMiddleWare.instance();
    }

    public static class Sided implements ITWC<TWC> {
        ThreadLocal<TWC> instance = new ThreadLocal<>();
        final ResourceLocation apiKey;
        private final List<TWC.SidedConfigurer> configurer = new ArrayList<>();
        private Consumer<TWC> preConfigure = (nap) -> {
        };

        public Sided(ResourceLocation apiKey) {
            this.apiKey = apiKey;
        }

        public Sided initOnServer() {
            return initOnServer(this);
        }

        public static Sided initOnServer(Sided twc) {
            RefractionEvents.SERVER_STARTING.register((server) -> {
                if (twc.get() != null && twc.get().isOpen()) return;
                twc.open(server.overworld(), UUID.randomUUID());
            });
            return twc;
        }

        public Sided initOnClientRegistry() {
            return initOnClientRegistry(this);
        }

        public static Sided initOnClientRegistry(Sided twc) {
            RefractionClientEvents.TWC_NAMED_OPEN.register((rl, uuid) -> {
                if (rl.equals(twc.apiKey)) twc.open(ClientData.getPlayer().level(), uuid);
            });
            return twc;
        }

        public Sided initCommon() {
            return initCommon(this);
        }

        public static Sided initCommon(Sided twc) {
            return twc.initOnServer().initOnClientRegistry();
        }

        public void open(Level level, UUID uuid) {
            instance.set(new TWC(level, uuid, apiKey));
            instance.get().preConfigure = this.preConfigure;
            instance.get().configurer = this.configurer;
            instance.get().open();
        }

        public TWC get() {
            return instance.get();
        }

        @Override
        public void sendMessage(String routerID, ServerPlayer player, Message message) {
            instance.get().sendMessage(routerID, player, message);
        }

        @Override
        public void sendMessage(String routerID, Message message) {
            instance.get().sendMessage(routerID, message);
        }

        @Override
        public void post(String routerID, Message message, Consumer<Message> callback) {
            instance.get().post(routerID, message, callback);
        }

        public Sided configure(Consumer<TWC> consumer) {
            this.configurer.add(new TWC.SidedConfigurer(Side.COMMON, consumer));
            return this;
        }

        public Sided configureServer(Consumer<TWC> consumer) {
            this.configurer.add(new TWC.SidedConfigurer(Side.SERVER, consumer));
            return this;
        }

        public Sided configureClient(Consumer<TWC> consumer) {
            this.configurer.add(new TWC.SidedConfigurer(Side.CLIENT, consumer));
            return this;
        }

        public Sided preConfigure(Consumer<TWC> consumer) {
            this.preConfigure = consumer;
            return this;
        }
    }

    // here cause some things can mess with thread safety --Zeus
    protected class ThreadSafeData {
        final TWC twc = TWC.this;
        protected final HashMap<String, Router> ROUTERS = new HashMap<>();
        protected long msgID;
        protected final Level level;
        protected State channelState = State.CLOSED;
        protected Predicate<Player> canReceiveFrom = (player) -> true;

        public ThreadSafeData(Level level) {
            this.level = level;
        }
    }

    // header data order is irrelevant, so nbt it is --Zeus
        public record Header(CompoundTag nbt) {
    }

    public static class Message {
        private long id;
        private String router = "";
        private FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        private Header header = new Header(new CompoundTag());
        private Player player;
        private TWC twc;

        protected Message(TWC twc, Player player) {
            this.twc = twc;
            this.player = player;
        }

        public Message() {
            this(null, null);
        }

        public Message header(Consumer<Header> header) {
            header.accept(this.header);
            return this;
        }

        public Message header(Header header) {
            this.header = header;
            return this;
        }

        public Message buf(Consumer<FriendlyByteBuf> buf) {
            buf.accept(this.buf);
            return this;
        }

        public Message buf(FriendlyByteBuf buf) {
            this.buf = buf;
            return this;
        }

        public Message nbt(CompoundTag tag) {
            this.buf.writeNbt(tag);
            return this;
        }

        public Message nbt(Consumer<CompoundTag> tag) {
            var nbt = new CompoundTag();
            tag.accept(nbt);
            return nbt(nbt);
        }

        public CompoundTag nbt() {
            return buf.readNbt();
        }

        public FriendlyByteBuf buf() {
            return buf;
        }

        public Header header() {
            return header;
        }

        public Player player() {
            return player;
        }

        public Router router(TWC twc) {
            return twc.router(router);
        }

        public boolean isClient() {
            return twc.level().isClientSide;
        }

        protected FriendlyByteBuf toBytes(String router) {
            var fullBuf = new FriendlyByteBuf(Unpooled.buffer());
            fullBuf.writeLong(this.id = twc.data().msgID);
            fullBuf.writeUtf(this.router = router);
            fullBuf.writeNbt(header.nbt);
            fullBuf.writeBytes(buf);
            return fullBuf;
        }

        public static Message fromBytes(TWC twc, FriendlyByteBuf buf, @Nullable Player player) {
            var message = new Message(twc, player);
            message.id = buf.readLong();
            message.router = buf.readUtf();
            message.header = new Header(buf.readNbt());
            message.buf = new FriendlyByteBuf(Unpooled.buffer()).writeBytes(buf);
            return message;
        }
    }

    public static class Router {
        protected final String routerID;
        protected final TWC twc;
        // allowing multiple listeners could be nice for expansions
        // a bit of overhead with the map, but it allows us to
        // easily add/remove listeners, and some other cool things w/ more readability,
        // so the memory tradeoff is worth it imo --Zeus
        private final ConcurrentHashMap<String, Listener> listeners = new ConcurrentHashMap<>();
        private Message last;
        private long sentMessages;
        private long receivedMessages;

        public Router(TWC twc, String routerID) {
            this.twc = twc;
            this.routerID = routerID;
        }

        protected void receive(Message message) {
            last = message;
            for (Listener listener : listeners.values()) {
                listener.receive(message);
            }
            incReceivedMessages();
        }

        // debating if I should explicitly discern client/server sends --Zeus
        public void send(Message message) {
            List<ServerPlayer> players = new ArrayList<>();
            if (twc.level() instanceof ServerLevel serverLevel)
                players = serverLevel.getServer().getPlayerList().getPlayers();
            twc.middleWare().sendMessage(twc, routerID, message, players);
            incSentMessages();
        }

        public void send(Player player, Message message) {
            twc.middleWare().sendMessage(twc, routerID, message, List.of(player));
            incSentMessages();
        }

        public void respond(Message request, Message response) {
            String responseRouter = request.header.nbt.getString("response_router");
            if (responseRouter.isEmpty()) return;
            twc.middleWare().sendMessage(twc, responseRouter, response, List.of(request.player()));
        }

        public Message last() {
            return last;
        }

        private void incSentMessages() {
            twc.data().msgID++;
            sentMessages++;
        }

        private void incReceivedMessages() {
            twc.data().msgID++;
            receivedMessages++;
        }
    }

    public enum State {
        OPEN,
        CLOSED
    }


    public record SidedConfigurer(Side side, Consumer<TWC> configurer) {

    }

    @FunctionalInterface
    public interface Listener {
        void receive(Message message);
    }
}
