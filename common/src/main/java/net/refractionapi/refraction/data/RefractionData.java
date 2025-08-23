package net.refractionapi.refraction.data;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.scheme.ServerScheme;
import net.refractionapi.refraction.feature.screen.ScreenBuilder;
import net.refractionapi.refraction.feature.screen.ServerScreen;
import net.refractionapi.refraction.feature.task.PlayerTasks;
import net.refractionapi.refraction.mixininterfaces.IServerPlayer;

import java.util.Optional;

@SuppressWarnings("all")
public class RefractionData {
    public final Player player;
    public ServerScreen screen;
    public ScreenBuilder<?> builder;
    public ServerScheme scheme;
    public PlayerTasks tasks;

    public Optional<ServerScreen> getScreen() {
        return screen == null || builder == null ? Optional.empty() : Optional.of(this.screen);
    }

    public RefractionData(Player player) {
        this.player = player;
    }

    public static RefractionData get(Player player) {
        return player instanceof IServerPlayer serverPlayer ? serverPlayer.get() : null;
    }
}
