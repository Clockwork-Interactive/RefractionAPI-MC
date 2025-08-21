package net.refractionapi.refraction.gui.cli;

import joptsimple.internal.Strings;
import net.refractionapi.refraction.events.RefractionClientEvents;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class CLI {
    private final HashMap<String, CLICmd> cmds = new HashMap<>();
    private final CopyOnWriteArrayList<String> history = new CopyOnWriteArrayList<>();

    public CLI() {
        this.compileCmds();
    }

    public void runCommand(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            print("Invalid syntax");
            return;
        }
        String command = args[0];
        history.add("$>%s".formatted(Strings.join(args, " ")));
        if (!cmds.containsKey(command)) {
            print("Command '%s' does not exist!\nRun 'help' or 'help -d' for info!".formatted(command));
            return;
        }
        String[] cmdArgs = Arrays.stream(args)
                .skip(1)
                .filter(arg -> !arg.isEmpty())
                .map(String::strip)
                .toArray(String[]::new);
        cmds.get(command).exec(cmdArgs);
    }

    public void print(String s) {
        this.history.add(s);
    }

    public String[] getCmds() {
        return cmds.keySet().toArray(String[]::new);
    }

    public CLICmd getCmd(String cmd) {
        return cmds.get(cmd);
    }

    public String[] getDescCmds() {
        return cmds.keySet().stream().map((s) -> "%s - %s".formatted(s, cmds.get(s).description())).toArray(String[]::new);
    }
    
    public List<String> history() {
        return history;
    }

    private void compileCmds() {
        add(new HelpCmd(this));
        add(new ClearCmd(this));
        add(new HijackCmd(this));
        add(new LSCmd(this));
        add(new CLIServerCommand(this));
        add(new TasksCmd(this));
        RefractionClientEvents.CLI_REGISTER.invoker().register(this);
    }

    public void add(CLICmd cliCmd) {
        this.cmds.compute(cliCmd.command(), (k, v) -> {
            if (v == null) return cliCmd;
            if (v.priority() >= cliCmd.priority()) v.combine(cliCmd);
            else v = cliCmd.combine(v);
            return v;
        });
        // sort by cmd name
        this.cmds.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEachOrdered(e -> this.cmds.put(e.getKey(), e.getValue()));
    }
}
