package net.refractionapi.refraction.config;

import net.minecraft.network.FriendlyByteBuf;
import net.refractionapi.refraction.feature.data.Syncable;

public class RConfig implements Syncable<RConfig> {
    public static boolean debugTools = false;

    public RConfig() {
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
