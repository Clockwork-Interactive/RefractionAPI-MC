package net.refractionapi.refraction.init;

import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.init.services.InitClient;

public class ClientReservice extends Reservice {
    private static final ClientReservice instance = new ClientReservice();

    @Override
    public void setupServices() {
        loadAndCache(InitClient.class);
    }

    @Override
    public void closeServices() {
        getAndDumpCache(InitClient.class).forEach(InitClient::close);
    }

    public static ClientReservice instance() {
        return instance;
    }

    public static void init() {

    }

    static {
        RefractionClientEvents.CLIENT_PLAYER_JOIN.register(instance::setupServices);
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(instance::closeServices);
    }
}
