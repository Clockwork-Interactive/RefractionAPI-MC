package net.refractionapi.refraction.config;

import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.feature.reconfig.RCBoolean;
import net.refractionapi.refraction.feature.reconfig.RCBuilder;
import net.refractionapi.refraction.feature.reconfig.RCList;
import net.refractionapi.refraction.feature.reconfig.RCString;

import java.util.List;

public class RServerConfig {
    public static final RCBuilder builder = new RCBuilder();
    public static final RCBoolean enableRDebugWhitelist;
    public static final RCList<RCString> permittedUsers;

    public static boolean isPermitted(Player player) {
        if (player == null) return false;
        return whitelistEnabled() ? permittedUsers.asList().contains(player.getStringUUID()) : player.hasPermissions(2);
    }

    public static boolean whitelistEnabled() {
        return enableRDebugWhitelist.get();
    }

    static {
        enableRDebugWhitelist = builder.set(
                "enableRDebugWhitelist",
                "if Refraction Debug should be locked behind the \"permittedUsers\" whitelist",
                false
        ).build();
        permittedUsers = builder.set(
                "permittedUsers-UUID",
                RCString.class,
                List.of("should be in UUID form | you can safely delete or replace this")
        ).build();
    }
}
