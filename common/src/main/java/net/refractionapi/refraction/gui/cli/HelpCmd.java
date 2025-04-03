package net.refractionapi.refraction.gui.cli;

public class HelpCmd extends CLICmd {
    public HelpCmd(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "help";
    }

    @Override
    public String description() {
        return "Shows all commands and their description.";
    }

    @Override
    public void mapArgs() {
        mapArg("-d", "Displays advanced cmd details.", this::details);
    }

    private int details(String[] args, int pos) {
        for (String cmd : this.cli.getCmds()) {
            exec(new String[]{cmd});
        }
        return 1;
    }

    @Override
    public void exec(String[] args) {
        if (args.length != 0) {
            String cmd = args[0];
            if (cmd.equals("-d")) {
                getArg("-d").run(args, 0);
                return;
            }
            CLICmd cliCmd = this.cli.getCmd(cmd);
            if (cliCmd == null) {
                print("Command '%s' is unknown!".formatted(cmd));
                return;
            }
            print("%s - %s".formatted(cmd, cliCmd.description()));
            cliCmd.args().forEach((s, p) -> {
                print("   %s - %s".formatted(s, p.getFirst()));
            });
            return;
        }
        this.print(this.cli.getCmds().length == 0 ? "No commands available" : "Available Commands:\n%s".formatted(String.join("\n", this.cli.getDescCmds())));
    }
}
