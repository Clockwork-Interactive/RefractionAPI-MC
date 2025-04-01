package net.refractionapi.refraction.feature.reconfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.SyncConfig;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.util.FileUtil;

import java.io.*;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Configuration system with multi-loader support <br>
 * Saved and loaded as JSON5, but the functionality of it is currently not being used.
 */
public class ReConfigurer {
    private static final LevelResource RECONFIG = FileUtil.createResource("reconfig");
    private static final HashMap<Side, HashMap<String, RCBuilder>> builders = new HashMap<>();
    public static final ResourceLocation CONFIG = Refraction.id("reconfig");
    protected static NamedAPI configurer = NamedAPI.create(CONFIG)
            .configure((channel) -> {
                channel.valid((plr, buf, route) -> !channel.isServer()); // only the server can send reconfig files --Zeus
                channel.registerListener(ReConfigurer::fromServer);
                channel.registerListener("single", ReConfigurer::fromServer);
                channel.registerSender(ReConfigurer::toClient);
            }).initCommon();
    protected static SyncConfig syncConfig = new SyncConfig()
            .setSyncer((entity) -> {
                configurer.sync(entity);
                syncCommonConfigs();
            });

    public static void registerServer(String name, RCBuilder builder) {
        put(Side.SERVER, name, builder.with(Side.SERVER, name, ""));
    }

    public static void registerCommon(String name, RCBuilder builder) {
        String file = "./reconfig/%s".formatted(name);
        builder.syncOnSave = true;
        put(Side.COMMON, name, builder.with(Side.COMMON, name, file)); // purely for syncing purposes --Zeus
        registerServer(name, builder);
        registerClient(name, builder.copy());
    }

    public static void registerClient(String name, RCBuilder builder) {
        if (!RefractionServices.PLATFORM.isClient()) return;
        String file = "./reconfig/%s".formatted(name);
        put(Side.CLIENT, name, builder.with(Side.CLIENT, name, file));
        load(file, builder); // we can directly load the client configs --Zeus
    }

    public static void reload(Side side) {
        builders.get(side).forEach((id, builder) -> builder.load());
    }

    private static void put(Side side, String id, RCBuilder builder) {
        builders.computeIfAbsent(side, (s) -> new HashMap<>()).put(id, builder);
        Refraction.LOGGER.info("Registered ReConfig file {} for {}", id, side.toString());
    }

    static void saveAll(Side side) {
        forSide(side, (info, builder) -> save(builder.file, builder));
    }

    static void prepareServerConfigs(MinecraftServer server) {
        forSide(Side.SERVER, (info, builder) -> {
            String saveName = "%s/%s".formatted(server.getWorldPath(RECONFIG).toString(), builder.name);
            builder.with(Side.SERVER, info, saveName);
        });
    }

    static void prepareCommonConfigs(MinecraftServer server) {
        forSide(Side.COMMON, (info, builder) -> {
            String saveName = "%s/%s".formatted(server.getWorldPath(RECONFIG).toString(), builder.name);
            builder.with(Side.COMMON, info, saveName);
        });
    }

    static void loadAll(Side side) {
        forSide(side, (info, builder) -> load(builder.file, builder));
    }

    static void forSide(Side side, BiConsumer<String, RCBuilder> consumer) {
        builders.computeIfAbsent(side, (s) -> new HashMap<>()).forEach(consumer);
    }

    static void clearSide(Side side) {
        forSide(side, (name, builder) -> builder.file = name);
    }

    private static JsonObject saveObject(File file, JsonObject object) {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            gson.toJson(object, writer);
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to save reconfig file {}", file.getName(), e);
        }
        return object;
    }

    private static JsonObject getJsonObject(File file) {
        try (InputStream stream = new FileInputStream(file)) {
            JsonReader reader = new JsonReader(new InputStreamReader(stream));
            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            return gson.fromJson(reader, TypeToken.get(JsonObject.class)).getAsJsonObject();
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to load reconfig file {}", file.getName(), e);
        }
        return null;
    }

    static void save(String file, RCBuilder builder) {
        File location = new File("%s.json5".formatted(file));
        JsonObject object = new JsonObject();
        try {
            builder.values().forEach((value) -> {
                if (!value.description().get().isEmpty()) object.addProperty("comment-%s".formatted(value.name().get()), value.description().get());
                value.value().serialize(value.name().get(), object);
            });
            if (!location.exists() && location.getParentFile().mkdirs()) {
                if (location.createNewFile())
                    Refraction.LOGGER.info("Created reconfig file {}", location.getName());
            }
            saveObject(location, object);
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to save reconfig file {}", location.getName(), e);
        }
    }

    private static JsonObject loadTrimmedJson(String file, RCBuilder builder) {
        File location = new File("%s.json5".formatted(file));
        JsonObject json = getJsonObject(location);
        assert json != null;
        json.asMap().forEach((name, element) -> { // remove any invalid values / comments --Zeus
            if (!builder.valueExists(name)) json.remove(name);
        });
        saveObject(location, json);
        return json;
    }

    static void load(String file, RCBuilder builder) {
        try {
            File location = new File("%s.json5".formatted(file));
            if (!location.exists()) {
                Refraction.LOGGER.debug("Reconfig file {} does not exist, creating it", location.getName());
                save(file, builder);
                return;
            }
            JsonObject json = loadTrimmedJson(file, builder);
            builder.values().forEach((value) -> {
                value.value().deserialize(value.name().get(), json);
            });
        } catch (Exception e) {
            Refraction.LOGGER.error("Couldn't load reconfig file {}", file, e);
        }
    }

    static void syncCommonConfigs() {
        configurer.channel().send();
    }

    protected static void syncCommonConfig(RCBuilder builder) {
        if (!builder.syncOnSave) return;
        configurer.channel().send("single", (buf) -> {
            compileConfig(new File("%s.json5".formatted(builder.file)), builder, buf);
        }, (routerID, header) -> header.putString("option", "single"));
    }

    static void toClient(FriendlyByteBuf buf) {
        configurer.channel().header((routerID, header) -> header.putString("option", "reconfig"));
        forSide(Side.COMMON, (name, builder) -> compileConfig(new File("%s.json5".formatted(builder.file)), builder, buf));
    }

    static void compileConfig(File file, RCBuilder builder, FriendlyByteBuf buf) {
        if (!file.exists()) {
            Refraction.LOGGER.error("Reconfig file {} does not exist, cannot send to client", file.getName());
            return;
        }
        JsonObject obj = getJsonObject(file);
        if (obj == null) return;
        buf.writeUtf(builder.name);
        buf.writeUtf(obj.toString());
    }

    static int fromServer(Player player, FriendlyByteBuf buf) {
        if (player != null) return 0; // should be impossible --Zeus
        String name = buf.readUtf();
        String json = buf.readUtf();
        JsonReader reader = new JsonReader(new StringReader(json));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject object = gson.fromJson(reader, TypeToken.get(JsonObject.class)).getAsJsonObject();
        saveObject(new File("./reconfig/%s.json5".formatted(name)), object);
        load("./reconfig/%s".formatted(name), builders.get(Side.COMMON).get(name));
        TwoWayChannel.ReceivedHeader header = configurer.channel().header();
        if (header != null && header.header().getString("option").equals("reconfig")) // first time loading the client configs --Zeus
            Refraction.LOGGER.info("Received reconfig file {} from server", name);
        return 1;
    }

    static {
        RefractionEvents.SERVER_STARTING.register((server) -> {
            prepareServerConfigs(server);
            prepareCommonConfigs(server);
            loadAll(Side.SERVER);
        });
        RefractionEvents.SERVER_STOPPING.register(() -> {
            saveAll(Side.SERVER);
            saveAll(Side.COMMON);
            clearSide(Side.SERVER);
        });
    }

    public enum Side {
        SERVER,
        COMMON,
        CLIENT
    }
}