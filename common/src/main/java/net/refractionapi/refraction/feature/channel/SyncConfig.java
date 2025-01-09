package net.refractionapi.refraction.feature.channel;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.events.RefractionEvents;

public class SyncConfig {
    private Syncer syncer;

    public SyncConfig() {
        RefractionEvents.PLAYER_JOINED.register(serverPlayer -> {
            if (this.syncer != null)
                this.syncer.sync(serverPlayer);
        });
    }

    public Syncer getSyncer() {
        return syncer;
    }

    public void syncAll(ServerLevel server) {
        if (server != null && this.syncer != null) {
            server.getServer().getPlayerList().getPlayers().forEach(this.syncer::sync);
        }
    }

    public SyncConfig setSyncer(Syncer syncer) {
        this.syncer = syncer;
        return this;
    }

    @FunctionalInterface
    public interface Syncer {
        void sync(Entity entity);
    }
}
