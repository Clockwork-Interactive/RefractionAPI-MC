package net.refractionapi.refraction.feature.channel;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.data.Syncable;
import org.apache.logging.log4j.util.InternalApi;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class NamedAPI implements Syncable<NamedAPI> {
    private static final HashMap<ResourceLocation, UUID>[] channels = new HashMap[]{new HashMap<>(), new HashMap<>()};
    private final SyncConfig syncConfig;
    private final ResourceLocation api;
    private Consumer<TwoWayChannel> configurer = (channel) -> {
    };
    private TwoWayChannel channel;

    private NamedAPI(ResourceLocation apiID) {
        this.api = apiID;
        this.setSynced();
        this.syncConfig = new SyncConfig().setSyncer(this::sync);
    }

    @InternalApi
    public NamedAPI() {
        this.api = null;
        this.syncConfig = null;
    }

    public NamedAPI configure(Consumer<TwoWayChannel> consumer) {
        this.configurer = consumer;
        return this;
    }

    public NamedAPI open(Level level) {
        this.channel = new TwoWayChannel(level);
        this.configurer.accept(this.channel);
        this.syncAll(level);
        this.channel.open();
        channels[0].put(this.api, this.channel.id());
        return this;
    }

    public NamedAPI open(Level level, UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        this.channel = new TwoWayChannel(level, uuid);
        this.configurer.accept(this.channel);
        this.syncAll(level);
        this.channel.open();
        channels[1].put(this.api, this.channel.id());
        return this;
    }

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
        return channels[1].get(id);
    }

    public static void sendToServer(ResourceLocation id, String router, TwoWayChannel.Extra extra, TwoWayChannel.Header header) {
        HashMap<ResourceLocation, UUID> map = channels[1];
        UUID channel = map.get(id);
        if (channel != null) {
            TwoWayIntermediary.instance(false).sendTo(false, router, channel, extra, header);
        }
    }

    public static void sendToServer(ResourceLocation id, String router, TwoWayChannel.Extra extra) {
        sendToServer(id, router, extra, null);
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

    @Override
    public void write(FriendlyByteBuf buf) {
        HashMap<ResourceLocation, UUID> map = channels[0];
        buf.writeInt(map.size());
        map.forEach((key, value) -> {
            buf.writeResourceLocation(key);
            buf.writeUUID(value);
        });
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        HashMap<ResourceLocation, UUID> map = channels[1];
        HashMap<ResourceLocation, UUID> mapOld = new HashMap<>(channels[1]);
        map.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            map.put(buf.readResourceLocation(), buf.readUUID());
        }
        map.entrySet().stream().filter((entry) -> !mapOld.containsKey(entry.getKey())).forEach((entry) -> RefractionClientEvents.NAMED_CHANNEL_OPEN.invoker().onOpen(entry.getKey(), entry.getValue()));
    }

    static {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(() -> {
            channels[1].clear();
        });
    }
}
