package net.refractionapi.refraction.cutscenes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.refractionapi.refraction.math.EasingFunction;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;

import java.util.ArrayList;
import java.util.List;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;
import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

public class Cutscene {
    protected final Vec3[] positions;
    protected final Vec3 target;
    protected Player player;
    protected int ptr = 0;
    protected final int[] time;
    protected int currentTime;
    protected int progressTracker;
    protected boolean stopped = false;
    protected boolean stopping = false;
    protected boolean started = false;
    protected final boolean shouldWait;
    protected ArmorStand camera;
    protected boolean trackCamera = false;
    protected final EasingFunction easingFunction;

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
        this.addToHandler(forced);
    }

    public void tick() {
        MinecraftForge.EVENT_BUS.post(new CutsceneTickEvent(TickEvent.Phase.START, this));
        if (this.camera.isRemoved() || this.stopped) {
            this.stop();
            return;
        }
        Vec3 updatedVec3 = this.updateVec3();
        this.camera.teleportTo(updatedVec3.x, updatedVec3.y, updatedVec3.z);
        this.camera.hurtMarked = true;
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.target);
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
        MinecraftForge.EVENT_BUS.post(new CutsceneTickEvent(TickEvent.Phase.END, this));
    }

    protected Vec3 updateVec3() {
        if (this.ptr + 1 >= this.positions.length)
            return new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        Vec3 pos = this.positions[this.ptr + 1];
        int trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
        if (new Vec3(pos.x + 0.5F, pos.y, pos.z + 0.5F).equals(this.camera.position()) || this.progressTracker >= trackedTime) {
            this.ptr++;
            if (this.ptr <= this.positions.length) {
                trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
                this.progressTracker = 0;
            }
        }
        this.progressTracker++;
        float delta = ((float) this.progressTracker / trackedTime);
        delta = this.easingFunction == EasingFunctions.LINEAR ? this.easingFunction.getEasing(delta) : this.easingFunction.getEasing(delta / 2);
        return this.camera.position().lerp(new Vec3(pos.x + 0.5F, pos.y, pos.z + 0.5F), delta);
    }

    public void stop() {
        if (this.player instanceof ServerPlayer serverPlayer) {
            if (this.camera != null) {
                ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), false), serverPlayer);
                this.camera.kill();
            }
        }
        this.stopped = true;
    }

    public void start() {
        this.createCamera();
        this.started = true;
        if (this.player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), serverPlayer);
        }
    }

    protected void addToHandler(boolean forced) {
        QUEUE.putIfAbsent(player, new ArrayList<>());
        List<Cutscene> updatedCutscene = new ArrayList<>();
        if (!forced) {
            updatedCutscene.addAll(QUEUE.get(player));
        } else {
            QUEUE.get(player).forEach(Cutscene::stop);
        }
        updatedCutscene.add(this);
        QUEUE.put(player, updatedCutscene);
    }

    protected void createCamera() {
        ArmorStand camera = new ArmorStand(player.level(), this.positions[0].x, this.positions[0].y, this.positions[0].z);
        camera.setNoGravity(true);
        camera.setInvulnerable(true);
        camera.setInvisible(true);
        camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.target);
        this.camera = camera;
        this.player.level().addFreshEntity(camera);
    }

    public Cutscene trackCamera(boolean track) {
        this.trackCamera = track;
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
        Vec3[] relativePositions = new Vec3[positions.length];
        for (int i = 0; i < positions.length; i++) {
            Vec3 pos = positions[i];
            Vec3 vec3 = player.getEyePosition();
            Vec3 vec31F = calculateViewVector(0, player.getYRot()).scale(pos.x);
            Vec3 vec31S = calculateViewVector(0, player.getYRot() + 90).scale(pos.z);
            Vec3 FBVector = vec3.add(vec31F);
            Vec3 RLVector = vec3.add(vec31S);
            Vec3 vectorDifference = FBVector.subtract(RLVector);
            Vec3 vec = vec3.add(vectorDifference);
            relativePositions[i] = vec.subtract(0.5F, 1.75F, 0.5F);
        }
        return new Cutscene(player, target, time, forced, easingFunction, relativePositions);
    }

}
