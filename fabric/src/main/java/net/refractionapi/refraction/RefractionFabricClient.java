package net.refractionapi.refraction;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.InteractionResult;
import net.refractionapi.refraction.client.ClientData;
import net.refractionapi.refraction.events.event.RefractionClientEvents;
import net.refractionapi.refraction.feature.cutscenes.client.CinematicBars;
import net.refractionapi.refraction.feature.quest.client.QuestRenderer;

public class RefractionFabricClient implements ClientModInitializer, ScreenEvents.Remove {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(CinematicBars::bars);
        HudRenderCallback.EVENT.register(QuestRenderer::quest);
        ClientTickEvents.START_CLIENT_TICK.register((minecraft) -> RefractionClientEvents.clientTick(false));
        ClientTickEvents.END_CLIENT_TICK.register((minecraft) -> RefractionClientEvents.clientTick(true));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> RefractionClientEvents.loggedOut());
        AttackEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> InteractionResult.sidedSuccess(RefractionClientEvents.onAttack()));
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> ScreenEvents.remove(screen).register(this));
        ClientData.load();
    }

    @Override
    public void onRemove(Screen screen) {
        RefractionClientEvents.onRemove(screen);
    }

}
