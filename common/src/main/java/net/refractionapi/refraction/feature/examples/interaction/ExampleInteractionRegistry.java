package net.refractionapi.refraction.feature.examples.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.feature.interaction.InteractionBuilder;

public class ExampleInteractionRegistry {

    public static final InteractionBuilder<ExampleInteraction> EXAMPLE_INTERACTION = new InteractionBuilder<>("example", ExampleInteraction.class)
            .clientHandler((args) -> ClientData.handleInteraction(() -> new ExampleInteraction(ClientData.getPlayer()), (CompoundTag) args[1]))
            .clientSerializer((args) -> new CompoundTag())
            .clientDeserializer((tag) -> new Object[]{ClientData.getPlayer(), tag})
            .constructor((args) -> new ExampleInteraction((Player) args[0]));


    public static void init() {
    }

}
