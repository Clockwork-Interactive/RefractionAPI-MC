package net.refractionapi.refraction.gui.cli;

public class ClearCmd extends CLICmd {
    public ClearCmd(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "clear";
    }

    @Override
    public String description() {
        return "Clears console history.";
    }

    @Override
    public void mapArgs() {

    }

    @Override
    public void exec(String[] args) {
        this.cli.history().clear();
    }
}
