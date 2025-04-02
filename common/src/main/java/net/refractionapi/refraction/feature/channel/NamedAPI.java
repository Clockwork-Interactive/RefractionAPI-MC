package net.refractionapi.refraction.feature.channel;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.data.Syncable;

import java.util.*;
import java.util.function.Consumer;

/**
 * Registers a {@link TwoWayChannel} with a name and allows for easy access to it <br>
 */
public class NamedAPI implements Syncable<NamedAPI> {
    private static final ThreadLocal<HashMap<ResourceLocation, UUID>> channels = ThreadLocal.withInitial(HashMap::new);
    private final SyncConfig syncConfig;
    private final ResourceLocation api;
    private final List<Consumer<TwoWayChannel>> configurer = new ArrayList<>();
    private TwoWayChannel channel;

    private NamedAPI(ResourceLocation apiID) {
        this.api = apiID;
        this.setSynced();
        this.syncConfig = new SyncConfig().setSyncer(this::sync);
    }

    public NamedAPI() {
        this.api = null;
        this.syncConfig = null;
    }

    public ResourceLocation id() {
        return api;
    }

    /**
     * Configure the api on channel open
     */
    public NamedAPI configure(Consumer<TwoWayChannel> consumer) {
        this.configurer.add(consumer);
        return this;
    }

    public static NamedAPI initOnServerStart(NamedAPI api) {
        RefractionEvents.SERVER_STARTING.register((server) -> {
            api.open(server.overworld());
        });
        return api;
    }

    /**
     * Specify this on server, automatically starts the API, when the server is on
     */
    public NamedAPI initOnServerStart() {
        return initOnServerStart(this);
    }

    public static NamedAPI initOnOpen(NamedAPI namedAPI) {
        RefractionClientEvents.NAMED_CHANNEL_OPEN.register((api, id) -> {
            if (api.equals(namedAPI.api)) namedAPI.open(ClientData.getPlayer().level(), id);
        });
        return namedAPI;
    }

    /**
     * Specify this on client, automatically starts the API, when the server has opened a client channel
     */
    public NamedAPI initOnOpen() {
        return initOnOpen(this);
    }

    /**
     * Specify this on a commonly used API <br>
     * WARNING: With an improperly configured common api <br>
     * It's easy to overlook security issues on the server <br>
     * Reference for a safe common API: {@link net.refractionapi.refraction.feature.reconfig.ReConfigurer ReConfig}
     */
    public ThreadedAPI initCommon() {
        return new ThreadedAPI(this);
    }

    /**
     * Usually you can use {@link NamedAPI#initOnServerStart()} (server)
     */
    public NamedAPI open(Level level) {
        this.channel = new TwoWayChannel(level);
        this.configurer.forEach((consumer) -> consumer.accept(this.channel));
        this.syncAllServer(level);
        this.channel.open();
        channels.get().put(this.api, this.channel.id());
        return this;
    }

    /**
     * Usually you can use {@link NamedAPI#initOnOpen()} (client)
     */
    public NamedAPI open(Level level, UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        this.channel = new TwoWayChannel(level, uuid);
        this.configurer.forEach((consumer) -> consumer.accept(this.channel));
        this.syncAll(level);
        this.channel.open();
        channels.get().put(this.api, this.channel.id());
        return this;
    }

    /**
     * Usually you can use {@link NamedAPI#initOnOpen()} (client)
     */
    public NamedAPI open(Level level, ResourceLocation api) {
        return open(level, getChannel(api));
    }

    public void close() {
        if (this.channel != null) {
            this.channel.close();
        }
    }

    public TwoWayChannel channel() {
        return this.channel;
    }

    public static UUID getChannel(ResourceLocation id) {
        return channels.get().get(id);
    }

    public static Optional<ResourceLocation> getChannel(UUID id) {
        return channels.get().entrySet().stream().filter((entry) -> entry.getValue().equals(id)).findFirst().map(Map.Entry::getKey);
    }

    public static void removeChannel(ResourceLocation rl) {
        channels.get().remove(rl);
    }

    /**
     * Static providers for client channels if they are non-static-accessible <br>
     * and the API key is known
     */
    public static void sendToServer(ResourceLocation id, String router, TwoWayChannel.Data data, TwoWayChannel.Header header) {
        HashMap<ResourceLocation, UUID> map = channels.get();
        UUID channel = map.get(id);
        if (channel != null) {
            TwoWayIntermediary.instance(false).sendTo(false, router, channel, data, header, null);
        }
    }

    public static void sendToServer(ResourceLocation id, String router, TwoWayChannel.Data data) {
        sendToServer(id, router, data, null);
    }

    public static void sendToServer(ResourceLocation id, String router, TwoWayChannel.Header header) {
        sendToServer(id, router, null, header);
    }

    public static void sendToServer(ResourceLocation id, String router) {
        sendToServer(id, router, null, null);
    }

    public static NamedAPI create(ResourceLocation id) {
        return new NamedAPI(id);
    }

    protected static NamedAPI create(NamedAPI api) {
        NamedAPI namedAPI = new NamedAPI(api.api);
        namedAPI.configurer.addAll(api.configurer);
        return namedAPI;
    }

    public static void clear() {
        channels.get().clear();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        HashMap<ResourceLocation, UUID> map = channels.get();
        buf.writeInt(map.size());
        map.entrySet().removeIf((entry) -> entry.getKey() == null || entry.getValue() == null);
        map.forEach((key, value) -> {
            buf.writeResourceLocation(key);
            buf.writeUUID(value);
        });
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        HashMap<ResourceLocation, UUID> map = channels.get();
        HashMap<ResourceLocation, UUID> mapOld = new HashMap<>(channels.get());
        map.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buf.readResourceLocation(), buf.readUUID());
        }
        map.entrySet().stream().filter((entry) -> !mapOld.containsKey(entry.getKey())).forEach((entry) -> RefractionClientEvents.NAMED_CHANNEL_OPEN.invoker().onOpen(entry.getKey(), entry.getValue()));
    }

    static {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(() -> {
            channels.get().clear();
        });
    }
}
