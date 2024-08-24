package net.refractionapi.refraction;

import net.refractionapi.refraction.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.runnable.RunnableHandler;
import net.refractionapi.refraction.runnable.TickableProccesor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Refraction {

    public static final String MOD_ID = "refraction";
    public static final String MOD_NAME = "Refraction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        RunnableHandler.init();
        RunnableCooldownHandler.init();
        TickableProccesor.init();
        CutsceneHandler.init();
    }

}