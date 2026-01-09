package net.refractionapi.refraction.feature.examples.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.interaction.NPCInteraction;

public class ExampleInteraction extends NPCInteraction {
    public ExampleInteraction(Player player) {
        super(ExampleInteractionRegistry.EXAMPLE_INTERACTION, player);
    }

    @Override
    public void init() {
        var start = this.newStage("start");
        start.addDialogue(Component.literal("hiii"), 25);
        start.addOption(Component.literal("hello"), "hello", (i) -> Refraction.LOGGER.info(String.valueOf(i.getPlayer().level())));
        start.addOption(Component.literal("bye"), "bye");
        var hello = newStage("hello");
        hello.addDialogue(Component.literal("hello"), 10);
        hello.addOption(Component.literal("bye"), "bye");
        var bye = newStage("bye");
        bye.addDialogue(Component.literal("bye"), 5);
        bye.onSwitch(NPCInteraction::sendToServer);
        bye.end();
    }

    @Override
    public void handle(CompoundTag tag) {

    }

}
