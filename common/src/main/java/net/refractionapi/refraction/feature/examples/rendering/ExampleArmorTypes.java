package net.refractionapi.refraction.feature.examples.rendering;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.refractionapi.refraction.Refraction;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ExampleArmorTypes {
    public static final Holder<ArmorMaterial> NVG = register("nvg",
            Util.make(new EnumMap<>(ArmorItem.Type.class), attribute -> {
                attribute.put(ArmorItem.Type.BOOTS, 99);
                attribute.put(ArmorItem.Type.LEGGINGS, 99);
                attribute.put(ArmorItem.Type.CHESTPLATE, 99);
                attribute.put(ArmorItem.Type.HELMET, 99);
                attribute.put(ArmorItem.Type.BODY, 99);
            }), 0, 1F, 1F, () -> Items.NETHERITE_BLOCK);

    private static Holder<ArmorMaterial> register(
            String name,
            EnumMap<ArmorItem.Type, Integer> typeProtection,
            int enchantability,
            float toughness,
            float knockbackResistance,
            Supplier<Item> ingredientItem
    ) {
        ResourceLocation location = Refraction.id(name);
        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_NETHERITE;
        Supplier<Ingredient> ingredient = () -> Ingredient.of(ingredientItem.get());

        EnumMap<ArmorItem.Type, Integer> typeMap = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values()) {
            typeMap.put(type, typeProtection.get(type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, location,
                new ArmorMaterial(typeProtection, enchantability, equipSound, ingredient, List.of(), toughness, knockbackResistance));
    }

}
