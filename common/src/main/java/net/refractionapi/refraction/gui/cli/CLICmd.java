package net.refractionapi.refraction.gui.cli;

import net.refractionapi.refraction.feature.channel.NamedAPI;
import net.refractionapi.refraction.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public abstract class CLICmd {
    protected final CLI cli;
    private final HashMap<String, Pair<String, Arg<?>>> argExecs = new HashMap<>();

    public CLICmd(CLI cli) {
        this.cli = cli;
        this.mapArgs();
    }

    public abstract String command();

    public abstract String description();

    public int priority() {
        return 0;
    }

    public abstract void mapArgs();

    public void mapArg(String arg, String desc, Arg<?> argFunc) {
        this.argExecs.put(arg, Pair.of(desc, argFunc));
    }

    protected Arg<?>[] getArgs() {
        return argExecs.values().stream().map(Pair::getSecond).toArray(Arg[]::new);
    }

    protected Arg<?>[] getArgs(String[] args) {
        return Arrays.stream(args).map((s) -> argExecs.getOrDefault(s, Pair.of("", null))).map(Pair::getSecond).toArray(Arg[]::new);
    }

    protected HashMap<String, Pair<String, Arg<?>>> args() {
        return new HashMap<>(argExecs);
    }

    protected void runArgs(String[] args, BiConsumer<String, Arg<?>> consumer) {
        Arg<?>[] run = getArgs(args);
        for (int i = 0; i < run.length; i++) {
            Arg<?> arg = run[i];
            if (arg == null) continue;
            consumer.accept(args[i], arg);
            arg.run(args, i);
        }
    }

    protected void runArgs(String[] args) {
        runArgs(args, (s, a) -> {
        });
    }

    protected String[] subStringArgs(String[] args, String match) {
        String combined = String.join(" ", args);
        int index = combined.indexOf(match);
        if (index == -1) return new String[0];
        return combined.substring(index).split(" ");
    }

    protected String[] subStringTillArgs(String[] args, String match) {
        String combined = String.join(" ", args);
        int index = combined.indexOf(match);
        if (index == -1) return args;
        return combined.substring(0, index).split(" ");
    }

    protected int empty(String[] args, int pos) {
        return 1;
    }

    protected <T> Arg<T> getArg(String arg) {
        return (Arg<T>) argExecs.get(arg).getSecond();
    }

    protected boolean hasTrailing(String[] args, int pos) {
        return args.length - 1 > pos;
    }

    protected CLICmd combine(CLICmd cmd) {
        this.argExecs.putAll(cmd.argExecs);
        return this;
    }

    protected int parseInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            this.print("Invalid number format: %s".formatted(arg));
            return -1;
        }
    }

    public void print(String s) {
        this.cli.print(s);
    }

    protected NamedAPI api() {
        return CLIComms.api.get();
    }

    public abstract void exec(String[] args);

    @FunctionalInterface
    public interface Arg<T> {
        T run(String[] args, int pos);
    }
}
