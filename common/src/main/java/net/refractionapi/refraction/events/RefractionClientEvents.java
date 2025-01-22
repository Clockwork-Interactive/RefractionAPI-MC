package net.refractionapi.refraction.events;

import net.refractionapi.refraction.mixininterfaces.IParticleEngine;

public class RefractionClientEvents {
    public static final RefractionEvent<RenderContext> BEFORE_ENTITIES = new RefractionEventCaller<>(RenderContext.class, listeners -> context -> {
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
}
