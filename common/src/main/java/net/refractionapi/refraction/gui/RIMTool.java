package net.refractionapi.refraction.gui;

import imgui.type.ImBoolean;
import net.refractionapi.refraction.feature.channel.NamedAPI;

public abstract class RIMTool {
    protected ImBoolean open = new ImBoolean(false);
    protected final String DEBUG = "Debugging";
    protected final String NETWORK = "Networking";

    public abstract void init();

    public void tick() {

    }

    public abstract void render();

    public abstract String name();

    public abstract String group();

    public NamedAPI api() {
        return RIMGuiInternal.gui.channel;
    }

    public void toggle() {
        this.open.set(!this.open.get());
    }
}
