package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaUtils;

public class AtdaExampleRegistry {

    public static Atda<Player, AtdaExampleData> EXAMPLE = Atda.register("example");

    public static void init() {
        Atda.registerProvider(Player.class, (player) -> {
            AtdaUtils.attachAtda(player, AtdaExampleRegistry.EXAMPLE, new AtdaExampleProvider());
        });
        RefractionEvents.PLAYER_CLONE.register((current, old) -> {
            AtdaUtils.onClone(old, current, AtdaExampleRegistry.EXAMPLE);
        });
    }

}
