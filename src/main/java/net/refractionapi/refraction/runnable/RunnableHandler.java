package net.refractionapi.refraction.runnable;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.refractionapi.refraction.Refraction;
import oshi.util.tuples.Triplet;

import java.util.*;
import java.util.function.Predicate;

public class RunnableHandler<T> {

    private final Runnable runnable;
    private final Predicate<T> predicate;
    private final T test;
    private int ticksLeft;

    protected RunnableHandler(Runnable runnable, int ticks, Predicate<T> predicate, T test) {
        this.runnable = runnable;
        this.ticksLeft = ticks;
        this.predicate = predicate;
        this.test = test;
        Handler.RUNNABLES.add(this);
    }

    public void tick() {
        if (this.predicate != null && this.test != null && !this.predicate.test(this.test)) {
            this.ticksLeft = -1;
            return;
        }
        if (this.runnable == null || this.ticksLeft <= 0) {
            this.ticksLeft = -1;
            return;
        }
        try {
            this.runnable.run();
        } catch (Exception e) {
            Refraction.LOGGER.error("Error while executing runnable: {}", runnable);
        }
        this.ticksLeft--;
    }

    public static void addRunnable(Runnable runnable, int ticks) {
        addRunnable(runnable, ticks, t -> true, null);
    }

    public static <T> void addRunnable(Runnable runnable, int ticks, Predicate<T> predicate, T test) {
        if (FMLEnvironment.dist.isDedicatedServer() || Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            if (ticks <= 0) {
                runnable.run();
                return;
            }

            new RunnableHandler<>(runnable, ticks, predicate, test);
        }
    }


    public static HashMap<Triplet<Runnable, Predicate<?>, ?>, Integer> runnables = new HashMap<>();


    @Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class Handler {

        private static final List<RunnableHandler<?>> RUNNABLES = new ArrayList<>();

        @SubscribeEvent
        public static void serverTick(TickEvent.ServerTickEvent event) {
            if (event.phase.equals(TickEvent.Phase.END)) return;
            ListIterator<RunnableHandler<?>> iterator = RUNNABLES.listIterator();
            while (iterator.hasNext()) {
                RunnableHandler<?> runnableHandler = iterator.next();
                runnableHandler.tick();
                if (runnableHandler.ticksLeft <= 0) {
                    iterator.remove();
                }
            }
        }
    }

}
