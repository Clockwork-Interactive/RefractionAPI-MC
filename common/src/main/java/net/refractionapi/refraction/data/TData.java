package net.refractionapi.refraction.data;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.init.ModSpec;
import net.refractionapi.refraction.mixininterfaces.IServerPlayer;

import java.util.HashMap;

public abstract class TData {
    public final ModSpec id;
    private static final HashMap<ModSpec, TData> data = new HashMap<>();
    protected Player player;

    public TData(ModSpec id) {
        this.id = id;
        data.put(this.id, this);
    }

    public TData(ModSpec id, Player player) {
        this.id = id;
        this.player = player;
    }

    public abstract TData create(ModSpec spec, Player player);

    @SuppressWarnings("unchecked")
    public static <T extends TData> T get(Player player, String fromMod) {
        if (!(player instanceof ServerPlayer serverPlayer))
            throw new UnsupportedOperationException("Can't retrieve TData on client!");
        return serverPlayer instanceof IServerPlayer iServerPlayer ? (T) iServerPlayer.get(data.get(RModRegistrar.getSpec(fromMod))) : null;
    }

    public static <T extends TData> T get(Player player) {
        return get(player, RModRegistrar.getCallerModID());
    }

    public static <T extends TData> T get(Player player, Class<T> clazz) {
        return get(player, RModRegistrar.getCallerModID());
    }
}
