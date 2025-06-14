package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.helper.vfx.VFXer;
import net.refractionapi.refraction.util.InitSelf;

import java.io.File;

@InitSelf(false)
public class RefractionClient {
    private static RefractionClient INSTANCE;
    public static ScreenRegistry screenRegistry = new ScreenRegistry();

    public RefractionClient() {
        INSTANCE = this;
    }

    public File minecraftDir() {
        return Minecraft.getInstance().gameDirectory;
    }

    public static RefractionClient instance() {
        return INSTANCE;
    }

    static {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientData::reset);
        VFXer.init();
    }
}
