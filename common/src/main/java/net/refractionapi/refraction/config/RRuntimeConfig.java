package net.refractionapi.refraction.config;

import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.feature.data.Syncable;

public class RRuntimeConfig implements Syncable<RRuntimeConfig> {
    public static boolean debugTools = false;

    public RRuntimeConfig() {
        this.setSynced();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(debugTools);
    }

    @Override
    public void read(FriendlyByteBuf buf) {
        debugTools = buf.readBoolean();
    }
}
