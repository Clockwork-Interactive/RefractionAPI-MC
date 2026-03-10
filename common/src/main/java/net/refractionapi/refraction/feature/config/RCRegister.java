package net.refractionapi.refraction.feature.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.channel.SyncConfig;
import net.refractionapi.refraction.feature.twc.TWC;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.util.FileUtil;
import net.refractionapi.refraction.util.Side;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RCRegister {
    private static final LevelResource RECONFIG = FileUtil.createResource("reconfig");
    private static final ThreadLocal<ConcurrentHashMap<Side, RConfigurers>> CONFIGURERS = ThreadLocal.withInitial(ConcurrentHashMap::new);
    private static final ResourceLocation KEY = Refraction.id("reconfig");
    private static final TWC.Sided API = TWC.named(KEY)
            .configureServer((twc) -> {
                twc.markSendOnly();
                provokeServer(twc.level());
            })
            .configureClient((twc) -> {
                twc.listener("sync", RCRegister::deserializeSync);
                twc.listener("reload", RCRegister::reload);
            }).initCommon();
    private static final SyncConfig SYNC = new SyncConfig()
            .setSyncer(RCRegister::syncToClient);

    private static void provokeServer(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        configurers(Side.SERVER).configs.forEach((id, config) -> {
            loadFromFile(config);
        });
        configurers(Side.COMMON).configs.forEach((id, config) -> {
            loadFromFile(config);
            syncToClients(config);
        });
    }

    public static void registerServer(String fileName, Reconfig config) {
        register(Side.SERVER, fileName, config);
    }

    public static void registerClient(String fileName, Reconfig config) {
        if (!RefractionServices.PLATFORM.isClient()) return;
        register(Side.CLIENT, fileName, config);
        loadFromFile(config);
    }

    public static void registerCommon(String fileName, Reconfig config) {
        configurers(Side.COMMON).add(fileName, config);
        registerServer(fileName, config);
        registerClient(fileName, new Reconfig(config.builder));
    }

    private static void register(Side side, String fileName, Reconfig config) {
        var configurers = configurers(side);
        if (configurers.exists(fileName))
            throw new IllegalArgumentException("Reconfig file already registered: %s | %s".formatted(fileName, side));
        configurers.add(fileName, config);
        if (!config.addToManager(fileName, side))
            throw new IllegalArgumentException("Reconfig already added to manager: %s | %s".formatted(fileName, side));
    }

    protected static void compileBuf(Reconfig reconfig, FriendlyByteBuf byteBuf) {
        for (var value : reconfig.values()) value.encodeValue(byteBuf);
    }

    private static void syncToClients(Reconfig config) {
        if (!config.isRegistered()) throw new IllegalStateException("Cannot sync unregistered config!");
        API.sendMessage("sync", msg(config));
    }

    private static void syncToClient(ServerPlayer serverPlayer, Reconfig config) {
        if (!config.isRegistered()) throw new IllegalStateException("Cannot sync unregistered config!");
        API.sendMessage("sync", serverPlayer, msg(config));
    }

    private static void syncToClient(Entity entity) {
        if (!(entity instanceof ServerPlayer serverPlayer)) return;
        var configurers = configurers(Side.COMMON);
        configurers.configs.forEach((id, config) -> syncToClient(serverPlayer, config));
    }

    private static TWC.Message msg(Reconfig config) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeUtf(config.fileName);
        compileBuf(config, buf);
        return TWC.message().buf(buf);
    }

    /**
     * buf struct:
     * - utf fileName
     * - values...
     */
    private static void deserializeSync(TWC.Message message) {
        var buf = message.buf();
        var fileName = buf.readUtf();
        var configs = configurers(Side.CLIENT);
        var config = configs.get(fileName);
        if (config == null) {
            Refraction.LOGGER.warn("Received reconfig sync for unregistered file: {}", fileName);
            return;
        }
        config.decode(buf);
        saveToFile(config);
    }

    private static JsonObject getJsonObject(File file) {
        try (InputStream stream = new FileInputStream(file)) {
            var reader = new JsonReader(new InputStreamReader(stream));
            var gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            return gson.fromJson(reader, TypeToken.get(JsonObject.class)).getAsJsonObject();
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to load reconfig file {}", file.getName(), e);
        }
        return null;
    }

    private static JsonObject loadTrimmed(Reconfig config) {
        var obj = new JsonObject();
        var file = file(config);
        if (!file.exists()) return obj;
        var json = getJsonObject(file);
        if (json == null) throw new IllegalStateException("Failed to load reconfig file: " + file.getName());
        new ConcurrentHashMap<>(json.asMap()).forEach((name, element) -> {
            if (!config.valueExists(name)) json.remove(name);
        });
        return json;
    }

    private static void saveToFile(Reconfig config) {
        if (!config.isRegistered()) throw new IllegalStateException("Cannot save unregistered config!");
        var file = file(config);
        var json = new JsonObject();
        for (var value : config.values()) value.write(json);
        FileUtil.writeJsonToFile(file, json);
    }

    public static void loadFromFile(Reconfig config) {
        if (!config.isRegistered()) throw new IllegalStateException("Cannot load unregistered config!");
        var trimmed = loadTrimmed(config);
        for (var value : config.values()) value.read(trimmed);
    }

    /**
     * buf struct:
     * - enum side
     */
    private static void reload(TWC.Message message) {

    }

    private static File file(Reconfig reconfig) {
        return new File(filePath(reconfig));
    }

    private static String filePath(Reconfig config) {
        return "./%s/%s.json5".formatted("reconfig" + (config.isServer() ? "/server" : "/client"), config.fileName);
    }

    private static RConfigurers configurers(Side side) {
        return CONFIGURERS.get().computeIfAbsent(side, (s) -> new RConfigurers(side, new HashMap<>()));
    }

    private record RConfigurers(Side side, HashMap<String, Reconfig> configs) {
        public Reconfig get(String fileName) {
            return configs.get(fileName);
        }

        public void add(String fileName, Reconfig config) {
            Reconfig inst;
            configs.put(fileName, inst = new Reconfig(config.builder));
            inst.addToManager(fileName, side());
        }

        public boolean exists(String fileName) {
            return configs.containsKey(fileName);
        }
    }
}