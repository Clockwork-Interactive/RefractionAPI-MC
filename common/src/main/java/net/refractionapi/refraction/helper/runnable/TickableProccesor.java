package net.refractionapi.refraction.helper.runnable;

import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.events.RefractionEvents;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

public class TickableProccesor {

    private static final HashMap<TickableProccesor, LevelAccessor> RUNNABLES = new HashMap<>();
    private boolean running = false;
    private Runnable process = () -> {
    };
    private Runnable onStop = () -> {
    };
    private BooleanSupplier supplier = () -> true;

    public TickableProccesor() {
    }

    public TickableProccesor process(Runnable process) {
        this.process = process;
        return this;
    }

    public TickableProccesor shouldRun(BooleanSupplier supplier) {
        this.supplier = supplier;
        return this;
    }

    public TickableProccesor onStop(Runnable runnable) {
        this.onStop = runnable;
        return this;
    }

    public void start(LevelAccessor level) {
        this.running = true;
        RUNNABLES.put(this, level);
    }

    public void stop() {
        this.running = false;
        this.onStop.run();
    }

    public static void init() {
        RefractionEvents.LEVEL_TICK.register((level, post) -> {
            RUNNABLES.keySet().removeIf(processor -> {
                boolean stop = !processor.running || !processor.supplier.getAsBoolean();
                if (stop) {
                    processor.stop();
                }
                return stop;
            });
            RUNNABLES.entrySet().stream().filter((entry) -> entry.getValue().equals(level)).forEach((processor) -> processor.getKey().process.run());
        });
    }

}
