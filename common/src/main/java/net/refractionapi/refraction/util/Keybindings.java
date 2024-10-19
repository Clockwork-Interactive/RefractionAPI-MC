package net.refractionapi.refraction.util;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.refractionapi.refraction.platform.RefractionServices;
import org.jetbrains.annotations.ApiStatus;

public interface Keybindings {

    String KEY_CATEGORY_REFRACTION = "key.category.refraction.binds";

    Mapping DEBUG_RENDERERS = register("debug_renderers", InputConstants.KEY_F7, KEY_CATEGORY_REFRACTION);


    static Mapping register(String id, int key, String category) {
        Mapping mapping = new Mapping(id, new KeyMapping(id, key, category));
        RefractionServices.KEYBINDINGS.register(mapping);
        return mapping;
    }

    @ApiStatus.Internal
    void register(Mapping mapping);

    record Mapping(String id, KeyMapping mapping) {
    }

    static void init() {

    }

}
