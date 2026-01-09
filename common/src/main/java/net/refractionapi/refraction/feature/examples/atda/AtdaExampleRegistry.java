package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.feature.atda.Atda;

public class AtdaExampleRegistry {
    public static Atda<Player, AtdaExampleData> EXAMPLE = Atda.register(Player.class, "example");

    public static void init() {
        if (!RRuntimeConfig.debugTools) return;
        Atda.register(Player.class, EXAMPLE, new AtdaExampleProvider());
        Atda.syncOnLoad(EXAMPLE);
        Atda.registerCloning(EXAMPLE);
    }
}
