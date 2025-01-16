package net.refractionapi.refraction.helper.vfx;

import net.refractionapi.refraction.events.LevelRenderContext;

import java.util.function.BiConsumer;

public abstract class VFXWrapper<T extends VFXWrapper<T>> {
    private BiConsumer<T, LevelRenderContext> updater = (vfxWrapper, levelRenderContext) -> {
    };
    protected final VFXer vfXer;

    public VFXWrapper(VFXer vfXer) {
        this.vfXer = vfXer;
    }

    @SuppressWarnings("unchecked")
    public T onRender(BiConsumer<T, LevelRenderContext> updater) {
        this.updater = updater;
        return (T) this;
    }

    public BiConsumer<T, LevelRenderContext> updater() {
        return this.updater;
    }

    public abstract T spawn();

    public abstract void remove();
}
