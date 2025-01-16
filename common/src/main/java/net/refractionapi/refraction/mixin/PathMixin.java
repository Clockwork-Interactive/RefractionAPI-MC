package net.refractionapi.refraction.mixin;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import net.refractionapi.refraction.mixininterfaces.IPath;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Set;

@Mixin(Path.class)
public class PathMixin implements IPath {

    @Override
    public void debug(Node[] pOpenSet, Node[] pClosedSet, Set<Target> pTargetNodes) {
    }

    @Override
    public List<Node> getNodes() {
        return List.of();
    }

}
