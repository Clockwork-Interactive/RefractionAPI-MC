package net.refractionapi.refraction.cutscenes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.cutscenes.point.PointHandler;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetBarPropsS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetFOVS2CPacket;
import net.refractionapi.refraction.networking.S2C.SetZRotS2CPacket;
import net.refractionapi.refraction.vec3.Vec3Helper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.QUEUE;

public class Cutscene {

    public final LivingEntity livingEntity;
    public Vec3 playerTPPos;
    protected final List<PointHandler> points = new ArrayList<>();
    public ArmorStand camera;
    public boolean forced;
    public boolean started = false;
    public boolean stopped = false;
    protected Consumer<Cutscene> beforeStop;
    protected Consumer<Cutscene> afterStop;
    protected Consumer<Cutscene> cameraTick;
    protected Consumer<Cutscene> generalCameraTick;
    protected Consumer<Cutscene> afterSwitch;
    public Vec3 spawnPoint = Vec3.ZERO;
    public Vec3 target = Vec3.ZERO;
    public boolean invulnerable = true;
    protected boolean lockedPosition = true;
    public boolean lockedCamera = true;
    protected boolean lockedLook = true;
    protected final boolean defaultBars;
    public Vec3 lookAt;

    protected Cutscene(LivingEntity livingEntity, Vec3 lookAt, boolean forced, boolean defaultBars) {
        this.livingEntity = livingEntity;
        this.forced = forced;
        this.defaultBars = defaultBars;
        this.lookAt = lookAt;
        this.hideName(true);
    }

    public void tick() {
        if (this.generalCameraTick != null) {
            this.generalCameraTick.accept(this);
        }
        if (this.cameraTick != null) {
            this.cameraTick.accept(this);
        }
        if (this.points.isEmpty()) {
            this.stop();
            return;
        }
        if (this.lockedLook) {
            this.livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, this.lookAt);
        }
        if (this.lockedPosition) {
            this.livingEntity.teleportTo(this.playerTPPos.x, this.playerTPPos.y, this.playerTPPos.z);
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
        this.playerTPPos = this.livingEntity.position();
        this.createCamera();
        this.started = true;
        if (this.livingEntity instanceof ServerPlayer serverPlayer) {
            RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), serverPlayer);
            if (this.defaultBars)
                RefractionMessages.sendToPlayer(new SetBarPropsS2CPacket(true, 0, 50, 0, 0, 15, EasingFunctions.LINEAR), serverPlayer);
        }
    }

    public void stop() {
        if (this.afterStop != null) {
            this.afterStop.accept(this);
        }
        if (this.camera != null) {
            this.camera.discard();
        }

        if (this.livingEntity instanceof ServerPlayer serverPlayer)
            if (QUEUE.get(serverPlayer).isEmpty()) {
                RefractionMessages.sendToPlayer(new InvokeCutsceneS2CPacket(-1, false), serverPlayer);
                RefractionMessages.sendToPlayer(new SetFOVS2CPacket(-1), serverPlayer);
                RefractionMessages.sendToPlayer(new SetZRotS2CPacket(-1), serverPlayer);
                RefractionMessages.sendToPlayer(new SetBarPropsS2CPacket(false, 0, 0, 0, 0, 0, EasingFunctions.LINEAR), serverPlayer);
                this.hideName(false);
            }

        this.stopped = true;
    }

    public static void stopAll(LivingEntity livingEntity) {
        if (QUEUE.containsKey(livingEntity)) {
            for (Cutscene cutscene : QUEUE.get(livingEntity)) {
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

    public Cutscene generalCameraTick(Consumer<Cutscene> generalCameraTick) {
        this.generalCameraTick = generalCameraTick;
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
        //this.livingEntity.getAttribute(ForgeMod.NAMETAG_DISTANCE.get()).setBaseValue(hide ? 0.0D : 64.0D); //TODO fix multiloader
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
        this.camera = new ArmorStand(this.livingEntity.level(), this.spawnPoint.x, this.spawnPoint.y, this.spawnPoint.z);
        this.camera.setPos(this.spawnPoint);
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, this.target);
        this.camera.setInvisible(true);
        this.camera.setInvulnerable(true);
        this.camera.setNoGravity(true);
        this.camera.setCustomNameVisible(false);
        this.livingEntity.level().addFreshEntity(this.camera);
    }

    public PointHandler createPoint(int transitionTime, int lockedTime) {
        PointHandler pointHandler = new PointHandler(this, transitionTime, lockedTime);
        this.points.add(pointHandler);
        return pointHandler;
    }

    public static Cutscene create(LivingEntity livingEntity, Vec3 lookAt, boolean forced, boolean defaultBars) {
        return new Cutscene(livingEntity, lookAt, forced, defaultBars);
    }

    public static Cutscene create(LivingEntity livingEntity, Vec3 lookAt, boolean forced) {
        return create(livingEntity, lookAt, forced, true);
    }

    public static Cutscene create(LivingEntity livingEntity, boolean forced, boolean defaultBars) {
        return create(livingEntity, Vec3Helper.getVec(livingEntity, 1.0F, 0.0F), forced, defaultBars);
    }

    public static Cutscene create(LivingEntity livingEntity, boolean forced) {
        return create(livingEntity, forced, true);
    }

    public static Cutscene create(LivingEntity livingEntity) {
        return create(livingEntity, false);
    }

    public static Vec3 rightEye(LivingEntity livingEntity) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(0.0F, livingEntity.getYRot()).scale(0.1F);
        Vec3 vec31S = Vec3Helper.calculateViewVector(0.0F, livingEntity.getYRot() + 90.0F).scale(0.15F);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference);
    }

    public static Vec3 leftEye(LivingEntity livingEntity) {
        Vec3 vec3 = livingEntity.getEyePosition();
        Vec3 vec31F = Vec3Helper.calculateViewVector(0.0F, livingEntity.getYRot()).scale(0.1F);
        Vec3 vec31S = Vec3Helper.calculateViewVector(0.0F, livingEntity.getYRot() + 90.0F).scale(-0.15F);
        Vec3 FBVector = vec3.add(vec31F);
        Vec3 RLVector = vec3.add(vec31S);
        Vec3 vectorDifference = FBVector.subtract(RLVector);
        return vec3.add(vectorDifference);
    }

}
