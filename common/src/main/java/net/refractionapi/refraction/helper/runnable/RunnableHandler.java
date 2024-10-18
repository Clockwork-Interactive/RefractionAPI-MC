package net.refractionapi.refraction.helper.runnable;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class RunnableHandler<T> {

    public static final List<RunnableHandler<?>> RUNNABLES = new ArrayList<>();
    private final Runnable runnable;
    private final Predicate<T> predicate;
    private final T test;
    private int ticksLeft;
    private Consumer<T> onEnd;

    protected RunnableHandler(Runnable runnable, int ticks, Predicate<T> predicate, T test) {
        this.runnable = runnable;
        this.ticksLeft = ticks;
        this.predicate = predicate;
        this.test = test;
        RUNNABLES.add(this);
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

    public static RunnableHandler<?> addRunnable(Runnable runnable, int ticks) {
        return addRunnable(runnable, ticks, t -> true, null);
    }

    public static <T> RunnableHandler<T> addRunnable(Runnable runnable, int ticks, Predicate<T> predicate, T test) {
        if (ticks <= 0) {
            runnable.run();
            return null;
        }
        return new RunnableHandler<>(runnable, ticks, predicate, test);
    }

    public void onEnd(Consumer<T> consumer) {
        this.onEnd = consumer;
    }

    private void end() {
        if (this.onEnd != null) {
            this.onEnd.accept(this.test);
        }
    }

    public static void init() {
        RefractionEvents.SERVER_TICK.register((post) -> {
            ListIterator<RunnableHandler<?>> iterator = RUNNABLES.listIterator();
            while (iterator.hasNext()) {
                RunnableHandler<?> runnableHandler = iterator.next();
                runnableHandler.tick();
                if (runnableHandler.ticksLeft <= 0) {
                    runnableHandler.end();
                    iterator.remove();
                }
            }
        });
    }

}
