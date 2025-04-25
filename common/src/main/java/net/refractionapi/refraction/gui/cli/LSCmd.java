package net.refractionapi.refraction.gui.cli;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.util.Pair;

import java.util.Optional;
import java.util.UUID;

public class LSCmd extends CLICmd {
    public LSCmd(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "ls";
    }

    @Override
    public String description() {
        return "Displays specified list.";
    }

    @Override
    public void mapArgs() {
        mapArg("-twc", "Display two way channels.", this::listTwoWayChannels);
        mapArg("-mods", "Displays refraction-registered mods.", this::listMods);
    }

    public int listMods(String[] args, int pos) {
        print("Registered Mods:");
        for (String mod : RModRegistrar.mods()) {
            print("  " + mod);
        }
        return 1;
    }

    public int listTwoWayChannels(String[] args, int pos) {
        int i = 0;
        for (Pair<UUID, Optional<TwoWayChannel>> channel : TwoWayIntermediary.instance(false).channels()) {
            ResourceLocation id = NamedAPI.getChannel(channel.getFirst()).orElse(null);
            print("id: [%s] uuid: [%s]%s".formatted(i, channel.getFirst().toString(), id == null ? "" : " api: [%s]".formatted(id.toString())));
            channel.getSecond().ifPresent((c) -> {
                print(" Known Routes:");
                for (String route : c.routes()) {
                    print("   " + route);
                }
            });
            i++;
        }
        return 0;
    }

    @Override
    public void exec(String[] args) {
        if (args.length == 0) {
            print("ls [ARGS]");
            return;
        }
        runArgs(args);
    }
}
