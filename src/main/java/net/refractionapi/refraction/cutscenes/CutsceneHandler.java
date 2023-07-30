package net.refractionapi.refraction.cutscenes;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.refractionapi.refraction.Refraction;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Mod.EventBusSubscriber(modid = Refraction.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CutsceneHandler {
    public static final List<Cutscene> ACTIVE_CUTSCENES = new ArrayList<>();

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.END)) return;

        ListIterator<Cutscene> iterator = ACTIVE_CUTSCENES.listIterator();
        while (iterator.hasNext()) {
            Cutscene cutscene = iterator.next();
            cutscene.tick();
            if (cutscene.stopped) {
                iterator.remove();
            }

        }
    }

}
