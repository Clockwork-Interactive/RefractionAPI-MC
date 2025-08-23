package net.refractionapi.refraction.gui.cli;

public class TasksCmd extends CLICmd {
    public TasksCmd(CLI cli) {
        super(cli);
    }

    @Override
    public String command() {
        return "tasks";
    }

    @Override
    public String description() {
        return "Show all tasks on current level and player";
    }

    @Override
    public void mapArgs() {
        mapArg("run", "Try running a task via ResourceLocation (will not work if has custom args)", this::tryRun);
    }

    public boolean tryRun(String[] args, int pos) {
        if (!hasTrailing(args, pos)) {
            print("Invalid usage of: run [STRING]");
            return false;
        }
        String taskID = args[pos + 1];
        this.api().channel().send("tasks", (buf) -> {
            buf.writeUtf("run");
            buf.writeUtf(taskID);
        });
        return true;
    }

    @Override
    public void exec(String[] args) {
        runArgs(args);
        if (args.length != 0) return;
        this.api().channel().post(
                "tasks",
                (router, tag) -> {
                },
                (buf) -> buf.writeUtf("request"),
                (plr, router, header, buf) -> {
                    for (int i = 0; i < buf.readInt(); i++) print(buf.readUtf());
                }
        );
    }
}
