package net.refractionapi.refraction.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;

import java.util.function.Consumer;

public class RCheckbox extends Checkbox {

    private static final ResourceLocation TEXTURE = Refraction.id("gui/checkbox.png");
    private final Consumer<RCheckbox> onPress;

    public RCheckbox(int x, int y, int width, int height, Component component, boolean selected, Consumer<RCheckbox> onPress) {
        super(x, y, width, height, component, selected);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.onPress.accept(this);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int $$1, int $$2, float $$3) {
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        float modifier = this.width / 10.0F;
        int size = (int) (64 * modifier);
        int buttonSize = (int) (10 * modifier);
        guiGraphics.blit(TEXTURE, this.getX(), this.getY(), buttonSize, this.selected() ? buttonSize : 0, buttonSize, buttonSize, size, size);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
