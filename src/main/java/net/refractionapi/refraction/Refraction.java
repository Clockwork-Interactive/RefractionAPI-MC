package net.refractionapi.refraction;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.refractionapi.refraction.networking.ModMessages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Refraction.MOD_ID)
public class Refraction {
    public static final Logger LOGGER = LogManager.getLogger("Refraction API");
    public static final String MOD_ID = "refraction";

    public Refraction() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }

}