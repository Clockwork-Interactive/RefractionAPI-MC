package net.refractionapi.refraction.gui.cli;

public class CLIServerCommand extends CLICmd {
    public CLIServerCommand(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "exec";
    }

    @Override
    public String description() {
        return "Execute server command.";
    }

    @Override
    public void mapArgs() {

    }

    @Override
    public void exec(String[] args) {
        if (args.length == 0) {
            print("exec [CMD]\nexec give @s stick");
            return;
        }
        String conjoined = String.join(" ", args);
        if (conjoined.isEmpty()) {
            print("Command can't be empty!");
            return;
        }
        conjoined = conjoined.charAt(0) == '/' ? conjoined : "/%s".formatted(conjoined);
        String command = conjoined;
        this.api().channel().send("command", (buf) -> buf.writeUtf(command));
        print(command);
    }
}
