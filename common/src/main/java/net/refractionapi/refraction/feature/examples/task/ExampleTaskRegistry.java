package net.refractionapi.refraction.feature.examples.task;

import net.refractionapi.refraction.feature.task.LevelTaskHolder;
import net.refractionapi.refraction.feature.task.PlayerTaskHolder;

public class ExampleTaskRegistry {
    public static LevelTaskHolder<ExampleLevelTask> EXAMPLE_LEVEL = new LevelTaskHolder<>(
            "level_example",
            ExampleLevelTask::new,
            ExampleLevelTask::new,
            (configure) -> configure.setMaxTicks(20 * 60).setDesc("Example level task")
    );

    public static PlayerTaskHolder<ExamplePlayerTask> EXAMPLE_PLAYER = new PlayerTaskHolder<>(
            "player_example",
            ExamplePlayerTask::new,
            ExamplePlayerTask::new,
            (configure) -> configure.setDesc("Example player task")
    );

    public static void init() {

    }
}
