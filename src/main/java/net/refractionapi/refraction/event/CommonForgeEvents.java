package net.refractionapi.refraction.event;

import net.minecraft.server.level.ServerPlayer;
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

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void deathEvent(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player)
            RefractionMisc.enableMovement(player, true);
    }

}
