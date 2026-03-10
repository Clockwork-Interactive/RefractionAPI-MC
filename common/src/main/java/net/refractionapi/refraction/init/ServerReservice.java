package net.refractionapi.refraction.init;

import net.minecraft.server.MinecraftServer;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.init.services.InitServer;

public class ServerReservice extends Reservice {
    private static final ServerReservice instance = new ServerReservice();
    private MinecraftServer server;

    public ServerReservice() {
        RefractionEvents.SERVER_STARTING.register(this::setupServices);
        RefractionEvents.SERVER_STOPPING.register(this::closeServices);
    }

    public void setupServices(MinecraftServer server) {
        this.server = server;
        setupServices();
    }

    @Override
    public void setupServices() {
        loadAndCache(InitServer.class).forEach(service -> service.start(server));
    }

    @Override
    public void closeServices() {
        getAndDumpCache(InitServer.class).forEach(InitServer::stop);
    }

    public static ServerReservice instance() {
        return instance;
    }

    public static void init() {

    }
}
