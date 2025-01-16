package net.refractionapi.refraction.helper.vfx;

import net.refractionapi.refraction.helper.runnable.Runnabler;

public class VFXRenderer extends VFXWrapper<VFXRenderer> {
    private int ticks = -1;

    public VFXRenderer(VFXer vfXer) {
        super(vfXer);
    }

    public VFXRenderer ticks(int ticks) {
        this.ticks = ticks;
        return this;
    }

    @Override
    public VFXRenderer spawn() {
        VFXer.activeWrappers.add(this);
        if (this.ticks != -1) {
            Runnabler.createClient()
                    .delayRun(this.ticks, (runnabler) -> this.remove());
        }
        return this;
    }

    @Override
    public void remove() {
        VFXer.activeWrappers.remove(this);
    }
}
