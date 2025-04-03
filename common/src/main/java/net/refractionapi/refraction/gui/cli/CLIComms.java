package net.refractionapi.refraction.gui.cli;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.ThreadedAPI;

public class CLIComms {
    public static final ResourceLocation ID = Refraction.id("cli_comms");
    public static final ThreadedAPI api = NamedAPI.create(ID)
            .configure((c) -> {
                c.valid((plr, buf, id) -> RServerConfig.isPermitted(plr));
                c.registerListener((plr, buf) -> {
                    Refraction.LOGGER.info(String.valueOf(buf.readInt()));
                    Refraction.LOGGER.info(c.header().header().getString("test"));
                    return 1;
                });
            })
            .initCommon();

    public static void init() {

    }
}
