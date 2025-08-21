package net.refractionapi.refraction.feature.examples.task;

import net.refractionapi.refraction.feature.task.TaskHolder;

public class ExampleTaskRegistry {
    public static TaskHolder<ExampleTask> EXAMPLE = new TaskHolder<>(
            "example",
            ExampleTask::new,
            ExampleTask::new
    ).setMaxTicks(20 * 60).setDesc("Example task for Refraction");

    public static void init() {

    }
}
