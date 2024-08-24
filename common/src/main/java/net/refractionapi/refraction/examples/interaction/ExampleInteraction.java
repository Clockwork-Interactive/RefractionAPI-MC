package net.refractionapi.refraction.examples.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.interaction.NPCInteraction;

public class ExampleInteraction extends NPCInteraction {

    public ExampleInteraction(Player player) {
        super(ExampleInteractionRegistry.EXAMPLE_INTERACTION, player);
    }

    @Override
    public void init() {
        this.newStage("start")
                .addDialogue(Component.literal("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"), 25)
                .addOption(Component.literal("hello"), "hello", (i) -> Refraction.LOGGER.info(String.valueOf(i.getPlayer().level())))
                .addOption(Component.literal("bye"), "bye")
                .newStage("hello")
                .addDialogue(Component.literal("hello"), 10)
                .addOption(Component.literal("bye"), "bye")
                .newStage("bye")
                .addDialogue(Component.literal("bye"), 5)
                .onSwitch(NPCInteraction::sendToServer)
                .end();
    }

    @Override
    public void handle(CompoundTag tag) {

    }

}
