package net.refractionapi.refraction.feature.examples.quest;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.refractionapi.refraction.feature.quest.Quest;
import net.refractionapi.refraction.quest.points.*;
import net.refractionapi.refraction.feature.quest.points.*;

public class ExampleQuest extends Quest {

    public ExampleQuest(ServerPlayer player, CompoundTag tag) {
        super(player, tag);
    }

    @Override
    public void generate() {
        this
                .newPart(Component.literal("Example description").withStyle(ChatFormatting.GOLD))
                .addQuestPoint(new LocationPoint(this, this.getPlayer().position().add(10, 0, 0), 5.0D))
                .addQuestPoint(new ItemPoint(this, Items.DIAMOND, 2))
                .addQuestPoint(new ItemPoint(this, Items.IRON_INGOT, 1))
                .onCompletion((questPart -> questPart.getPlayer().displayClientMessage(Component.literal("Part completed!").withStyle(ChatFormatting.GREEN), false)))
                .newPart(Component.literal("Example description 2").withStyle(ChatFormatting.RED))
                .addQuestPoint(new LocationPoint(this, this.getPlayer().position().add(20, 0, 0), 5.0D))
                .newPart(Component.literal("Example description 3"))
                .addQuestPoint(new KillPoint(this, EntityType.CHICKEN, 2))
                .addQuestPoint(new InteractionPoint(this, EntityType.COW))
                .addQuestPoint(new EnchantmentPoint(this, Enchantments.SHARPNESS, 2, 2))
                .build()
                .onQuestEnd((quest -> {
                    quest.getPlayer().displayClientMessage(Component.literal("Quest completed!").withStyle(ChatFormatting.GREEN), false);
                }));
    }

    @Override
    public Component questName() {
        return Component.literal("Example Quest");
    }

}
