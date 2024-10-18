package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.screen.RefractionScreen;

public class ExampleScreen extends Screen implements RefractionScreen {

    private final String transferredData;

    public ExampleScreen(String transferredData) {
        super(Component.literal(""));
        this.transferredData = transferredData;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(Button.builder(Component.literal(transferredData), (onPress) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("data", "Hello from the client!");
            this.sendData(tag);
        }).pos(this.width / 2 - 75, this.height / 2).build());
    }

    @Override
    public void handleServerEvent(CompoundTag tag) {
        Refraction.LOGGER.info("Received data from server: {}", tag.getString("data"));
    }

}
