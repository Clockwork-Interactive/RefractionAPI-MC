package net.refractionapi.refraction.client.screen;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.screen.tab.Tab;
import net.refractionapi.refraction.feature.screen.tab.TabWidget;
import net.refractionapi.refraction.feature.screen.tab.TabbedScreen;
import net.refractionapi.refraction.debug.RDebugRenderer;

import java.awt.*;
import java.util.Locale;

public class RDashboard extends TabbedScreen {

    public RDashboard() {
        super(Component.literal("Dashboard"));
    }

    @Override
    protected void initSettings() {
        setSize(400, 200);
        setBorderSize(5);
    }

    @Override
    protected Color getBackgroundColor() {
        return Refraction.refractionPrimary;
    }

    @Override
    protected Color getBorderColor() {
        return Refraction.refractionSecondary;
    }

    @Override
    protected void initTabs() {
        Tab debuggers = createTab(Component.literal("Debuggers"));
        for (String renderer : RDebugRenderer.getRenderers()) {
            debuggers.addCheckbox(Component.literal(renderer.toUpperCase(Locale.ROOT)), 20, RDebugRenderer.isEnabled(renderer), (checkbox1 -> RDebugRenderer.toggle(renderer))).setPadding(5, 0, 0, 5);
        }
        Tab mazeGenerator = createTab(Component.literal("Maze Generator"));
        TabWidget<EditBox> size = mazeGenerator.addNumberInput(Component.literal("Size"), 50, 20);
        TabWidget<EditBox> wallHeight = mazeGenerator.addNumberInput(Component.literal("Wall Height"), 50, 20);
        TabWidget<EditBox> wallThickness = mazeGenerator.addNumberInput(Component.literal("Wall Thickness"), 50, 20);
        TabWidget<EditBox> centerSize = mazeGenerator.addNumberInput(Component.literal("Center Size"), 50, 20);
        TabWidget<EditBox> start = mazeGenerator.addBlockPosInput(Component.literal("Start"), 50, 20);
        TabWidget<EditBox> startDirection = mazeGenerator.addTextInput(Component.literal("Start Direction"), 50, 20, (box, string) -> {
        });
        TabWidget<EditBox> wallBlock = mazeGenerator.addBlockInput(Component.literal("Wall Block"), 50, 20);
        TabWidget<EditBox> wallOuterBlock = mazeGenerator.addBlockInput(Component.literal("Wall Outer Block"), 50, 20);
        TabWidget<EditBox> floorBlock = mazeGenerator.addBlockInput(Component.literal("Floor Block"), 50, 20);
        TabWidget<EditBox> ceilingBlock = mazeGenerator.addBlockInput(Component.literal("Ceiling Block"), 50, 20);
        mazeGenerator.addButton(Component.literal("Generate"), 50, 20, (button) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("type", "maze");
            if (
                    size.widget().getValue().isEmpty() ||
                            wallHeight.widget().getValue().isEmpty() ||
                            wallThickness.widget().getValue().isEmpty() ||
                            centerSize.widget().getValue().isEmpty() ||
                            start.widget().getValue().isEmpty() ||
                            startDirection.widget().getValue().isEmpty()) {
                return;
            }
            tag.putInt("size", Integer.parseInt(size.widget().getValue()));
            tag.putInt("wallHeight", Integer.parseInt(wallHeight.widget().getValue()));
            tag.putInt("wallThickness", Integer.parseInt(wallThickness.widget().getValue()));
            tag.putInt("centerSize", Integer.parseInt(centerSize.widget().getValue()));
            String[] startData = start.widget().getValue().split(";");
            tag.putIntArray("start", new int[]{Integer.parseInt(startData[0]), Integer.parseInt(startData[1]), Integer.parseInt(startData[2])});
            tag.putString("startDirection", startDirection.widget().getValue());
            tag.putString("wallBlock", wallBlock.widget().getValue());
            tag.putString("wallOuterBlock", wallOuterBlock.widget().getValue());
            tag.putString("floorBlock", floorBlock.widget().getValue());
            tag.putString("ceilingBlock", ceilingBlock.widget().getValue());
            this.sendData(tag);
        });
        for (int i = 0; i < mazeGenerator.getWidgets().length; i++) {
            mazeGenerator.getWidgets()[i].setPadding(3, 0, 0, i * 4 + 3);
        }
    }

}
