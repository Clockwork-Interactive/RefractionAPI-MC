package net.refractionapi.refraction.examples.interaction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.interaction.NPCInteraction;

public class ExampleInteraction extends NPCInteraction {

    public ExampleInteraction(Player player) {
        super(ExampleInteractionRegistry.EXAMPLE_INTERACTION, player);
    }

    @Override
    public void init() {
        this.newStage("start")
                .addDialouge(Component.literal("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"), 25)
                .addOption(Component.literal("hello"), "hello")
                .addOption(Component.literal("bye"), "bye")
                .newStage("hello")
                .addDialouge(Component.literal("hello"), 10)
                .addOption(Component.literal("bye"), "bye")
                .newStage("bye")
                .addDialouge(Component.literal("bye"), 5)
                .onSwitch(NPCInteraction::sendToServer)
                .end();
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("test", "Hiii!");
    }

    public static void handle(CompoundTag tag, NetworkEvent.Context context) {
        context.enqueueWork(() -> Refraction.LOGGER.info("{} {}", tag.getString("test"), context.getSender().getName().getString()));
        context.setPacketHandled(true);
    }

}
