package net.refractionapi.refraction.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class KeybindingsFabric implements Keybindings {

    @Override
    public void register(Mapping mapping) {
        KeyBindingHelper.registerKeyBinding(mapping.mapping());
    }

}
