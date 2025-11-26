package net.refractionapi.refraction.mixininterfaces;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;

public interface IStructureTemplate {
    List<StructureTemplate.Palette> palettes();
}
