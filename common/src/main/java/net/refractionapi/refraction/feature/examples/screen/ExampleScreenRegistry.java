package net.refractionapi.refraction.feature.examples.screen;

import net.minecraft.nbt.CompoundTag;
import net.refractionapi.refraction.client.screen.RDashboardServer;
import net.refractionapi.refraction.feature.scheme.ScreenScheme;
import net.refractionapi.refraction.feature.screen.ScreenBuilder;

public class ExampleScreenRegistry {
    public static final ScreenBuilder<ExampleServerScreen> EXAMPLE_SCREEN = new ScreenBuilder.Builder()
            .serverScreenCreator(ExampleServerScreen::new)
            .serializer((args) -> {
                CompoundTag tag = new CompoundTag();
                tag.putString("data", (String) args[0]);
                return tag;
            })
            .deserializer((tag) -> new Object[]{tag.getString("data")})
            .build("example_screen");

    public static final ScreenBuilder<RDashboardServer> DASHBOARD = new ScreenBuilder.Builder()
            .serverScreenCreator(RDashboardServer::new)
            .serializer((a) -> new CompoundTag())
            .deserializer((tag) -> new Object[]{})
            .clientAccessible()
            .build("dashboard");

    public static final ScreenScheme<ExampleServerScheme> EXAMPLE_SCHEME = ScreenScheme.builder()
            .serverScreenCreator(ExampleServerScheme.class, ExampleServerScheme::new)
            // bytebuf way of serialization
            .serializer((objs, buf) -> buf.writeUtf((String) objs[0]))
            .deserializer((buf) -> new Object[]{buf.readUtf()})
            // nbt way of serialization
            .nbtSerializer((objs, tag) -> tag.putString("test", "test"))
            .nbtDeserializer((tag) -> new Object[]{tag.getString("test")})
            //.clientAccessible() can be called from the client to be opened
            .build("example");

    public static void init() {
    }
}
