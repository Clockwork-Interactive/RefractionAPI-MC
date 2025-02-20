package net.refractionapi.refraction.gui;

import java.util.UUID;

public interface IRIMGui {
    void beginFrame();

    void endFrame();

    boolean onMouseButton(long window, int button, int action, int mods);

    boolean onScroll(double xoffset, double yoffset);

    void onGrabMouse();

    boolean onKey(long window, int key, int scancode, int action, int mods);

    boolean onChar(long window, int codepoint, int mod);

    void toggle();

    void assignChannel(UUID uuid);

    public <T extends RIMTool> T byNameAndGroup(String name, String grouo);
}
