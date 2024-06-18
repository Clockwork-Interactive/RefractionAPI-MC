package net.refractionapi.refraction.examples.interaction;

import net.refractionapi.refraction.interaction.InteractionBuilder;

public class ExampleInteractionRegistry {

    public static final InteractionBuilder<ExampleInteraction> EXAMPLE_INTERACTION = new InteractionBuilder<>("example", ExampleInteraction.class)
            .consumer(ExampleInteraction::handle);


    public static void init() {
    }

}
