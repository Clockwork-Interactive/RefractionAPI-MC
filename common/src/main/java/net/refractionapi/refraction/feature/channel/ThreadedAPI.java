package net.refractionapi.refraction.feature.channel;

import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.events.RefractionEvents;

public class ThreadedAPI {
    private final ThreadLocal<NamedAPI> api;

    public ThreadedAPI(NamedAPI api) {
        this.api = ThreadLocal.withInitial(() -> NamedAPI.create(api));
        this.init();
    }

    public NamedAPI get() {
        return api.get();
    }

    public TwoWayChannel channel() {
        return get().channel();
    }

    public void init() {
        RefractionEvents.SERVER_STARTING.register((server) -> {
            api.get().open(server.overworld());
        });
        RefractionClientEvents.NAMED_CHANNEL_OPEN.register((id, ptr) -> {
            if (id.equals(api.get().id()))
                api.get().open(ClientData.getPlayer().level(), ptr);
        });
    }
}
