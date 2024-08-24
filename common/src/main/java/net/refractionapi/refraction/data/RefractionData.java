package net.refractionapi.refraction.data;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.event.RefractionCommonData;
import net.refractionapi.refraction.screen.ScreenBuilder;
import net.refractionapi.refraction.screen.ServerScreen;

import java.util.Optional;

public class RefractionData {

    public final Player player;
    public ServerScreen screen;
    public ScreenBuilder<?> builder;

    public Optional<ServerScreen> getScreen() {
        return screen == null || builder == null ? Optional.empty() : Optional.of(this.screen);
    }

    public RefractionData(Player player) {
        this.player = player;
    }

    public static RefractionData get(Player player) {
        return RefractionCommonData.runtimeData.computeIfAbsent(player, RefractionData::new);
    }

}
