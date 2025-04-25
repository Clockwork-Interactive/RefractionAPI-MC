package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.scheme.RScreen;
import net.refractionapi.refraction.feature.scheme.RegisterScreen;
import net.refractionapi.refraction.feature.scheme.ScreenScheme;

@RegisterScreen(ExampleServerScheme.class)
public class ExampleScreenScheme extends Screen implements RScreen {
    private final String data;

    public ExampleScreenScheme(String data) {
        super(Component.literal("Example"));
        this.data = data;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.literal(data), (onPress) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("data", "Hello from the client!");
            this.sendNbt(tag);
        }).pos(this.width / 2 - 75, this.height / 2).build());
    }

    @Override
    public void handle(ScreenScheme.ScreenMessage screenMessage) {
        Refraction.LOGGER.info("Received data from server: {}", screenMessage.tag().getString("data"));
    }
}
