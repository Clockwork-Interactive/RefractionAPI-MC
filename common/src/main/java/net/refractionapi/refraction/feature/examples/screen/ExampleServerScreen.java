package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.feature.screen.ServerScreen;

import static net.refractionapi.refraction.Refraction.LOGGER;

public class ExampleServerScreen extends ServerScreen {

    public ExampleServerScreen(ServerPlayer player) {
        super(ExampleScreenRegistry.EXAMPLE_SCREEN, player);
    }

    @Override
    public void onClose() {
        super.onClose();
        LOGGER.info("Screen closed!");
    }

    @Override
    public void handle(CompoundTag tag) {
        LOGGER.info("Received data from client: {}", tag.getString("data"));
        CompoundTag response = new CompoundTag();
        response.putString("data", "Hello from the server!");
        sendData(response);
    }

}
