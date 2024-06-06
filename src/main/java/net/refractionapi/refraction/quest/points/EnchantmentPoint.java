package net.refractionapi.refraction.quest.points;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.refractionapi.refraction.quest.Quest;

import java.util.Map;

public class EnchantmentPoint extends QuestPoint {

    private final Enchantment enchantment;
    private final int minLevel;
    private final int maxLevel;

    public EnchantmentPoint(Quest quest, Enchantment enchantment, int minLevel, int maxLevel) {
        super(quest);
        this.enchantment = enchantment;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public void tick() {
        if (this.quest.getPlayer().getInventory().items.stream().anyMatch((itemStack -> {
            Map<Enchantment, Integer> enchantments = itemStack.getAllEnchantments();
            return enchantments.containsKey(this.enchantment) && enchantments.get(this.enchantment) >= this.minLevel && enchantments.get(this.enchantment) <= this.maxLevel;
        }))) {
            this.completed = true;
        }
    }

    @Override
    public String id() {
        return "%s+%d-%d".formatted(this.enchantment.getDescriptionId(), this.minLevel, this.maxLevel);
    }

    @Override
    public Component description() {
        return Component.literal("Enchant an item with ").append(Component.translatable(this.enchantment.getDescriptionId())).append(Component.literal(" %d".formatted(this.minLevel) + (this.maxLevel == this.minLevel ? "" : "-%d".formatted(this.maxLevel))));
    }

}
