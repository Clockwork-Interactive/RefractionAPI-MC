package net.refractionapi.refraction;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.config.RConfig;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.SyncConfig;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.feature.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;
import net.refractionapi.refraction.feature.examples.interaction.ExampleInteractionRegistry;
import net.refractionapi.refraction.feature.examples.reconfig.ReConfigExample;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.feature.reconfig.ReConfigurer;
import net.refractionapi.refraction.gui.*;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.helper.command.RDebugCommand;
import net.refractionapi.refraction.helper.entity.FrozenManager;
import net.refractionapi.refraction.helper.registry.item.RItems;
import net.refractionapi.refraction.helper.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.helper.runnable.RunnableHandler;
import net.refractionapi.refraction.helper.runnable.Runnabler;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.util.TestHooks;
import net.refractionapi.refraction.util.TestHooksClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class Refraction {
    public static final String MOD_ID = "refraction";
    public static final String MOD_NAME = "Refraction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Color refractionPrimary = new Color(20, 13, 26);
    public static final Color refractionSecondary = new Color(55, 20, 82);
    public static final RConfig config = new RConfig();
    public static final SyncConfig syncConfig = new SyncConfig()
            .setSyncer(config::sync);

    public static void init() {
        if (RefractionServices.PLATFORM.isDevelopmentEnvironment()) {
            RConfig.debugTools = true;
            new TestHooks();
            if (RefractionServices.PLATFORM.isClient()) {
                new TestHooksClient();
            }
        }
        register();
        RIMServer.init();
        Runnabler.init();
        RunnableHandler.init();
        RunnableCooldownHandler.init();
        TickableProccesor.init();
        CutsceneHandler.init();
        ExampleInteractionRegistry.init();
        ExampleScreenRegistry.init();
        RItems.init();
        AtdaExampleRegistry.init();
        FrozenManager.init();
        RefractionEvents.PLAYER_JOINED.register(RefractionData::get);
        RefractionEvents.REGISTER_COMMANDS.register((RDebugCommand::new));
        RefractionEvents.SERVER_STARTED.register(TwoWayIntermediary::init);
        if (RefractionServices.PLATFORM.isClient()) {
            ClientData.load();
        }
        ReConfigurer.registerCommon("refraction-common", ReConfigExample.builder);
    }

    public static void startGui(long ptr) {
        if (RefractionServices.PLATFORM.isClient()) {
            RIMGuiInternal.gui = new RIMGuiInternal(
                    ptr,
                    new RIMDebuggers(),
                    new RIMChannelAnalyzer(),
                    new RIMNetworkActivity(),
                    new RIMCommandExec()
            );
        }
    }

    private static void register() {
        RModRegistrar.registerSelf(MOD_ID);
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }
}