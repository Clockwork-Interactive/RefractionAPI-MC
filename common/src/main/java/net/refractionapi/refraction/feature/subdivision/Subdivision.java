package net.refractionapi.refraction.feature.subdivision;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.helper.registry.RBlocks;
import net.refractionapi.refraction.mixininterfaces.IStructureTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Heavy WIP
 * Do not use yet
 *
 * @author Zeus
 */
public class Subdivision {
    @Getter
    private static Subdivision instance;
    private final MinecraftServer server;
    private final ResourceManager manager;
    private final SubdivisionGenerator generator;
    private final HolderGetter<Block> holderGetter;
    // maybe remove cache in the future to save memory --Zeus
    private final HashMap<ResourceLocation, StructCache> cache = new HashMap<>();

    public Subdivision(MinecraftServer server) {
        this.server = server;
        this.manager = server.getResourceManager();
        this.generator = new SubdivisionGenerator(server);
        this.holderGetter = this.server.registries()
                .compositeAccess()
                .registryOrThrow(Registries.BLOCK)
                .asLookup()
                .filterFeatures(this.server.getWorldData().enabledFeatures());
        prepareStructures();
    }

    private void prepareStructures() {
        Map<ResourceLocation, Resource> resources = manager.listResources("subdivision", path -> path.getPath().endsWith(".nbt"));
        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            try (InputStream stream = entry.getValue().open()) {
                parseStruct(entry.getKey(), stream);
            } catch (IOException e) {
                Refraction.LOGGER.error("Couldn't load structure: ", e);
            }
        }
    }

    private void parseStruct(ResourceLocation location, InputStream stream) throws IOException {
        var tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        var structureTemplate = new StructureTemplate();
        var version = NbtUtils.getDataVersion(tag, 500);
        structureTemplate.load(
                this.holderGetter,
                DataFixTypes.STRUCTURE.updateToCurrentVersion(this.server.getFixerUpper(), tag, version)
        );
        var str = (IStructureTemplate) structureTemplate;
        var palettes = str.palettes();
        var firstPalette = palettes.getFirst();
        var doorBlock = RBlocks.DOORWAY.get();
        // find door positions --Zeus
        var doorPositions = new HashSet<BlockPos>();
        for (var block : firstPalette.blocks()) {
            boolean isDoor = block.state().is(doorBlock);
            if (!isDoor) continue;
            doorPositions.add(block.pos());
        }
        var doorBuilders = new HashSet<SubdivisionPiece.DoorBuilder>();
        for (var doorPosition : doorPositions) {
            var foundBuilder = doorBuilders.stream().filter((b) -> isNextTo(b.positions, doorPosition)).findFirst();
            var builder = foundBuilder.isEmpty() ? new SubdivisionPiece.DoorBuilder() : foundBuilder.get();
            builder.positions.add(doorPosition);
            doorBuilders.add(builder);
        }
        var doors = new HashSet<SubdivisionPiece.Door>();
        doorBuilders.forEach(builder -> doors.add(builder.create(structureTemplate.getSize())));
        var parts = location.getPath().split("/");
        location = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), parts[parts.length - 1].replace(".nbt", ""));
        cache.put(location, new StructCache(
                structureTemplate,
                firstPalette,
                doors
        ));
    }

    public boolean isNextTo(Set<BlockPos> posSet, BlockPos pos) {
        return posSet.stream().anyMatch(otherPos -> {
            var xDiff = Math.abs(otherPos.getX() - pos.getX());
            var yDiff = Math.abs(otherPos.getY() - pos.getY());
            var zDiff = Math.abs(otherPos.getZ() - pos.getZ());
            return xDiff + yDiff + zDiff == 1;
        });
    }

    // calc the direction of the position from structSize center --Zeus
    public static Direction getDirection(Vec3i structSize, BlockPos pos) {
        var centerX = structSize.getX() / 2F;
        var centerY = structSize.getY() / 2F;
        var centerZ = structSize.getZ() / 2F;
        var posX = pos.getX() + 0.5F;
        var posY = pos.getY() + 0.5F;
        var posZ = pos.getZ() + 0.5F;
        var deltaX = posX - centerX;
        var deltaY = posY - centerY;
        var deltaZ = posZ - centerZ;
        var absDeltaX = Math.abs(deltaX);
        var absDeltaY = Math.abs(deltaY);
        var absDeltaZ = Math.abs(deltaZ);
        // TODO up / down --Zeus
        if (absDeltaX > absDeltaY && absDeltaX > absDeltaZ) return deltaX > 0 ? Direction.EAST : Direction.WEST;
        if (absDeltaZ > absDeltaY) return deltaZ > 0 ? Direction.SOUTH : Direction.NORTH;
        return deltaY > 0 ? Direction.UP : Direction.DOWN;
    }

    public Set<ResourceLocation> getStructIDs() {
        return cache.keySet();
    }

    public Set<String> getStructIDStrings() {
        return cache.keySet().stream().map(ResourceLocation::toString).collect(Collectors.toSet());
    }

    public boolean pieceExists(ResourceLocation id) {
        return cache.containsKey(id);
    }

    public StructCache getPiece(ResourceLocation id) {
        return cache.get(id);
    }

    public static void generate(SubdivisionSet set, ServerLevel serverLevel, BlockPos pos) {
        getInstance().generator.generate(set, serverLevel, pos);
    }

    public static void placePiece(SubdivisionPiece.Configurer configurer, ServerLevel serverLevel, BlockPos pos, int rotation) {
        getInstance().generator.generateSingle(serverLevel, pos, null, configurer, rotation);
    }

    public static void init() {
        RefractionEvents.SERVER_STARTED.register((server) -> {
            if (instance == null) instance = new Subdivision(server);
        });
        RefractionEvents.SERVER_STOPPING.register(() -> instance = null);
    }

    public record StructCache(
            StructureTemplate template,
            StructureTemplate.Palette palette,
            Set<SubdivisionPiece.Door> doors
    ) {
    }
}
