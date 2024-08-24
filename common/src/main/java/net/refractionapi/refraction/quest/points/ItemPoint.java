package net.refractionapi.refraction.quest.points;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.refractionapi.refraction.quest.Quest;

public class ItemPoint extends QuestPoint {

    private final Item item;
    private final int count;
    private int collected = 0;

    public ItemPoint(Quest quest, Item item, int count) {
        super(quest);
        this.item = item;
        this.count = count;
    }

    @Override
    public void tick() {
        this.collected = this.quest.getPlayer().getInventory().countItem(this.item);
        if (this.collected >= this.count) {
            this.completed = true;
        }
    }

    @Override
    public String id() {
        return "%s+%d".formatted(this.item.getDescriptionId(), this.count);
    }

    @Override
    public Component description() {
        return Component.literal("Collect: ").append(Component.translatable(this.item.getDescriptionId()).append(" %s/%s".formatted(this.collected, this.count)));
    }

}
