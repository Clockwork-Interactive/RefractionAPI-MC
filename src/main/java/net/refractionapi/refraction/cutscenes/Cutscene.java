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

import static net.refractionapi.refraction.cutscenes.CutsceneHandler.ACTIVE_CUTSCENES;

public class Cutscene {
    protected final BlockPos[] positions;
    protected final BlockPos target;
    protected Player player;
    protected int ptr = 0;
    protected final int[] time;
    protected int currentTime;
    protected boolean stopped = false;
    protected boolean stopping = false;
    protected ArmorStand camera;

    public Cutscene(Player player, BlockPos target, int[] time, BlockPos... positions) {
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
        if (this.player instanceof ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new InvokeCutsceneS2CPacket(this.camera.getId(), true), serverPlayer);
        }
        ACTIVE_CUTSCENES.add(this);
    }

    public void tick() {
        if (this.camera.isRemoved() || this.stopped) {
            this.stop();
            return;
        }
        this.camera.teleportTo(this.updateVec3().x, this.updateVec3().y, this.updateVec3().z);
        this.camera.hurtMarked = true;
        this.camera.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(this.target.getX(), this.target.getY(), this.target.getZ()));
        if (this.ptr >= this.positions.length - 1 && !this.stopping) {
            this.stopping = true;
            RunnableCooldownHandler.addDelayedRunnable(() -> this.stopped = true, this.time[this.time.length - 1]);
        }
    }

    protected Vec3 updateVec3() {
        if (this.ptr + 1 >= this.positions.length) return new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        BlockPos pos = this.positions[this.ptr + 1];
        this.currentTime--;
        if (new Vec3(pos.getX(), pos.getY(), pos.getZ()).equals(this.camera.position()) || this.currentTime <= 0) {
            this.ptr++;
            if (this.ptr + 1 < this.time.length) {
                this.currentTime = this.time[this.ptr];
            }
            return new Vec3(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        }
        double delta = (double) 3 / this.currentTime;
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

}
