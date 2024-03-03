package net.refractionapi.refraction.cutscenes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.refractionapi.refraction.cutscenes.point.PointHandler;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetZRotS2CPacket;
import net.refractionapi.refraction.vec3.Vec3Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;

public class Cutscene {

    public final ServerPlayer player;
    protected Vec3 playerTPPos;
    protected final List<PointHandler> points = new ArrayList<>();
    public ArmorStand camera;
    public boolean forced;
    public boolean started = false;
    public boolean stopped = false;
    protected Consumer<Cutscene> beforeStop;
    protected Consumer<Cutscene> afterStop;
    protected Consumer<Cutscene> cameraTick;
    protected Consumer<Cutscene> afterSwitch;
    public Vec3 spawnPoint = Vec3.ZERO;
    public Vec3 target = Vec3.ZERO;
    public boolean invulnerable = true;
    protected boolean lockedPosition = true;
    public boolean lockedCamera = true;
    protected boolean lockedLook = true;
    public Vec3 lookAt;

    protected Cutscene(ServerPlayer player, Vec3 lookAt, boolean forced) {
        this.player = player;
        this.forced = forced;
        this.lookAt = lookAt;
        this.hideName(true);
    }

    public void tick() {
        if (this.cameraTick != null) {
            this.cameraTick.accept(this);
        }
        if (this.points.isEmpty()) {
            this.stop();
            return;
        }
        if (this.lockedLook) {
            this.player.lookAt(EntityAnchorArgument.Anchor.EYES, this.lookAt);
        }
        if (this.lockedPosition) {
            this.player.teleportTo(this.playerTPPos.x, this.playerTPPos.y, this.playerTPPos.z);
        }
        PointHandler current = this.points.get(0);
        if (!current.isSwitched()) {
            current.onSwitch();
        }
        current.tickPoints();
        if (current.getTransitionTime() <= 0 && current.getLockedTime() <= 0) {
            this.points.remove(0);
        }
    }

    protected void start() {
        this.playerTPPos = this.player.position();
        this.createCamera();
        this.started = true;
        RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), this.player);
    }

    public void stop() {
        if (this.afterStop != null) {
            this.afterStop.accept(this);
        }
        if (this.camera != null) {
            this.camera.kill();
        }

        if (QUEUE.get(this.player).isEmpty()) {
            RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(-1, false), this.player);
            RefractionMessages.sendToPlayer(new SetFOVS2CPacket(-1), this.player);
            RefractionMessages.sendToPlayer(new SetZRotS2CPacket(-1), this.player);
            this.hideName(false);
        }

        this.stopped = true;
    }

    public static void stopAll(Player player) {
        if (QUEUE.containsKey(player)) {
            for (Cutscene cutscene : QUEUE.get(player)) {
                cutscene.stop();
            }
        }
    }

    public Cutscene afterStop(Consumer<Cutscene> afterStop) {
        this.afterStop = afterStop;
        return this;
    }

    public Cutscene beforeStop(Consumer<Cutscene> beforeStop) {
        this.beforeStop = beforeStop;
        return this;
    }

    public Cutscene tickCamera(Consumer<Cutscene> cameraTick) {
        this.cameraTick = cameraTick;
        return this;
    }

    public Cutscene afterSwitch(Consumer<Cutscene> afterSwitch) {
        this.afterSwitch = afterSwitch;
        return this;
    }

    public Cutscene lockPosition(boolean lock) {
        this.lockedPosition = lock;
        return this;
    }

    public Cutscene lockCamera(boolean lock) {
        this.lockedCamera = lock;
        return this;
    }

    public Cutscene hideName(boolean hide) {
        this.player.getAttribute(ForgeMod.NAMETAG_DISTANCE.get()).setBaseValue(hide ? 0.0D : 64.0D);
        return this;
    }

    public Cutscene setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        return this;
    }

    public Cutscene lockLook(boolean lock) {
        this.lockedLook = lock;
        return this;
    }

    public void createCamera() {
        this.camera = new ArmorStand(this.player.level(), this.spawnPoint.x, this.spawnPoint.y, this.spawnPoint.z);
        this.camera.setPos(this.spawnPoint);
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.target);
        this.camera.setInvisible(true);
        this.camera.setInvulnerable(true);
        this.camera.setNoGravity(true);
        this.camera.setCustomNameVisible(false);
        this.player.level().addFreshEntity(this.camera);
    }

    public PointHandler createPoint(int transitionTime, int lockedTime) {
        PointHandler pointHandler = new PointHandler(this, transitionTime, lockedTime);
        this.points.add(pointHandler);
        return pointHandler;
    }

    public static Cutscene create(Player player, boolean forced) {
        return Cutscene.create(player, Vec3Helper.getVec(player, 1.0F, 0.0F), forced);
    }

    public static Cutscene create(Player player, Vec3 lookAt, boolean forced) {
        if (player instanceof ServerPlayer serverPlayer) {
            return new Cutscene(serverPlayer, lookAt, forced);
        }
        return null;
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
