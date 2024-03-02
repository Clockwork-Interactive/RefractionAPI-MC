package net.refractionapi.refraction.cutscenes;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

public class CutsceneTickEvent extends TickEvent {

    private final OLDCutscene cutscene;

    public CutsceneTickEvent(Phase phase, OLDCutscene cutscene) {
        super(Type.SERVER, LogicalSide.SERVER, phase);
        this.cutscene = cutscene;
    }

    public OLDCutscene getCutscene() {
        return cutscene;
    }

    public Player getPlayer() {
        return cutscene.player;
    }

}
