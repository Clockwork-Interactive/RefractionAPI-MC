package net.refractionapi.refraction.mixininterfaces;

import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.data.TData;

public interface IServerPlayer {
    TData get(TData data);

    RefractionData get();
}
