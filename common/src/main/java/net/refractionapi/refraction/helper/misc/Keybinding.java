package net.refractionapi.refraction.helper.misc;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.twc.TWC;

import java.util.ArrayList;
import java.util.List;

public class Keybinding {
    private final List<Character> registered = new ArrayList<>();
    private static final TWC.Sided API = new TWC.Sided(Refraction.id("keybinding"))
            .configureClient((twc) -> {
                twc.listener("registerKeybind", (msg) -> {
                });
            })
            .configureServer((twc) -> {
                twc.listener("sendKeybind", (msg) -> {
                });
            });

    public static void registerKeybind(Player player, String keybindID, Runnable onPress) {
        TWC.Message msg = TWC.message()
                .nbt((nbt) -> nbt.putString("keybindID", keybindID));
        API.sendMessage("registerKeybind", msg);
    }

    public static void receiveKeybind(Player player, String keybindID) {

    }
}
