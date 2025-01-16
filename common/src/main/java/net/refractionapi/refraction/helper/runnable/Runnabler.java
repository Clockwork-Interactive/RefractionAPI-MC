package net.refractionapi.refraction.helper.runnable;

import net.refractionapi.refraction.events.RefractionEvents;
import org.jetbrains.annotations.ApiStatus;

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
    private int delayTicksStart = 0;
    private int ticksLeft = 1;
    private int ticksLeftStart = 1;
    private boolean isClient;

    private Runnabler() {

    }

    public Runnabler delay(int ticks) {
        this.delayTicks = ticks;
        this.delayTicksStart = ticks;
        return this;
    }

    public Runnabler runtimeTicks(int ticks) {
        this.ticksLeft = ticks;
        this.ticksLeftStart = ticks;
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

    public Runnabler delayRun(int ticks, Consumer<Runnabler> delayRun) {
        this.delay(ticks);
        this.onRun = delayRun;
        this.start();
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
    public Runnabler run(int delayTicks, int runtimeTicks, Consumer<Runnabler> run) {
        this.delay(delayTicks);
        this.runtimeTicks(runtimeTicks);
        this.run = run;
        processes.add(this);
        return this;
    }

    public Runnabler client() {
        this.isClient = true;
        return this;
    }

    public float delayProgress(int ticks) {
        return (float) (ticks - this.delayTicks) / ticks;
    }

    public float runtimeProgress(int ticks) {
        return (float) (ticks - this.ticksLeft) / ticks;
    }

    public float delayProgress() {
        return delayProgress(this.delayTicksStart);
    }

    public float runtimeProgress() {
        return runtimeProgress(this.ticksLeftStart);
    }

    public Runnabler start() {
        return run(this.delayTicks, this.ticksLeft, this.run);
    }

    public Runnabler run(int runtimeTicks, Consumer<Runnabler> run) {
        return run(0, runtimeTicks, run);
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

    public static Runnabler createClient() {
        return create().client();
    }

    @ApiStatus.Internal
    public static void init() {
        RefractionEvents.COMMON_TICK.register((server, post) -> {
            if (post) return;
            for (Runnabler process : processes) {
                if ((process.isClient && server) || (!process.isClient && !server)) continue;
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
