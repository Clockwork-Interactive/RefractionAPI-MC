package net.refractionapi.refraction.feature.twc;

import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public interface ITWC<T extends ITWC<T>> {
    void sendMessage(String routerID, ServerPlayer player, TWC.Message message);

    void sendMessage(String routerID, TWC.Message message);

    void post(String routerID, TWC.Message message, Consumer<TWC.Message> callback);
}
