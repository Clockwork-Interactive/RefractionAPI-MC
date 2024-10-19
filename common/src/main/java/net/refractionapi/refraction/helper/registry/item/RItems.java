package net.refractionapi.refraction.helper.registry.item;

import net.minecraft.world.item.Item;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.helper.registry.RRegister;
import net.refractionapi.refraction.helper.registry.item.items.FloodDebugger;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.function.Supplier;

public class RItems {

    public static final RRegister<Item> FLOOD_DEBUGGER = register("flood_debug", () -> new FloodDebugger(new Item.Properties()));


    public static RRegister<Item> register(String id, Supplier<Item> item) {
        if (!Refraction.debugTools) return null;
        return RefractionServices.REGISTRY.registerItem(id, item);
    }

    public static void init() {

    }

}
