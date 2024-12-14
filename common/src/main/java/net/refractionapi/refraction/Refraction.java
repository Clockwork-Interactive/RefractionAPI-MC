package net.refractionapi.refraction;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.feature.examples.interaction.ExampleInteractionRegistry;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.helper.command.RDebugCommand;
import net.refractionapi.refraction.helper.registry.item.RItems;
import net.refractionapi.refraction.helper.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.helper.runnable.RunnableHandler;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import net.refractionapi.refraction.platform.RefractionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Refraction {

    public static final String MOD_ID = "refraction";
    public static final String MOD_NAME = "Refraction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static boolean debugTools = false;
    public static final Color refractionPrimary = new Color(20, 13, 26);
    public static final Color refractionSecondary = new Color(55, 20, 82);

    public static void init() {
        RModRegistrar.registerSelf(MOD_ID);
        if (RefractionServices.PLATFORM.isDevelopmentEnvironment()) {
            debugTools = true;
        }
        RunnableHandler.init();
        RunnableCooldownHandler.init();
        TickableProccesor.init();
        CutsceneHandler.init();
        ExampleInteractionRegistry.init();
        ExampleScreenRegistry.init();
        RItems.init();
        RefractionEvents.PLAYER_JOINED.register(RefractionData::get);
        RefractionEvents.REGISTER_COMMANDS.register((registrar -> {
            new RDebugCommand(registrar);
        }));
        if (RefractionServices.PLATFORM.isClient()) {
            ClientData.load();
        }
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }

}