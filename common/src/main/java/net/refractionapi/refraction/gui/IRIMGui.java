package net.refractionapi.refraction.gui;

public interface IRIMGui {
    void beginFrame();

    void endFrame();

    boolean onMouseButton(long window, int button, int action, int mods);

    boolean onScroll(double xoffset, double yoffset);

    void onGrabMouse();

    boolean onKey(long window, int key, int scancode, int action, int mods);

    boolean onChar(long window, int codepoint, int mod);

    void toggle();

    <T extends RIMTool> T byNameAndGroup(String name, String grouo);
}
