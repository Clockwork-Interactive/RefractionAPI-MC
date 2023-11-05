package net.refractionapi.refraction.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.cutscenes.Cutscene;
import net.refractionapi.refraction.math.EasingFunctions;
import net.refractionapi.refraction.misc.RefractionMisc;
import net.refractionapi.refraction.vec3.Vec3Helper;

import static net.refractionapi.refraction.vec3.Vec3Helper.calculateViewVector;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            RefractionMisc.enableMovement(player, true);
        }
    }

    //@SubscribeEvent
    //public static void chat(ServerChatEvent event) {
    //    if (event.getMessage().getString().equals("HI")) {
    //        Player player = event.getPlayer();
    //        Cutscene.createFacingRelativeCutscene(player, player.getEyePosition(), new int[]{100, 50}, true, EasingFunctions.EASE_IN_OUT_CUBIC, new Vec3(10, 0, 0), new Vec3(0.5F, 0, 0));
    //    }
    //}

}
