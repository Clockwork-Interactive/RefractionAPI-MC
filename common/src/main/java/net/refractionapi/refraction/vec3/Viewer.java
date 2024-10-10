package net.refractionapi.refraction.vec3;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.runnable.TickableProccesor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Viewer {

    protected final LevelAccessor level;
    protected Vec3 origin = Vec3.ZERO;
    protected Consumer<Entity> onLook = (e) -> {};
    protected Consumer<Entity> onStopLooking = (e) -> {};
    protected Consumer<Entity> whileLooking = (e) -> {};
    protected Runnable noObservor = () -> {};
    protected Runnable hasObservor = () -> {};
    protected Predicate<Entity> entityPredicate = (e) -> true;
    protected float fovRange = 90.0F;
    protected float range = 12.0F;
    protected List<Entity> previous = new ArrayList<>();
    private boolean discarded = false;
    private BooleanSupplier shouldRun = () -> !this.discarded;

    public Viewer(LevelAccessor level) {
        this.level = level;
        new TickableProccesor().process(this::tick).shouldRun(this.shouldRun).start(this.level);
    }

    public Viewer setOrigin(Vec3 origin) {
        this.origin = origin;
        return this;
    }

    public Viewer setOrigin(double x, double y, double z) {
        this.origin = new Vec3(x, y, z);
        return this;
    }

    public Viewer onLook(Consumer<Entity> consumer) {
        this.onLook = consumer;
        return this;
    }

    public Viewer onStopLooking(Consumer<Entity> consumer) {
        this.onStopLooking = consumer;
        return this;
    }

    public Viewer whileLooking(Consumer<Entity> consumer) {
        this.whileLooking = consumer;
        return this;
    }

    public Viewer noObservor(Runnable runnable) {
        this.noObservor = runnable;
        return this;
    }

    public Viewer hasObservor(Runnable runnable) {
        this.hasObservor = runnable;
        return this;
    }

    public Viewer predicate(Predicate<Entity> predicate) {
        this.entityPredicate = predicate;
        return this;
    }

    public Viewer setFovRange(float range) {
        this.fovRange = range;
        return this;
    }

    public Viewer setRange(float range) {
        this.range = range;
        return this;
    }

    public Viewer shouldRun(BooleanSupplier shouldRun) {
        this.shouldRun = shouldRun;
        return this;
    }

    public void discard() {
        this.discarded = true;
    }

    protected void tick() {
        List<Entity> lookers = this.getLookers();
        lookers.stream().filter((entity) -> this.previous.stream().noneMatch((e) -> e.equals(entity))).forEach((newEntity) -> this.onLook.accept(newEntity));
        this.previous.stream().filter((entity) -> lookers.stream().noneMatch((e) -> e.equals(entity))).forEach((oldEntity) -> this.onStopLooking.accept(oldEntity));
        if (!lookers.isEmpty()) {
            lookers.forEach(this.whileLooking);
            this.hasObservor.run();
        } else
            this.noObservor.run();
        this.previous = lookers;
    }

    protected List<Entity> getLookers() {
        return this.level.getEntities((Entity) null, new AABB(this.origin.add(this.range, this.range, this.range), this.origin.subtract(this.range, this.range, this.range)), (entity) -> Vec3Helper.isInAngle(entity, this.origin, this.fovRange) && this.entityPredicate.test(entity));
    }


}
