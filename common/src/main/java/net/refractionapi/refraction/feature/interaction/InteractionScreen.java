package net.refractionapi.refraction.feature.interaction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class InteractionScreen extends Screen {
    protected final NPCInteraction npcInteraction;
    protected String currentStage;
    protected final HashMap<String, List<Button>> buttons = new HashMap<>();
    protected int ticks;

    protected InteractionScreen(Component title, NPCInteraction interaction) {
        super(title);
        this.npcInteraction = interaction;
        this.npcInteraction.init();
        this.currentStage = this.npcInteraction.firstStage().getId();
    }

    @Override
    protected void init() {
        this.npcInteraction.getStages().forEach((id, stage) -> {
            List<Button> stageButtons = new ArrayList<>();
            stage.getOptions().forEach((component, config) -> {
                Button.OnPress onPress = (button) -> {
                    // server side switch --Zeus
                    stage.onSwitch(config.goTo(), component);
                    // client side switch --Zeus
                    this.switchStage(config.goTo());
                    // client onClick handler --Zeus
                    config.onClick().ifPresent(consumer -> consumer.accept(this.npcInteraction));
                    this.ticks = 0;
                };
                stageButtons.add(createButton(component, onPress));
            });
            this.buttons.put(id, stageButtons);
        });
        this.switchStage(this.currentStage);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        InteractionStage stage = this.npcInteraction.getStage(this.currentStage);
        float lerpTicks = (float) Math.min(this.ticks, stage.getDialogueTicks()) / (float) stage.getDialogueTicks();
        if (lerpTicks >= 1.0F && !stage.getGoTo().isEmpty()) {
            stage.onSwitch(stage.getGoTo(), Component.empty());
            this.switchStage(stage.getGoTo());
            this.ticks = 0;
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        this.ticks++;
        InteractionStage stage = this.npcInteraction.getStage(this.currentStage);
        if (stage == null) return;
        if (stage.ends() && (stage.getDialogueTicks() + 20 <= this.ticks || stage.shouldInstantlyClose())) {
            stage.onSwitch("", Component.empty());
            this.onClose();
        }
    }

    public abstract Button createButton(Component component, Button.OnPress onPress);

    public void switchStage(String id) {
        this.buttons.get(this.currentStage).forEach(this::removeWidget);
        this.currentStage = id;
        this.buttons.get(this.currentStage).forEach(this::addRenderableWidget);
    }
}
