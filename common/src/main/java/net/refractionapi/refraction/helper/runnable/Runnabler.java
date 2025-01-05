package net.refractionapi.refraction.helper.runnable;

import net.refractionapi.refraction.events.RefractionEvents;
import org.apache.logging.log4j.util.InternalApi;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class Runnabler {

    private static final CopyOnWriteArrayList<Runnabler> processes = new CopyOnWriteArrayList<>();
    private Consumer<Runnabler> delayRun = (runnabler) -> {
    };
    private Consumer<Runnabler> onRun = (runnabler) -> {
    };
    private Consumer<Runnabler> run = (runnabler) -> {
    };
    private BiConsumer<Runnabler, StopCase> onStop = (runnabler, stopCase) -> {
    };
    private Consumer<Runnabler> masterProcess = (runnabler) -> {
    };
    private BooleanSupplier test = () -> true;
    private int delayTicks = 0;
    private int ticksLeft = 1;

    private Runnabler() {

    }

    public Runnabler delay(int ticks) {
        this.delayTicks = ticks;
        return this;
    }

    public Runnabler runtimeTicks(int ticks) {
        this.ticksLeft = ticks;
        return this;
    }

    public int delayLeft() {
        return this.delayTicks;
    }

    public int ticksLeft() {
        return this.ticksLeft;
    }

    public Runnabler run(Consumer<Runnabler> run) {
        this.run = run;
        return this;
    }

    public Runnabler delayRun(Consumer<Runnabler> delayRun) {
        this.delayRun = delayRun;
        return this;
    }

    public Runnabler onRun(Consumer<Runnabler> onRun) {
        this.onRun = onRun;
        return this;
    }

    public Runnabler onStop(BiConsumer<Runnabler, StopCase> onStop) {
        this.onStop = onStop;
        return this;
    }

    public Runnabler masterProcess(Consumer<Runnabler> masterProcess) {
        this.masterProcess = masterProcess;
        return this;
    }

    public Runnabler test(BooleanSupplier test) {
        this.test = test;
        return this;
    }

    /**
     * set runtimeTicks to -1 to run infinitely
     */
    public void run(int delayTicks, int runtimeTicks, Consumer<Runnabler> run) {
        this.delayTicks = delayTicks;
        this.ticksLeft = runtimeTicks;
        this.run = run;
        processes.add(this);
    }

    public void start() {
        run(this.delayTicks, this.ticksLeft, this.run);
    }

    public void run(int runtimeTicks, Consumer<Runnabler> run) {
        run(0, runtimeTicks, run);
    }

    public void stop() {
        stop(StopCase.FORCED);
    }

    public void stop(StopCase stopCase) {
        this.onStop.accept(this, stopCase);
        processes.remove(this);
    }

    public static Runnabler create() {
        return new Runnabler();
    }

    @InternalApi
    public static void init() {
        RefractionEvents.SERVER_TICK.register(post -> {
            if (post) return;
            for (Runnabler process : processes) {
                if (!process.test.getAsBoolean()) {
                    process.stop(StopCase.TEST);
                    continue;
                }
                process.masterProcess.accept(process);
                if (process.delayTicks > 0) {
                    process.delayRun.accept(process);
                    process.delayTicks--;
                    continue;
                } else if (process.delayTicks == 0) {
                    process.delayTicks--;
                    process.onRun.accept(process);
                }
                if (process.ticksLeft > 0 || process.ticksLeft == -1) {
                    process.run.accept(process);
                    if (process.ticksLeft != -1)
                        process.ticksLeft--;
                } else {
                    process.stop(StopCase.FINISHED);
                }
            }
        });
    }

    public enum StopCase {
        FINISHED,
        FORCED,
        TEST
    }

}
