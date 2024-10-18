package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.screen.ScreenBuilder;
import net.refractionapi.refraction.feature.screen.ServerScreen;

public class ExampleScreenRegistry {

    public static final ScreenBuilder<ExampleServerScreen> EXAMPLE_SCREEN = new ScreenBuilder.Builder()
            .clientScreenCreator((args) -> ClientData.createScreen((String) args[0]))
            .serverScreenCreator(ExampleServerScreen::new)
            .serializer((args) -> {
                CompoundTag tag = new CompoundTag();
                tag.putString("data", (String) args[0]);
                return tag;
            })
            .deserializer((tag) -> new Object[]{tag.getString("data")})
            .serverHandler(ServerScreen::handle)
            .build("example_screen");



    public static void init() {
    }

}
