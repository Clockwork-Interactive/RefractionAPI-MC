package net.refractionapi.refraction;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.platform.RefractionServices;

@Mod(Refraction.MOD_ID)
public class RefractionForge {
    
    public RefractionForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        Refraction.init();
        if (RefractionServices.PLATFORM.isClient()) {
            RefractionServices.EVENTS.registerOverlays();
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> RefractionMessages.register());
    }

}