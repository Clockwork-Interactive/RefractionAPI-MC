package net.refractionapi.refraction.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.extensions.IForgeLivingEntity;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity, IForgeLivingEntity {

    @Unique
    private boolean refractionAPI_MC$canMove = true;

    @Override
    public void refractionAPI_MC$enableMovement(boolean canMove) {
        if (!canMove) {
            this.self().setDeltaMovement(0, 0, 0);
        }
        this.refractionAPI_MC$canMove = canMove;
    }

    @Override
    public boolean refractionAPI_MC$canMove() {
        return refractionAPI_MC$canMove;
    }



}
