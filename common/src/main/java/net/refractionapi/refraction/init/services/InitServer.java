package net.refractionapi.refraction.init.services;

import net.minecraft.server.MinecraftServer;

/**
 * SERVER ONLY!
 * Register self with @AutoService(InitServer.class)
 * Implement start() to initialize on server start
 * Implement stop() to clean up on server stop
 * --Zeus
 */
public interface InitServer {
    void start(MinecraftServer server);

    void stop();
}
