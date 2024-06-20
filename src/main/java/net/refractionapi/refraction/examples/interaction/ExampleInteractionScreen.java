package net.refractionapi.refraction.examples.interaction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.refractionapi.refraction.interaction.InteractionStage;
import net.refractionapi.refraction.interaction.NPCInteraction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ExampleInteractionScreen extends Screen {

    private final NPCInteraction npcInteraction;
    private final HashMap<String, List<Button>> buttons = new HashMap<>();
    private String currentStage;
    private int ticks;
    protected static final int BORDER = 114;

    public ExampleInteractionScreen(NPCInteraction npcInteraction) {
        super(Component.literal("Example Interaction Screen"));
        this.npcInteraction = npcInteraction;
        this.npcInteraction.init();
        this.currentStage = this.npcInteraction.firstStage().getId();
    }

    @Override
    protected void init() {
        super.init();
        this.npcInteraction.getStages().forEach((id, stage) -> {
            List<Button> stageButtons = new ArrayList<>();
            stage.getOptions().forEach((component, buttonOptions) -> {
                Button button = Button.builder(component, (onPress) -> {
                    stage.onSwitch(buttonOptions.goTo(), component);
                    this.switchStage(buttonOptions.goTo());
                    buttonOptions.onClick().ifPresent(consumer -> consumer.accept(this.npcInteraction));
                    this.ticks = 0;
                }).pos(this.width / 2 + BORDER + 10, this.height / 2 + stageButtons.size() * 20).build();
                stageButtons.add(button);
            });
            this.buttons.put(id, stageButtons);
        });
        this.switchStage(this.currentStage);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        int i = (this.width - 192) / 2;
        InteractionStage stage = this.npcInteraction.getStage(this.currentStage);
        float lerp = (float) Math.min(this.ticks, stage.getDialogueTicks()) / (float) stage.getDialogueTicks();
        Component component = stage.getDialogue();
        FormattedText subComponent = FormattedText.of(component.copy().getString().substring(0, (int) (component.getString().length() * lerp)));
        List<FormattedCharSequence> components = this.font.split(subComponent, 140);
        this.font.width(subComponent);
        for (int l = 0; l < components.size(); ++l) {
            FormattedCharSequence formattedcharsequence = components.get(l);
            pGuiGraphics.drawString(this.font, formattedcharsequence, i + 36, this.height / 2 + l * 9, Color.WHITE.getRGB(), false);
        }
        if (lerp >= 1.0F && !stage.getGoTo().isEmpty()) {
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
        if (stage.ends() && (stage.getDialogueTicks() + 25 <= this.ticks || stage.shouldInstantlyClose())) {
            stage.onSwitch("", Component.empty());
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void switchStage(String id) {
        this.buttons.get(this.currentStage).forEach(this::removeWidget);
        this.currentStage = id;
        this.buttons.get(this.currentStage).forEach(this::addRenderableWidget);
    }

}
