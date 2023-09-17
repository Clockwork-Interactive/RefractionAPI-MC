package net.refractionapi.refraction.cutscenes;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;

import java.util.ArrayList;
import java.util.List;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.ACTIVE_CUTSCENES;

public class Cutscene {
    protected final BlockPos[] positions;
    protected final BlockPos target;
    protected Player player;
    protected int ptr = 0;
    protected final int[] time;
    protected int currentTime;
    protected int progressTracker;
    protected boolean stopped = false;
    protected boolean stopping = false;
    protected final boolean shouldWait;
    protected ArmorStand camera;

    /**
     * Example cutscenes:
     * time{40,40}, blockPos{1,2} - When the end is reached, it will wait an additional 40 ticks before ending.
     * time{40}, blockPos{1,2} - When the end is reached, it will end immediately.
     *
     * @param player    Player that sees the cutscene.
     * @param target    Where the camera will focus to.
     * @param time      Time positions.
     * @param positions Block positions.
     */
    public Cutscene(Player player, BlockPos target, int[] time, BlockPos... positions) {
        if (time.length > positions.length) {
            throw new RuntimeException("Can't have more time indexes than position indexes!");
        }
        if (positions.length > time.length + 1) {
            throw new RuntimeException("Can't have more than 1 extra position index than time indexes!");
        }
        ACTIVE_CUTSCENES.forEach((cutscene -> {
            if (cutscene.player == player) {
                cutscene.stop();
            }
        }));
        this.player = player;
        this.target = target;
        this.positions = positions;
        this.time = time;
        this.currentTime = time[0];
        this.createCamera();
        if (this.player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), serverPlayer);
        }
        this.shouldWait = this.positions.length == this.time.length;
        ACTIVE_CUTSCENES.add(this);
    }

    public void tick() {
        if (this.camera.isRemoved() || this.stopped) {
            this.stop();
            return;
        }
        Vec3 updatedVec3 = this.updateVec3();
        this.camera.teleportTo(updatedVec3.x, updatedVec3.y, updatedVec3.z);
        this.camera.hurtMarked = true;
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.target.getX(), this.target.getY(), this.target.getZ()));
        if (this.ptr >= this.positions.length - 1 && !this.stopping) {
            if (this.shouldWait) {
                this.stopping = true;
                RunnableCooldownHandler.addDelayedRunnable(() -> this.stopped = true, this.time[this.time.length - 1]);
            } else {
                this.stopping = true;
                this.stopped = true;
            }
        }
    }

    protected Vec3 updateVec3() {
        if (this.ptr + 1 >= this.positions.length)
            return new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        BlockPos pos = this.positions[this.ptr + 1];
        int trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
        if (new Vec3(pos.getX(), pos.getY(), pos.getZ()).equals(this.camera.position()) || this.progressTracker >= trackedTime) {
            this.ptr++;
            if (this.ptr <= this.positions.length) {
                trackedTime = this.ptr >= this.time.length ? 1 : this.time[this.ptr];
                this.progressTracker = 0;
            }
        }
        this.progressTracker++;
        float delta = ((float) this.progressTracker / trackedTime) / 10;
        return this.camera.position().lerp(new Vec3(pos.getX(), pos.getY(), pos.getZ()), delta);
    }

    protected void stop() {
        if (this.player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), false), serverPlayer);
        }
        this.stopped = true;
        this.camera.kill();
    }

    protected void createCamera() {
        ArmorStand camera = new ArmorStand(player.level(), this.positions[0].getX(), this.positions[0].getY(), this.positions[0].getZ());
        camera.setNoGravity(true);
        camera.setInvulnerable(true);
        camera.setInvisible(true);
        camera.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.target.getX(), this.target.getY(), this.target.getZ()));
        this.camera = camera;
        this.player.level().addFreshEntity(camera);
    }

    public static List<Cutscene> createMultiPlayerCutscene(List<Player> players, BlockPos target, int[] time, BlockPos... positions) {
        List<Cutscene> cutscenes = new ArrayList<>();
        for (Player player : players) {
            cutscenes.add(new Cutscene(player, target, time, positions));
        }
        return cutscenes;
    }

}
