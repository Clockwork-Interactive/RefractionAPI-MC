package net.refractionapi.refraction.mixininterfaces;

import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;

import java.util.List;
import java.util.Set;

public interface IPath {
    void debug(Node[] pOpenSet, Node[] pClosedSet, Set<Target> pTargetNodes);

    List<Node> getNodes();
}
