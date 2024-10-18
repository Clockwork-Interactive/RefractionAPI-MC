package net.refractionapi.refraction;

import net.refractionapi.refraction.feature.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.examples.interaction.ExampleInteractionRegistry;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.helper.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.helper.runnable.RunnableHandler;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Refraction {

    public static final String MOD_ID = "refraction";
    public static final String MOD_NAME = "Refraction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static boolean debugTools = false;

    public static void init() {
        RunnableHandler.init();
        RunnableCooldownHandler.init();
        TickableProccesor.init();
        CutsceneHandler.init();
        ExampleInteractionRegistry.init();
        ExampleScreenRegistry.init();
        RefractionEvents.PLAYER_JOINED.register(RefractionData::get);
        if (RefractionServices.PLATFORM.isDevelopmentEnvironment()) {
            debugTools = true;
        }
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }

}