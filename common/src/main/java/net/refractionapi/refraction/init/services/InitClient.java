package net.refractionapi.refraction.init.services;

/**
 * CLIENT ONLY!
 * Register self with @AutoService(InitClient.class)
 * Implement close() to clean up on unload
 * --Zeus
 */
public interface InitClient {
    void close();
}
