package net.refractionapi.refraction.gui.cli;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.feature.channel.TwoWayChannel;
import net.refractionapi.refraction.feature.channel.TwoWayIntermediary;
import net.refractionapi.refraction.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HijackCmd extends CLICmd {
    private int id = -1;
    private String router = "";
    private FriendlyByteBuf buf;
    private CompoundTag header;

    public HijackCmd(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "hj";
    }

    @Override
    public String description() {
        return "Hijacks any channel connection, and sends specified packets - useful for pen-testing.";
    }

    @Override
    public void mapArgs() {
        mapArg("-i", "Writes int to buf.", this::writeInt);
        mapArg("-s", "Writes string to buf.", this::writeString);
        mapArg("-h", "Specifies header write. [ID] -[arg] [ARG]", this::empty);
    }

    public boolean writeInt(String[] args, int pos) {
        if (!hasTrailing(args, pos)) {
            print("Invalid usage of -i [INT]");
            return false;
        }
        int parsedInt = this.parseInt(args[pos + 1]);
        if (header != null) {
            header.putInt(args[pos - 1], parsedInt);
            return true;
        }
        this.buf.writeInt(parsedInt);
        return true;
    }

    public boolean writeString(String[] args, int pos) {
        if (!hasTrailing(args, pos)) {
            print("Invalid usage of -s [STRING]");
            return false;
        }
        if (header != null) {
            header.putString(args[pos - 1], args[pos + 1]);
            return true;
        }
        this.buf.writeUtf(args[pos + 1]);
        return true;
    }

    @Override
    public void exec(String[] args) {
        if (args.length < 2) {
            print("Invalid command usage!\nhj [ID] [ROUTER] [ARGS]");
            return;
        }
        this.id = parseInt(args[0]);
        if (id <= -1) return;
        this.router = args[1];
        List<Pair<UUID, Optional<TwoWayChannel>>> channels = TwoWayIntermediary.instance(false).channels();
        if (channels.size() - 1 < this.id) {
            print("Invalid id %s, out of range! [0 -> %s]".formatted(this.id, channels.size() - 1));
            return;
        }
        this.header = null;
        this.buf = null;
        channels.get(this.id).getSecond().ifPresent((c) -> {
            c.send(this.router,
                    (buf) -> {
                        this.buf = buf;
                        this.runArgs(subStringTillArgs(args, "-h"));
                        ResourceLocation namedAPI = NamedAPI.getChannel(c.id()).orElse(null);
                        print("Sent packet to %s [router: %s]%s".formatted(c.id(), router, namedAPI == null ? "" : " [api: %s]".formatted(namedAPI.toString())));
                    }, (id, nbt) -> {
                        this.header = nbt;
                        this.runArgs(subStringArgs(args, "-h"));
                    });
        });
    }
}
