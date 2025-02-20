package net.refractionapi.refraction.gui;

import java.util.UUID;

public class RIMNone implements IRIMGui {
    @Override
    public void beginFrame() {

    }

    @Override
    public void endFrame() {

    }

    @Override
    public boolean onMouseButton(long window, int button, int action, int mods) {
        return false;
    }

    @Override
    public boolean onScroll(double xoffset, double yoffset) {
        return false;
    }

    @Override
    public void onGrabMouse() {

    }

    @Override
    public boolean onKey(long window, int key, int scancode, int action, int mods) {
        return false;
    }

    @Override
    public boolean onChar(long window, int codepoint, int mod) {
        return false;
    }

    @Override
    public void toggle() {

    }

    @Override
    public void assignChannel(UUID uuid) {

    }

    @Override
    public <T extends RIMTool> T byNameAndGroup(String name, String grouo) {
        return null;
    }
}
