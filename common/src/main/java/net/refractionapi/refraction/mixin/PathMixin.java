package net.refractionapi.refraction.mixin;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;
import net.refractionapi.refraction.mixininterfaces.IPath;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

@Mixin(Path.class)
public class PathMixin implements IPath {

    @Shadow @Nullable private Path.DebugData debugData;

    @Shadow @Final private List<Node> nodes;

    @Override
    public void debug(Node[] pOpenSet, Node[] pClosedSet, Set<Target> pTargetNodes) {
        this.debugData = new Path.DebugData(pOpenSet, pClosedSet, pTargetNodes);
    }

    @Override
    public List<Node> getNodes() {
        return this.nodes;
    }

}
