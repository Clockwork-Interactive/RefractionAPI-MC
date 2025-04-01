package net.refractionapi.refraction.helper.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;

public class RegistryHelper {
    public static <T> Holder<T> getRegistry(HolderLookup.Provider provider, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return provider.lookupOrThrow(registry).getOrThrow(key);
    }

    public static <T> Holder<T> getRegistry(Level level, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return getRegistry(level.registryAccess(), registry, key);
    }

    public static <T> Holder<T> getRegistry(Entity entity, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return getRegistry(entity.level(), registry, key);
    }

    public static List<Holder<Enchantment>> getEnchantmentsWithEffect(ItemStack stack, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>> lookFor, MapCodec<?> codec) {
        return stack.getEnchantments().entrySet()
                .stream().filter((e) ->
                        e.getKey().value()
                                .getEffects(lookFor).stream()
                                .anyMatch((effect) -> effect.effect().codec().equals(codec))
                ).map(Map.Entry::getKey).toList();
    }

    public static boolean enchantmentEffectExists(ItemStack stack, DataComponentType<List<TargetedConditionalEffect<EnchantmentEntityEffect>>> lookFor, MapCodec<?> codec) {
        return !getEnchantmentsWithEffect(stack, lookFor, codec).isEmpty();
    }
}