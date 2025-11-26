package net.refractionapi.refraction.feature.subdivision;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.helper.registry.RBlocks;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;
import net.refractionapi.refraction.mixininterfaces.IStructureTemplate;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Heavy WIP
 * Do not use yet
 * @author Zeus
 */
public class Subdivision {
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
        CompoundTag tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        StructureTemplate structureTemplate = new StructureTemplate();
        int version = NbtUtils.getDataVersion(tag, 500);
        structureTemplate.load(
                this.holderGetter,
                DataFixTypes.STRUCTURE.updateToCurrentVersion(this.server.getFixerUpper(), tag, version)
        );
        IStructureTemplate str = (IStructureTemplate) structureTemplate;
        List<StructureTemplate.Palette> palettes = str.palettes();
        // we only get the first pallet cause Subdivision has its own mechanism --Zeus
        StructureTemplate.Palette firstPalette = palettes.getFirst();
        Block matching = RBlocks.DOORWAY.get();
        Set<SubdivisionPiece.Door> doors = new HashSet<>();
        Int2ObjectArrayMap<List<BlockPos>> doorPositions = new Int2ObjectArrayMap<>();
        Vec3i center = structureTemplate.getSize();
        // find door positions --Zeus
        for (StructureTemplate.StructureBlockInfo block : firstPalette.blocks()) {
            boolean isDoor = block.state().is(matching);
            if (!isDoor) continue;
            BlockPos pos = block.pos();
            boolean added = false;
            for (Map.Entry<Integer, List<BlockPos>> entry : doorPositions.entrySet()) {
                for (BlockPos pos1 : entry.getValue()) {
                    if (Math.abs(pos1.getX() - pos.getX()) <= 1 &&
                            Math.abs(pos1.getY() - pos.getY()) <= 1 &&
                            Math.abs(pos1.getZ() - pos.getZ()) <= 1) {
                        entry.getValue().add(pos);
                        added = true;
                        break;
                    }
                }
                if (added) break;
            }
            if (!added) {
                int index = doorPositions.size();
                List<BlockPos> positions = new ArrayList<>();
                positions.add(pos);
                doorPositions.put(index, positions);
            }
        }
        // group door positions into doors + their width --Zeus
        doorPositions.forEach((index, positions) -> {
            int minX = positions.stream().mapToInt(BlockPos::getX).min().orElse(0);
            int maxX = positions.stream().mapToInt(BlockPos::getX).max().orElse(0);
            int minY = positions.stream().mapToInt(BlockPos::getY).min().orElse(0);
            int maxY = positions.stream().mapToInt(BlockPos::getY).max().orElse(0);
            int minZ = positions.stream().mapToInt(BlockPos::getZ).min().orElse(0);
            int maxZ = positions.stream().mapToInt(BlockPos::getZ).max().orElse(0);

            int width = Math.max(maxX - minX, maxZ - minZ) + 1;
            int height = maxY - minY + 1;

            BlockPos doorCenter = new BlockPos(
                    (minX + maxX) / 2,
                    minY,
                    (minZ + maxZ) / 2
            );

            Direction direction = Vec3Helper.getDirection(doorCenter.getCenter(), doorCenter.rotate(Rotation.CLOCKWISE_180).getCenter());

            doors.add(new SubdivisionPiece.Door(
                    new int[]{width, height},
                    doorCenter,
                    direction
            ));
        });

        String[] parts = location.getPath().split("/");
        location = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), parts[parts.length - 1].replace(".nbt", ""));
        cache.put(location, new StructCache(
                structureTemplate,
                palettes.getFirst(),
                doors
        ));
    }

    public boolean exists(ResourceLocation id) {
        return cache.containsKey(id);
    }

    public StructCache get(ResourceLocation id) {
        return cache.get(id);
    }

    public static void generate(SubdivisionSet set, ServerLevel serverLevel, BlockPos pos) {
        getInstance().generator.generate(set, serverLevel, pos);
    }

    public static void placePiece(SubdivisionPiece.Configurer configurer, ServerLevel serverLevel, BlockPos pos, int rotation) {
        getInstance().generator.place(serverLevel, pos, configurer, rotation);
    }

    public static Subdivision getInstance() {
        return instance;
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
