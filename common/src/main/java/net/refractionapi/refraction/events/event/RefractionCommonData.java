package net.refractionapi.refraction.events.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.data.RefractionData;
import net.refractionapi.refraction.feature.quest.Quest;
import net.refractionapi.refraction.feature.quest.points.KillPoint;
import net.refractionapi.refraction.helper.entity.FrozenManager;

import java.util.HashMap;

import static net.refractionapi.refraction.feature.quest.QuestHandler.QUESTS;

public class RefractionCommonData {
    public static final HashMap<Player, RefractionData> runtimeData = new HashMap<>();
    public static Quest quest;
    public static CompoundTag tag = new CompoundTag();

    public static void playerTick(Player player) {
        //if (player.getBlockStateOn().equals(Blocks.BIRCH_TRAPDOOR.defaultBlockState())) {
        //    player.level().setBlockAndUpdate(player.blockPosition(), Blocks.AIR.defaultBlockState());
        //    Cutscene.create(player, true)
        //            .createPoint(15, 10)
        //            .setTarget(player.getEyePosition())
        //            .addFacingRelativeVecPoint(new Vec3(2.0F, -1.0F, 1.2F), new Vec3(0.7F, 0.0F, 0.0F), EasingFunctions.LINEAR)
        //            .setFOV(75)
        //            .setZRot(-60, 0)
        //            .newPoint(100, 0)
        //            .addFacingRelativeVecPoint(player, 0, 20, new Vec3(0.7F, 0.0F, 0.0F), new Vec3(5.0F, 5.0F, 0.0F), EasingFunctions.LINEAR)
        //            .setTarget(Vec3Helper.getVec(player, -(32 / 2), 0), Vec3Helper.getVec(player, 32 / 2, 0), EasingFunctions.LINEAR)
        //            .newPoint(10, 65)
        //            .setTarget(player.getEyePosition())
        //            .addFacingRelativeVecPoint(player, new Vec3(5.0F, 0.0F, 0.0F), new Vec3(1.3F, 0.0F, 0.0F), EasingFunctions.EASE_IN_CUBIC)
        //            .setFOV(90, 120, 10, 0, EasingFunctions.LINEAR)
        //            .build();
        //}
    }

    public static void livingTick(LivingEntity living) {

    }

    public static void death(LivingEntity living, DamageSource source) {
        if (living instanceof ServerPlayer player)
            FrozenManager.enableMovement(player, true);
        if (source.getEntity() instanceof ServerPlayer player) {
            if (QUESTS.containsKey(player.getUUID()))
                QUESTS.get(player.getUUID()).getQuestPoints().forEach(point -> {
                    if (point instanceof KillPoint killPoint) killPoint.onKill(living);
                });
        }
    }
}
