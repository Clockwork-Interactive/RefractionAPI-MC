package net.refractionapi.refraction.client;

import com.google.auto.service.AutoService;
import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.helper.vfx.VFXer;
import net.refractionapi.refraction.init.services.InitClient;

import java.io.File;

@AutoService(InitClient.class)
public class RefractionClient implements InitClient {
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
        VFXer.init();
    }

    @Override
    public void close() {
        ClientData.reset();
    }
}
