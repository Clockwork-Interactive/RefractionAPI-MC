package net.refractionapi.refraction.client;

import net.refractionapi.refraction.helper.vfx.VFXer;

public class RefractionClient {
    private static RefractionClient INSTANCE;

    public RefractionClient() {
        INSTANCE = this;
        VFXer.init();
    }

    public static RefractionClient instance() {
        return INSTANCE;
    }
}
