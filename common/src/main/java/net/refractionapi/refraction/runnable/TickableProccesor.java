package net.refractionapi.refraction.runnable;

import net.minecraft.world.level.LevelAccessor;
import net.refractionapi.refraction.platform.RefractionServices;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

public class TickableProccesor {

    private static final HashMap<TickableProccesor, LevelAccessor> RUNNABLES = new HashMap<>();
    private boolean running = false;
    private Runnable process = () -> {};
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

    public void start(LevelAccessor level) {
        this.running = true;
        RUNNABLES.put(this, level);
    }

    public void stop() {
        this.running = false;
    }

    public static void init() {
        RefractionServices.EVENTS.addLevelProcess((level) -> {
            RUNNABLES.keySet().removeIf(processor -> !processor.running || !processor.supplier.getAsBoolean());
            RUNNABLES.entrySet().stream().filter((entry) -> entry.getValue().equals(level)).forEach((processor) -> processor.getKey().process.run());
        });
    }

}
