package net.refractionapi.refraction.runnable;

import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

public class TickableProccesor {

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
        Handler.RUNNABLES.put(this, level);
    }

    public void stop() {
        this.running = false;
    }

    @Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class Handler {

        private static final HashMap<TickableProccesor, LevelAccessor> RUNNABLES = new HashMap<>();

        @SubscribeEvent
        public static void unloadLevel(LevelEvent.Unload event) {
            RUNNABLES.entrySet().removeIf(entry -> entry.getValue().equals(event.getLevel()));
        }

        @SubscribeEvent
        public static void tickLevel(TickEvent.LevelTickEvent event) {
            RUNNABLES.keySet().removeIf(processor -> !processor.running || !processor.supplier.getAsBoolean());
            RUNNABLES.entrySet().stream().filter((entry) -> entry.getValue().equals(event.level)).forEach((processor) -> processor.getKey().process.run());
        }

    }

}
