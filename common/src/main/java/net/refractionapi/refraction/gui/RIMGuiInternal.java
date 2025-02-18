package net.refractionapi.refraction.gui;

import imgui.ImFont;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.internal.ImGuiContext;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.glfwGetCurrentContext;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

@ApiStatus.Internal
public class RIMGuiInternal implements NativeResource, IRIMGui {
    private static final RIMNone none = new RIMNone();
    public static RIMGuiInternal gui;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private ImGuiContext context;
    private HashSet<RIMTool> tools;
    private boolean active;
    protected NamedAPI channel;

    public RIMGuiInternal(long ptr, RIMTool... tools) {
        this.tools = new HashSet<>();
        Arrays.asList(tools).forEach(RIMGuiInternal.this::addWidget);
        this.context = new ImGuiContext(ImGui.createContext().ptr);
        this.imGuiGlfw.init(ptr, true);
        this.imGuiGl3.init("#version 410 core");
        RIMStyle.init();
        ImGui.setCurrentContext(this.context);
    }

    public void beginFrame() {
        this.imGuiGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        if (this.active) {
            render();
        }
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void render() {
        renderBar();
        this.tools.stream().filter(tool -> tool.open.get()).forEach(RIMTool::render);
    }

    public void renderBar() {
        if (!ImGui.beginMainMenuBar()) return;
        ImFont font = ImGui.getFont();
        float width = font.calcTextSizeAX(ImGui.getFontSize(), Float.MAX_VALUE, 0, " Refraction ");
        float height = ImGui.getTextLineHeightWithSpacing() + 8;
        ImGui.getWindowDrawList().addRectFilled(0, 0, width, height, ImGui.getColorU32(ImGuiCol.FrameBgHovered));
        ImGui.text("Refraction ");
        for (RIMTool widget : tools) {
            if (!ImGui.beginMenu(widget.group())) continue;
            if (ImGui.menuItem(widget.name())) widget.toggle();
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();
    }

    public void toggle() {
        this.active = !this.active;
    }

    @Override
    public void assignChannel(UUID uuid) {
        this.channel = NamedAPI.create(RIMServer.CHANNEL_NAME).open(ClientData.getPlayer().level(), uuid);
    }

    @Override
    public boolean onMouseButton(long window, int button, int action, int mods) {
        return ImGui.getIO().getWantCaptureMouse();
    }

    @Override
    public boolean onScroll(double xoffset, double yoffset) {
        return ImGui.getIO().getWantCaptureMouse();
    }

    @Override
    public void onGrabMouse() {
        ImGui.setWindowFocus(null);
    }

    @Override
    public boolean onKey(long window, int key, int scancode, int action, int mods) {
        return ImGui.getIO().getWantCaptureKeyboard();
    }

    @Override
    public boolean onChar(long window, int codepoint, int mod) {
        return ImGui.getIO().getWantTextInput();
    }

    @Override
    public void free() {
        this.imGuiGl3.dispose();
        this.imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    public void addWidget(RIMTool widget) {
        this.tools.add(widget);
        widget.init();
    }

    public static IRIMGui get() {
        return gui == null ? none : gui;
    }

    static {
        RefractionClientEvents.NAMED_CHANNEL_OPEN.register((channel, ptr) -> {
            if (channel.equals(RIMServer.CHANNEL_NAME)) {
                get().assignChannel(ptr);
            }
        });
    }
}
