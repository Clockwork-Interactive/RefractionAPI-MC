package net.refractionapi.refraction.client.screen.widget;

import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class RCheckbox extends Checkbox {

    private final Consumer<Checkbox> onPress;

    public RCheckbox(int x, int y, int width, int height, Component component, boolean selected, Consumer<Checkbox> onPress) {
        super(x, y, width, height, component, selected);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.onPress.accept(this);
    }

}
