package net.refractionapi.refraction.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.examples.interaction.ExampleInteractionRegistry;
import net.refractionapi.refraction.quest.QuestHandler;
import net.refractionapi.refraction.quest.points.InteractionPoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("RETURN"), method = "interact")
    public void interact(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        ExampleInteractionRegistry.EXAMPLE_INTERACTION.sendInteraction(pPlayer, pPlayer);
        if (cir.getReturnValue() != InteractionResult.FAIL) {
            if (QuestHandler.QUESTS.containsKey(pPlayer.getUUID())) {
                QuestHandler.QUESTS.get(pPlayer.getUUID()).getQuestPoints().forEach(questPoint -> {
                    if (questPoint instanceof InteractionPoint point) {
                        point.onInteract((Entity) (Object) this);
                    }
                });
            }
        }
    }

}
