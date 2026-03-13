package net.refractionapi.refraction.feature.subdivision;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.refractionapi.refraction.helper.randomizer.WeightedRandom;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public class SubdivisionSet {
    private final WeightedRandom<SubdivisionPiece.Configurer> pieces = new WeightedRandom<>();
    private final List<SubdivisionPiece.Configurer> origin = new ArrayList<>();
    protected int maxDepth = 5;

    public SubdivisionSet() {

    }

    public SubdivisionPiece.Configurer configurer(ResourceLocation id) {
        // could be better lol --Zeus
        var origin = this.origin.stream().filter(config -> config.id.equals(id)).findFirst();
        return origin.orElseGet(() -> pieces.getPercentages().entrySet().stream().filter(config -> config.getKey().id.equals(id)).findFirst()
                .map(Map.Entry::getKey).orElse(new SubdivisionPiece.Configurer(id)));
    }

    public SubdivisionSet maxDepth(int depth) {
        this.maxDepth = depth;
        return this;
    }

    public SubdivisionSet addOrigin(ResourceLocation id, Consumer<SubdivisionPiece.Configurer> consumer) {
        var config = configurer(id);
        consumer.accept(config);
        this.origin.add(config);
        return this;
    }

    public WeightedRandom<BlockState> createBlockSet(String id, Consumer<WeightedRandom<BlockState>> consumer) {
        var random = new WeightedRandom<BlockState>();
        consumer.accept(random);
        return random;
    }

    public SubdivisionSet add(ResourceLocation id, Consumer<SubdivisionPiece.Configurer> consumer) {
        var config = configurer(id);
        consumer.accept(config);
        return this.add(config);
    }

    public SubdivisionSet add(SubdivisionPiece.Configurer config) {
        this.pieces.add(config, config.weight);
        return this;
    }

    public WeightedRandom<SubdivisionPiece.Configurer> pieces() {
        return pieces;
    }

    public SubdivisionPiece.Configurer origin() {
        return origin.isEmpty() ? pieces.get() : origin.get(new Random().nextInt(origin.size()));
    }
}
