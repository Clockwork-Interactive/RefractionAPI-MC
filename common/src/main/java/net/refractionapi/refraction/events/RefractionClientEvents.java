package net.refractionapi.refraction.events;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.events.wrappers.ModelSet;
import net.refractionapi.refraction.feature.rendering.RenderDispatcherContext;
import net.refractionapi.refraction.gui.RIMTool;
import net.refractionapi.refraction.gui.cli.CLI;
import net.refractionapi.refraction.mixininterfaces.IParticleEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RefractionClientEvents {
    public static final RefractionEvent<RenderContext> BEFORE_ENTITIES = new RefractionEventCaller<>(RenderContext.class, listeners -> context -> {
        if (context == null) return;
        for (RenderContext listener : listeners) {
            listener.onRender(context);
        }
    });
    public static final RefractionEvent<RenderContext> POST_ALL = new RefractionEventCaller<>(RenderContext.class, listeners -> context -> {
        if (context == null) return;
        for (RenderContext listener : listeners) {
            listener.onRender(context);
        }
    });
    public static final RefractionEvent<RegisterParticles> REGISTER_PARTICLES = new RefractionEventCaller<>(RegisterParticles.class, listeners -> engine -> {
        for (RegisterParticles listener : listeners) {
            listener.onRegister(engine);
        }
    });
    public static final RefractionEvent<Generic> CLIENT_PLAYER_LEAVE = new RefractionEventCaller<>(Generic.class, listeners -> () -> {
        for (Generic listener : listeners) {
            listener.onEvent();
        }
    });
    public static final RefractionEvent<Generic> CLIENT_PLAYER_JOIN = new RefractionEventCaller<>(Generic.class, listeners -> () -> {
        for (Generic listener : listeners) {
            listener.onEvent();
        }
    });
    public static final RefractionEvent<NamedChannelOpen> NAMED_CHANNEL_OPEN = new RefractionEventCaller<>(NamedChannelOpen.class, listeners -> (id, uuid) -> {
        for (NamedChannelOpen listener : listeners) {
            listener.onOpen(id, uuid);
        }
    });
    public static final RefractionEvent<NamedChannelOpen> TWC_NAMED_OPEN = new RefractionEventCaller<>(NamedChannelOpen.class, listeners -> (id, uuid) -> {
        for (NamedChannelOpen listener : listeners) {
            listener.onOpen(id, uuid);
        }
    });
    public static final RefractionEvent<RegisterCLI> CLI_REGISTER = new RefractionEventCaller<>(RegisterCLI.class, listeners -> (cli) -> {
        for (RegisterCLI listener : listeners) {
            listener.register(cli);
        }
    });
    public static final RefractionEvent<Generic> FINISH_LOADING = new RefractionEventCaller<>(Generic.class, listeners -> () -> {
        for (Generic listener : listeners) {
            listener.onEvent();
        }
    });
    public static final RefractionEvent<RIMToolRegistry> RIMTOOLS_REGISTER = new RefractionEventCaller<>(RIMToolRegistry.class, listeners -> () -> {
        List<RIMTool> tools = new ArrayList<>();
        for (var listener : listeners) {
            var ret = listener.register();
            if (ret != null) tools.addAll(ret);
        }
        return tools;
    });
    public static final RefractionEvent<KeyInput> KEY_INPUT = new RefractionEventCaller<>(KeyInput.class, listeners -> (key, scancode, action, mods) -> {
        for (KeyInput listener : listeners) {
            listener.onKeyInput(key, scancode, action, mods);
        }
    });
    public static final RefractionEvent<RegisterLayer> REGISTER_LAYER = new RefractionEventCaller<>(RegisterLayer.class, listeners -> (context, modelSet) -> {
        for (RegisterLayer listener : listeners) {
            listener.register(context, modelSet);
        }
    });

    @FunctionalInterface
    public interface RegisterLayer {
        void register(RenderDispatcherContext context, ModelSet modelSet);
    }

    @FunctionalInterface
    public interface KeyInput {
        void onKeyInput(int key, int scancode, int action, int mods);
    }

    @FunctionalInterface
    public interface RIMToolRegistry {
        List<RIMTool> register();
    }

    @FunctionalInterface
    public interface GenericReturn<T> {
        T onEvent();
    }

    @FunctionalInterface
    public interface NamedChannelOpen {
        void onOpen(ResourceLocation id, UUID uuid);
    }

    @FunctionalInterface
    public interface Generic {
        void onEvent();
    }

    @FunctionalInterface
    public interface RegisterParticles {
        void onRegister(IParticleEngine engine);
    }

    @FunctionalInterface
    public interface RenderContext {
        void onRender(LevelRenderContext context);
    }

    @FunctionalInterface
    public interface RegisterCLI {
        void register(CLI cli);
    }
}
