package net.refractionapi.refraction.helper.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.networking.S2C.EnablePlayerMovementS2CPacket;
import net.refractionapi.refraction.util.Mutable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class FrozenManager {
    private static final ConcurrentHashMap<LivingEntity, FrozenData> frozenData = new ConcurrentHashMap<>();

    public static void setFrozenTicks(LivingEntity entity, Vec3 teleport, int ticks) {
        computeMap(entity, (k, v) -> {
            if (v == null) {
                return new FrozenData(teleport, new Mutable<>(ticks));
            } else {
                v.ticks.value = ticks;
                if (v.ticks.value <= 0) {
                    unfreeze(entity);
                }
                return v;
            }
        });
    }

    public static void setFrozenTicks(LivingEntity entity, int ticks) {
        setFrozenTicks(entity, null, ticks);
    }

    public static void addFrozenTicks(LivingEntity entity, Vec3 teleport, int ticks) {
        computeMap(entity, (k, v) -> {
            if (v == null) {
                return new FrozenData(teleport, new Mutable<>(ticks));
            } else {
                v.ticks.value += ticks;
                if (v.ticks.value <= 0) {
                    unfreeze(entity);
                }
                return v;
            }
        });
    }

    public static void addFrozenTicks(LivingEntity entity, boolean teleport, int ticks) {
        addFrozenTicks(entity, teleport ? entity.position() : null, ticks);
    }

    public static void addFrozenTicks(LivingEntity entity, int ticks) {
        addFrozenTicks(entity, false, ticks);
    }

    public static void unfreeze(LivingEntity entity) {
        frozenData.remove(entity);
        enableMovement(entity, true);
    }

    public static void enableMovement(LivingEntity entity, boolean canMove) {
        if (entity instanceof ServerPlayer player) {
            RefractionMessages.sendToPlayer(new EnablePlayerMovementS2CPacket(canMove), player);
        } else if (entity instanceof ILivingEntity) {
            ((ILivingEntity) entity).refractionAPI_MC$enableMovement(canMove);
        }
    }

    private static void computeMap(LivingEntity entity, BiFunction<LivingEntity, FrozenData, FrozenData> remappingFunction) {
        frozenData.compute(entity, remappingFunction);
        enableMovement(entity, frozenData.get(entity) == null || frozenData.get(entity).ticks.value <= 0);
    }

    private record FrozenData(Vec3 teleportPos, Mutable<Integer> ticks) {
    }

    public static void init() {
        RefractionEvents.SERVER_TICK.register((post) -> {
            if (post) return;
            frozenData.forEach((entity, data) -> {
                if (data.ticks.value > 0) {
                    if (data.teleportPos != null) {
                        entity.teleportTo(data.teleportPos.x(), data.teleportPos.y(), data.teleportPos.z());
                    }
                    data.ticks.value--;
                    if (data.ticks.value <= 0) {
                        entity.hurtMarked = true;
                        unfreeze(entity);
                    }
                }
            });
        });
    }
}
