package net.refractionapi.refraction.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.misc.RefractionMisc;

import java.util.HashMap;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    public static final HashMap<LivingEntity, Vec3> frozenEntities = new HashMap<>();

    @SubscribeEvent
    public static void tickEvent(LivingEvent.LivingTickEvent event) {
        frozenEntities.forEach((entity, vec3) -> {
            if (vec3 != null && !(entity instanceof Player)) {
                entity.hurtMarked = true;
                entity.teleportTo(vec3.x, vec3.y, vec3.z);
            }
        });
    }

    @SubscribeEvent
    public static void playerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;
        if (frozenEntities.containsKey(event.player)) {
            event.player.hurtMarked = true;
            Vec3 vec3 = frozenEntities.get(event.player);
            event.player.teleportTo(vec3.x, vec3.y, vec3.z);
        }
    }

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            RefractionMisc.enableMovement(player, true);
    }

    //@SubscribeEvent
    //public static void chat(ServerChatEvent event) {
    //    if (event.getMessage().getString().contains("HI")) {
    //        Cutscene.create(event.getPlayer(), true)
    //                .createPoint(40, 0)
    //                .setTarget(event.getPlayer())
    //                .addFacingRelativeVecPoint(new Vec3(5.0F, 0, 0), new Vec3(0.3F, 0, 0), EasingFunctions.LINEAR)
    //                .newPoint(50, 0)
    //                .setTarget(event.getPlayer())
    //                .addFacingRelativeVecPoint(new Vec3(0, 0, 5.0F), new Vec3(0, 0, 0.3F), EasingFunctions.LINEAR)
    //                .setFOV(20, 90, 10, EasingFunctions.LINEAR)
    //                .setZRot(0, 160, 40, EasingFunctions.LINEAR)
    //                .build();
    //        Cutscene.create(event.getPlayer(), false)
    //                .createPoint(40, 0)
    //                .setTarget(event.getPlayer())
    //                .addFacingRelativeVecPoint(new Vec3(5.0F, 0, 0), new Vec3(0.3F, 0, 0), EasingFunctions.LINEAR)
    //                .newPoint(50, 0)
    //                .setTarget(event.getPlayer())
    //                .addFacingRelativeVecPoint(new Vec3(0, 0, 5.0F), new Vec3(0, 0, 0.3F), EasingFunctions.LINEAR)
    //                .setFOV(20, 90, 10, EasingFunctions.LINEAR)
    //                .setZRot(0, 160, 40, EasingFunctions.LINEAR)
    //                .build();
    //    }
    //}

}
