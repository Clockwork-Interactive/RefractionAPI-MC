package net.refractionapi.refraction.cutscenes;

import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.refractionapi.refraction.networking.ModMessages;
import net.refractionapi.refraction.networking.S2C.InvokeCutsceneS2CPacket;
import net.refractionapi.refraction.runnable.RunnableCooldownHandler;
import net.refractionapi.refraction.vec3.Vec3Helper;

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.ACTIVE_CUTSCENES;

public class Cutscene {
    protected final BlockPos[] positions;
    protected final BlockPos target;
    protected PlayerEntity player;
    protected int ptr = 0;
    protected final int[] time;
    protected int currentTime;
    protected boolean stopped = false;
    protected boolean stopping = false;
    protected ArmorStandEntity camera;

    public Cutscene(PlayerEntity player, BlockPos target, int[] time, BlockPos... positions) {
        if (positions.length > time.length || time.length > positions.length) {
            throw new RuntimeException("Time and Position size need to be the same!");
        }
        ACTIVE_CUTSCENES.forEach((cutscene -> {
            if (cutscene.player == player) {
                cutscene.stop();
            }
        }));
        this.player = player;
        this.target = target;
        this.positions = positions;
        for (int i = 0; i < time.length; i++) {
            time[i] = time[i] * 2;
        }
        this.time = time;
        this.currentTime = time[0];
        this.createCamera();
        if (this.player instanceof ServerPlayerEntity) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), (ServerPlayerEntity) this.player);
        }
        ACTIVE_CUTSCENES.add(this);
    }

    public void tick() {
        if (!this.camera.isAlive() || this.stopped) {
            this.stop();
            return;
        }
        this.camera.teleportTo(this.updateVec3().x, this.updateVec3().y, this.updateVec3().z);
        this.camera.hurtMarked = true;
        this.camera.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(this.target.getX(), this.target.getY(), this.target.getZ()));
        if (this.ptr >= this.positions.length - 1 && !this.stopping) {
            this.stopping = true;
            RunnableCooldownHandler.addDelayedRunnable(() -> this.stopped = true, this.time[this.time.length - 1]);
        }
    }

    private Vector3d updateVec3() {
        if (this.ptr + 1 >= this.positions.length)
            return new Vector3d(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        BlockPos pos = this.positions[this.ptr + 1];
        this.currentTime--;
        if (new Vector3d(pos.getX(), pos.getY(), pos.getZ()).equals(this.camera.position()) || this.currentTime <= 0) {
            this.ptr++;
            if (this.ptr + 1 < this.time.length) {
                this.currentTime = this.time[this.ptr];
            }
            return new Vector3d(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        }
        double delta = (double) 3 / this.currentTime; // FIXME
        return Vec3Helper.lerp(this.camera.position(), new Vector3d(pos.getX(), pos.getY(), pos.getZ()), delta);
    }

    private void stop() {
        if (this.player instanceof ServerPlayerEntity) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), false), (ServerPlayerEntity) this.player);
        }
        this.stopped = true;
        this.camera.kill();
    }

    private void createCamera() {
        ArmorStandEntity camera = new ArmorStandEntity(this.player.level, this.positions[0].getX(), this.positions[0].getY(), this.positions[0].getZ());
        camera.setNoGravity(true);
        camera.setInvulnerable(true);
        camera.setInvisible(true);
        camera.lookAt(EntityAnchorArgument.Type.EYES, new Vector3d(this.target.getX(), this.target.getY(), this.target.getZ()));
        this.camera = camera;
        this.player.level.addFreshEntity(camera);
    }

}
