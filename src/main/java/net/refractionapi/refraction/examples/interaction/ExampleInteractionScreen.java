package net.refractionapi.refraction.examples.interaction;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.refractionapi.refraction.interaction.InteractionStage;
import net.refractionapi.refraction.interaction.NPCInteraction;
import net.refractionapi.refraction.networking.C2S.SyncInteractionC2SPacket;
import net.refractionapi.refraction.networking.RefractionMessages;

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
    private List<FormattedCharSequence> cachedComponents = Collections.emptyList();

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
                    stage.onSwitch();
                    this.switchStage(buttonOptions.goTo());
                    CompoundTag tag = new CompoundTag();
                    tag.putString("stage", buttonOptions.goTo());
                    tag.putString("button", Component.Serializer.toJson(component));
                    RefractionMessages.sendToServer(new SyncInteractionC2SPacket(this.npcInteraction.getBuilder().getId(), tag));
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
        float lerp = (float) Math.min(this.ticks, stage.getDialougeTicks()) / (float) stage.getDialougeTicks();
        Component component = stage.getDialouge();
        FormattedText subComponent = FormattedText.of(component.copy().getString().substring(0, (int) (component.getString().length() * lerp)));
        this.cachedComponents = this.font.split(subComponent, 140);
        this.font.width(subComponent);
        for (int l = 0; l < this.cachedComponents.size(); ++l) {
            FormattedCharSequence formattedcharsequence = this.cachedComponents.get(l);
            pGuiGraphics.drawString(this.font, formattedcharsequence, i + 36, this.height / 2 + l * 9, Color.WHITE.getRGB(), false);
        }
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public void tick() {
        this.ticks++;
        InteractionStage stage = this.npcInteraction.getStage(this.currentStage);
        if (stage == null) return;
        if (stage.ends() && (stage.getDialougeTicks() + 25 <= this.ticks || stage.shouldInstantlyClose())) {
            stage.onSwitch();
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
