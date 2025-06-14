package net.refractionapi.refraction.data;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.scheme.ServerScheme;
import net.refractionapi.refraction.init.ModSpec;

public class PlrExtension extends TData {
    public ServerScheme scheme;

    public PlrExtension(ModSpec id) {
        super(id);
    }

    public PlrExtension(ModSpec id, Player player) {
        super(id, player);
    }

    @Override
    public TData create(ModSpec spec, Player player) {
        return new PlrExtension(spec, player);
    }
}
