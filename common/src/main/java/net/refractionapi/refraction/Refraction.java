package net.refractionapi.refraction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.config.ExampleServerConfig;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.data.PlrExtension;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.debug.RDebugRenderers;
import net.refractionapi.refraction.events.RefractionEvent;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.events.Scheduler;
import net.refractionapi.refraction.feature.channel.SyncConfig;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.feature.config.RCRegister;
import net.refractionapi.refraction.feature.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;
import net.refractionapi.refraction.feature.examples.interaction.ExampleInteractionRegistry;
import net.refractionapi.refraction.feature.examples.reconfig.ReConfigExample;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenRegistry;
import net.refractionapi.refraction.feature.examples.screen.ExampleScreenScheme;
import net.refractionapi.refraction.feature.examples.subdivision.ExampleSubdivisionRegistry;
import net.refractionapi.refraction.feature.examples.task.ExampleTaskRegistry;
import net.refractionapi.refraction.feature.loader.LoadedChunkTracker;
import net.refractionapi.refraction.feature.quest.QuestHandler;
import net.refractionapi.refraction.feature.reconfig.ReConfigurer;
import net.refractionapi.refraction.feature.subdivision.Subdivision;
import net.refractionapi.refraction.feature.task.PlayerTasks;
import net.refractionapi.refraction.gui.RIMGuiInternal;
import net.refractionapi.refraction.gui.RIMServer;
import net.refractionapi.refraction.gui.cli.CLIComms;
import net.refractionapi.refraction.gui.tools.*;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.helper.command.DiscardCommand;
import net.refractionapi.refraction.helper.command.RDebugCommand;
import net.refractionapi.refraction.helper.command.RReConfigCommand;
import net.refractionapi.refraction.helper.command.SubdivisionCommand;
import net.refractionapi.refraction.helper.entity.FrozenManager;
import net.refractionapi.refraction.helper.registry.RBlocks;
import net.refractionapi.refraction.helper.registry.RItems;
import net.refractionapi.refraction.helper.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.helper.runnable.RunnableHandler;
import net.refractionapi.refraction.helper.runnable.Runnabler;
import net.refractionapi.refraction.helper.runnable.TickableProccesor;
import net.refractionapi.refraction.init.ClientReservice;
import net.refractionapi.refraction.init.Playground;
import net.refractionapi.refraction.init.Reprocessor;
import net.refractionapi.refraction.init.ServerReservice;
import net.refractionapi.refraction.platform.RefractionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Locale;

public class Refraction {
    public static final String MOD_ID = "refraction";
    public static final String MOD_NAME = "Refraction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Color refractionPrimary = new Color(20, 13, 26);
    public static final Color refractionSecondary = new Color(55, 20, 82);
    public static final RRuntimeConfig config = new RRuntimeConfig();
    public static final SyncConfig syncConfig = new SyncConfig()
            .setSyncer(config::sync);
    public static PlrExtension data;

    @SuppressWarnings("deprecation")
    public static void init() {
        if (RefractionServices.PLATFORM.isDevelopmentEnvironment()) Playground.init();
        RModRegistrar.registerSelf(MOD_ID);
        if (RefractionServices.PLATFORM.isClient()) ClientReservice.init();
        ServerReservice.init();
        Reprocessor.queueClient(reprocessor -> {
            reprocessor.registerClient(ExampleScreenScheme.class);
        });
        data = new PlrExtension(RModRegistrar.getSpec());
        RIMServer.init();
        RDebugRenderers.init();
        Runnabler.init();
        RunnableHandler.init();
        RunnableCooldownHandler.init();
        TickableProccesor.init();
        CutsceneHandler.init();
        FrozenManager.init();
        CLIComms.init();
        Scheduler.init();
        Subdivision.init();
        QuestHandler.init();

        ExampleInteractionRegistry.init();
        ExampleScreenRegistry.init();
        AtdaExampleRegistry.init();
        ExampleTaskRegistry.init();
        ExampleSubdivisionRegistry.init();
        RBlocks.init();
        RItems.init();

        RefractionEvents.LOAD_LEVEL.register((level) -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            LoadedChunkTracker.initTracker(serverLevel);
        });
        RefractionEvents.PLAYER_JOINED.register((player) -> {
            RefractionData.get(player);
            PlayerTasks.get(player);
        });
        RefractionEvents.PLAYER_CLONE.register(((current, old) -> {
            PlayerTasks.get(current);
        }));
        RefractionEvents.REGISTER_COMMANDS.register((c) -> {
            new RDebugCommand(c);
            new RReConfigCommand(c);
            new DiscardCommand(c);
            new SubdivisionCommand(c);
        });
        RefractionEvents.SERVER_STARTED.register(TwoWayIntermediary::init);
        RefractionEvents.SERVER_STARTED.register(RefractionEvent.Priority.LOWEST, (ms) -> RRuntimeConfig.serverStarted = true);
        if (RefractionServices.PLATFORM.isClient()) ClientData.load();
        ReConfigurer.registerCommon("refraction-common", ReConfigExample.builder);
        ReConfigurer.registerServer("refraction-server", RServerConfig.builder);
        RCRegister.registerServer("reconfig-server", ExampleServerConfig.EXAMPLE);
    }

    public static void startGui(long ptr) {
        if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac")) return;
        if (!RefractionServices.PLATFORM.isClient()) return;
        new RIMGuiInternal(
                ptr,
                new RIMDebuggers(),
                new RIMChannelAnalyzer(),
                new RIMNetworkActivity(),
                new RIMScreenInspector(),
                new RIMHealth(),
                new RIMCli()
        );
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.tryBuild(MOD_ID, id);
    }
}