package net.refractionapi.refraction.feature.examples.atda;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.atda.Atda;

public class AtdaExampleRegistry {

    public static Atda<Player, AtdaExampleData> EXAMPLE = Atda.register("example");

    public static void init() {

    }

}
