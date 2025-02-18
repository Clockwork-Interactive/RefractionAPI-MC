package net.refractionapi.refraction.client;

import net.minecraft.client.Minecraft;
import net.refractionapi.refraction.feature.examples.reconfig.ReConfigExample;
import net.refractionapi.refraction.feature.reconfig.ReConfigurer;
import net.refractionapi.refraction.helper.vfx.VFXer;
import net.refractionapi.refraction.util.InitSelf;

import java.io.File;

@InitSelf
public class RefractionClient {
    private static RefractionClient INSTANCE;

    public RefractionClient() {
        INSTANCE = this;
        VFXer.init();
    }

    public File minecraftDir() {
        return Minecraft.getInstance().gameDirectory;
    }

    public static RefractionClient instance() {
        return INSTANCE;
    }
}
