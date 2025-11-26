package net.refractionapi.refraction.mixin;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.mixininterfaces.IStructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements IStructureTemplate {

    @Shadow @Final private List<StructureTemplate.Palette> palettes;

    @Override
    public List<StructureTemplate.Palette> palettes() {
        return palettes;
    }
}
