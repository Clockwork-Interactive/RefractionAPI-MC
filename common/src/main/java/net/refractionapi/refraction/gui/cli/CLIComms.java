package net.refractionapi.refraction.gui.cli;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RServerConfig;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.ThreadedAPI;

public class CLIComms {
    public static final ResourceLocation ID = Refraction.id("cli_comms");
    public static final ThreadedAPI api = NamedAPI.create(ID)
            .preConfigure((nap) -> RefractionEvents.REGISTER_CLI.invoker().configure(nap))
            .configureServer((c) -> {
                c.valid((plr, buf, id) -> RServerConfig.isPermitted(plr));
            })
            .initCommon();

    public static void init() {

    }
}