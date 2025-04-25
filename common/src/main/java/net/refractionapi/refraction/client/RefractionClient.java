package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.helper.vfx.VFXer;
import net.refractionapi.refraction.util.InitSelf;

import java.io.File;

@InitSelf
public class RefractionClient {
    private static RefractionClient INSTANCE;
    public static ScreenRegistry screenRegistry = new ScreenRegistry();

    public RefractionClient() {
        INSTANCE = this;
        VFXer.init();
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientData::reset);
    }

    public File minecraftDir() {
        return Minecraft.getInstance().gameDirectory;
    }

    public static RefractionClient instance() {
        return INSTANCE;
    }
}
