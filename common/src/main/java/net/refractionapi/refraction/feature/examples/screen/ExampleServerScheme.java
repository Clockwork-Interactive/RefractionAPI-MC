package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.refractionapi.refraction.feature.scheme.ScreenScheme;
import net.refractionapi.refraction.feature.scheme.ServerScheme;

import static net.refractionapi.refraction.Refraction.LOGGER;

public class ExampleServerScheme extends ServerScheme {
    public ExampleServerScheme(ServerPlayer player, Object... args) {
        super(ExampleScreenRegistry.EXAMPLE_SCHEME, player);
    }

    @Override
    public void handleMessage(ScreenScheme.ScreenMessage message) {
        LOGGER.info("Received data from client: {}", message.tag().getString("data"));
        CompoundTag response = new CompoundTag();
        response.putString("data", "Hello from the server!");
        sendNbt(response);
    }
}
