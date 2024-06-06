package net.refractionapi.refraction.examples.interaction;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.npc.NPCInteraction;

public class ExampleInteraction extends NPCInteraction {

    public ExampleInteraction(LivingEntity entity) {
        super(entity);
    }

    @Override
    public void init() {
        this.newStage("start")
                .addDialouge(Component.literal("hii"), 25)
                .addOption(Component.literal("hello"), "hello")
                .addOption(Component.literal("bye"), "bye")
                .newStage("hello")
                .addDialouge(Component.literal("hello"), 25)
                .addOption(Component.literal("bye"), "bye")
                .newStage("bye")
                .addDialouge(Component.literal("bye"), 25);
    }

}
