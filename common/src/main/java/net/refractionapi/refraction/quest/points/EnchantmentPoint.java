package net.refractionapi.refraction.quest.points;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.refractionapi.refraction.quest.Quest;
import net.refractionapi.refraction.registry.RegistryHelper;

import java.util.Map;

public class EnchantmentPoint extends QuestPoint {

    private final ResourceKey<Enchantment> enchantmentKey;
    private final Enchantment enchantment;
    private final int minLevel;
    private final int maxLevel;

    public EnchantmentPoint(Quest quest, ResourceKey<Enchantment> enchantment, int minLevel, int maxLevel) {
        super(quest);
        this.enchantmentKey = enchantment;
        this.enchantment = RegistryHelper.getRegistry(this.quest.getLevel().registryAccess(), Registries.ENCHANTMENT, this.enchantmentKey).value();
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    public void tick() {
        if (this.quest.getPlayer().getInventory().items.stream().anyMatch((itemStack -> {
            ItemEnchantments enchantments = itemStack.getEnchantments();
            Holder<Enchantment> enchantment = RegistryHelper.getRegistry(this.quest.getLevel().registryAccess(), Registries.ENCHANTMENT, this.enchantmentKey);
            int level = EnchantmentHelper.getItemEnchantmentLevel(enchantment, itemStack);
            return enchantments.keySet().contains(enchantment) && level >= this.minLevel && level <= this.maxLevel;
        }))) {
            this.completed = true;
        }
    }

    @Override
    public String id() {
        return "%s+%d-%d".formatted(this.enchantment.description(), this.minLevel, this.maxLevel);
    }

    @Override
    public Component description() {
        return Component.literal("Enchant an item with ").append(this.enchantment.description()).append(Component.literal(" %d".formatted(this.minLevel) + (this.maxLevel == this.minLevel ? "" : "-%d".formatted(this.maxLevel))));
    }

}
