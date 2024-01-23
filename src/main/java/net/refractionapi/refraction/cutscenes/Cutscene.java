package net.refractionapi.refraction.cutscenes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.refractionapi.refraction.math.EasingFunction;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.vec3.Vec3Helper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;
import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class Cutscene {
    public int ticks = 0;
    protected final Vec3[] positions;
    public Vec3 target;
    public Player player;
    protected int ptr = 0;
    protected final int[] time;
    protected int currentTime;
    protected int progressTracker;
    protected boolean stopped = false;
    protected boolean stopping = false;
    protected boolean started = false;
    protected final boolean shouldWait;
    public ArmorStand camera;
    protected boolean trackCamera = false;
    protected final EasingFunction easingFunction;
    protected final Vec3 lookedAt;
    protected final Vec3 originalPosition;
    protected boolean lockedCamera = true;
    protected boolean lockedPosition = true;
    protected int fov = -1;
    protected Consumer<Cutscene> afterStop = null;
    protected Consumer<Cutscene> tickCamera = null;
    protected Consumer<Cutscene> afterSwitch = null;
    public Entity dynamicTarget = null;
    public Vec3 dynamicTargetOffset = new Vec3(0, 0, 0);
    public Vec3 initalPosition;
    public Vec3 initalPlayerPos;
    public float delta = 0.0F;

    /**
     * Example cutscenes:
     * time{40,40}, blockPos{1,2} - When the end is reached, it will wait an additional 40 ticks before ending.
     * time{40}, blockPos{1,2} - When the end is reached, it will end immediately.
     *
     * @param player    Player that sees the cutscene.
     * @param target    Where the camera will focus to.
     * @param time      Time positions.
     * @param forced    Should the cutscene delete the previous queue.
     * @param positions Block positions.
     */
    public Cutscene(Player player, Vec3 target, int[] time, boolean forced, EasingFunction easingFunction, Vec3... positions) {
        if (time.length > positions.length) {
            throw new RuntimeException("Can't have more time indexes than position indexes!");
        }
        if (positions.length > time.length + 1) {
            throw new RuntimeException("Can't have more than 1 extra position index than time indexes!");
        }
        this.player = player;
        this.target = target;
        this.positions = positions;
        this.time = time;
        this.currentTime = time[0];
        this.shouldWait = this.positions.length == this.time.length;
        this.easingFunction = easingFunction;
        this.lookedAt = this.player.getEyePosition().add(calculateViewVector(0, this.player.getYRot()));
        this.originalPosition = this.player.getPosition(1.0F);
        this.player.hurtMarked = true;
        this.player.setDeltaMovement(0, 0, 0);
        this.initalPlayerPos = this.player.getEyePosition();
        this.addToHandler(forced);
    }

    public void tick() {
        MinecraftForge.EVENT_BUS.post(new CutsceneTickEvent(TickEvent.Phase.START, this));
        if (this.camera.isRemoved() || this.stopped) {
            this.stop();
            return;
        }
        this.ticks++;
        if (this.lockedCamera) {
            this.player.lookAt(EntityAnchorArgument.Anchor.EYES, this.lookedAt);
        }
        if (this.lockedPosition) {
            this.player.teleportTo(this.originalPosition.x, this.originalPosition.y, this.originalPosition.z);
        }
        Vec3 updatedVec3 = this.updateVec3();
        this.camera.teleportTo(updatedVec3.x, updatedVec3.y, updatedVec3.z);
        this.camera.hurtMarked = true;
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.dynamicTarget == null ? this.target : this.dynamicTarget.getEyePosition().add(this.dynamicTargetOffset));
        if (this.trackCamera) {
            this.player.lookAt(EntityAnchorArgument.Anchor.EYES, this.camera.getEyePosition());
        }
        if (this.ptr >= this.positions.length - 1 && !this.stopping) {
            if (this.shouldWait) {
                this.stopping = true;
                RunnableCooldownHandler.addDelayedRunnable(() -> this.stopped = true, this.time[this.time.length - 1]);
            } else {
                this.stopping = true;
                this.stopped = true;
            }
        }
        if (this.tickCamera != null) {
            this.tickCamera.accept(this);
        }
        MinecraftForge.EVENT_BUS.post(new CutsceneTickEvent(TickEvent.Phase.END, this));
    }

    protected Vec3 updateVec3() {
        if (this.ptr + 1 >= this.positions.length)
            return new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        Vec3 pos = this.positions[this.ptr + 1];
        int trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
        if (this.progressTracker >= trackedTime) {
            this.ptr++;
            if (this.ptr <= this.positions.length) {
                if (this.afterSwitch != null)
                    this.afterSwitch.accept(this);
                trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
                this.progressTracker = 0;
                this.initalPosition = new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
            }
        }
        this.progressTracker++;
        float delta = (float) this.progressTracker / (float) trackedTime;
        delta = this.easingFunction == EasingFunctions.LINEAR ? delta : this.easingFunction.getEasing(delta);
        this.delta = delta;
        return initalPosition.lerp(pos.add(0.5F, 0, 0.5F), delta);
    }

    public void stop() {
        if (this.afterStop != null) {
            this.afterStop.accept(this);
        }
        if (this.player instanceof ServerPlayer serverPlayer) {
            if (this.camera != null) {
                if (QUEUE.get(this.player).size() <= 1) {
                    RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), false), serverPlayer);
                }
                this.camera.kill();
            }
            RefractionMessages.sendToPlayer(new SetFOVS2CPacket(-1), serverPlayer);

        }
        this.stopped = true;
    }

    public static void stopAll(Player player) {
        if (player instanceof ServerPlayer) {
            QUEUE.get(player).forEach(Cutscene::stop);
        }
    }

    public void start() {
        this.createCamera();
        this.started = true;
        if (this.player instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), serverPlayer);
            RefractionMessages.sendToPlayer(new SetFOVS2CPacket(this.fov), serverPlayer);
        }
    }

    protected void addToHandler(boolean forced) {
        QUEUE.putIfAbsent(this.player, new ArrayList<>());
        List<Cutscene> updatedCutscene = new ArrayList<>();
        if (!forced) {
            updatedCutscene.addAll(QUEUE.get(this.player));
        } else {
            QUEUE.get(this.player).forEach(Cutscene::stop);
        }
        updatedCutscene.add(this);
        QUEUE.put(this.player, updatedCutscene);
    }

    protected void createCamera() {
        ArmorStand camera = new ArmorStand(player.level(), this.positions[0].x, this.positions[0].y, this.positions[0].z);
        camera.setNoGravity(true);
        camera.setInvulnerable(true);
        camera.setInvisible(true);
        camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.dynamicTarget == null ? this.target : this.dynamicTarget.getEyePosition().add(this.dynamicTargetOffset));
        this.camera = camera;
        this.player.level().addFreshEntity(camera);
        this.initalPosition = new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
    }

    public Cutscene trackCamera(boolean track) {
        this.trackCamera = track;
        return this;
    }

    public Cutscene lockCamera(boolean lock) {
        this.lockedCamera = lock;
        return this;
    }

    public Cutscene lockPosition(boolean lock) {
        this.lockedPosition = lock;
        return this;
    }

    public Cutscene setFOV(int fov) {
        if (this.player instanceof ServerPlayer serverPlayer) {
            this.fov = fov;
        }
        return this;
    }

    public Cutscene dynamicPosition(Entity target, @Nullable Vec3 offset) {
        this.dynamicTarget = target;
        this.dynamicTargetOffset = offset == null ? new Vec3(0, 0, 0) : offset;
        return this;
    }

    public Cutscene lerpCamera(Vec3 start, Vec3 end, int lerpTicks) {
        this.target = start;
        this.lerpCamera(end, lerpTicks);
        return this;
    }

    public Cutscene lerpCamera(Vec3 end, int lerpInTicks) {
        AtomicInteger lerpTicks = new AtomicInteger();
        this.tickCamera(cutscene -> {
            if (lerpTicks.get() < lerpInTicks) {
                lerpTicks.getAndIncrement();
                cutscene.target = cutscene.target.lerp(end, ((double) this.ticks / lerpInTicks));
            }
        });
        return this;
    }

    public Cutscene tickCamera(Consumer<Cutscene> consumer) {
        this.tickCamera = consumer;
        return this;
    }

    public Cutscene afterStop(Consumer<Cutscene> consumer) {
        this.afterStop = consumer;
        return this;
    }

    public Cutscene afterSwitch(Consumer<Cutscene> consumer) {
        this.afterSwitch = consumer;
        return this;
    }

    public static List<Cutscene> createMultiPlayerCutscene(List<Player> players, Vec3 target, int[] time, boolean forced, EasingFunction easingFunction, Vec3... positions) {
        List<Cutscene> cutscenes = new ArrayList<>();
        for (Player player : players) {
            cutscenes.add(new Cutscene(player, target, time, forced, easingFunction, positions));
        }
        return cutscenes;
    }

    public static Cutscene createRelativeCutscene(Player player, Vec3 target, int[] time, boolean forced, EasingFunction easingFunction, Vec3... positions) {
        Vec3[] relativePositions = new Vec3[positions.length];
        for (int i = 0; i < positions.length; i++) {
            Vec3 pos = positions[i];
            relativePositions[i] = new Vec3(player.getBlockX() + pos.x, player.getBlockY() + pos.y, player.getBlockZ() + pos.z);
        }
        return new Cutscene(player, target, time, forced, easingFunction, relativePositions);
    }

    /**
     * @param positions = X = +forward ; -backwards, Y = +up ; -down, Z = +left ; -right
     */
    public static Cutscene createFacingRelativeCutscene(Player player, Vec3 target, int[] time, boolean forced, EasingFunction easingFunction, Vec3... positions) {
        return createFacingRelativeCutscene(player, player, target, time, forced, easingFunction, positions);
    }


    /**
     * @param positions = X = +forward ; -backwards, Y = +up ; -down, Z = +left ; -right
     */
    public static Cutscene createFacingRelativeCutscene(Player player, Entity relativeTo, Vec3 target, int[] time, boolean forced, EasingFunction easingFunction, Vec3... positions) {
        Vec3[] relativePositions = new Vec3[positions.length];
        for (int i = 0; i < positions.length; i++) {
            Vec3 pos = positions[i];
            Vec3 vec3 = relativeTo.getEyePosition();
            Vec3 vec31F = calculateViewVector(0, relativeTo.getYRot()).scale(pos.x);
            Vec3 vec31S = calculateViewVector(0, relativeTo.getYRot() + 90).scale(pos.z);
            Vec3 FBVector = vec3.add(vec31F);
            Vec3 RLVector = vec3.add(vec31S);
            Vec3 vectorDifference = FBVector.subtract(RLVector);
            Vec3 vec = vec3.add(vectorDifference);
            relativePositions[i] = vec.subtract(0.0F, 1.75F, 0.0F).add(0.0F, pos.y, 0.0F);
        }
        if (relativePositions.length > 1) {
            for (int i = 1; i < relativePositions.length; i++) {
                relativePositions[i] = relativePositions[i].subtract(0.5F, 0.0F, 0.5F);
            }
        }
        return new Cutscene(player, target, time, forced, easingFunction, relativePositions);
    }

    public static Vec3 rightEye(Player player) {
        Vec3 vec3 = player.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(0.0F, player.getYRot()).scale(0.1F);
        Vec3 vec31S = Vec3Helper.calculateViewVector(0.0F, player.getYRot() + 90.0F).scale(0.15F);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference);
    }

    public static Vec3 leftEye(Player player) {
        Vec3 vec3 = player.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(0.0F, player.getYRot()).scale(0.1F);
        Vec3 vec31S = Vec3Helper.calculateViewVector(0.0F, player.getYRot() + 90.0F).scale(-0.15F);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference);
    }

}
