package net.refractionapi.refraction.feature.algorithm;

import jdk.jfr.Experimental;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Not close to being finished --Zeus <br>
 * Made this for a small thing, most likely will remove in the future and place it in a separate mod.
 */
@Experimental
@ApiStatus.Internal
public class MazeGenerator {

    private final int gridSize = 5;
    private int mazeSize;
    private int[][] maze;
    private int wallHeight;
    private int centerSize;
    private int wallThickness;
    private BlockPos start;
    private Direction startDirection;
    private BlockState wallBlock;
    private BlockState wallOuterBlock;
    private BlockState floorBlock;
    private BlockState ceilingBlock;
    private Level level;

    private int[][][] tiles = new int[][][]{
            // Tile 0: Empty space
            {
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            // Tile 1: Vertical wall
            {
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0}
            },
            // Tile 1: Horizontal wall
            {
                    {0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            // Tile 3: Corner (top-left)
            {
                    {1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 0},
                    {1, 1, 1, 0, 0},
                    {1, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0}
            },
            // Tile 4: Corner (top-right)
            {
                    {0, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1},
                    {0, 0, 1, 1, 1},
                    {0, 0, 0, 1, 1},
                    {0, 0, 0, 0, 0}
            },
            // Tile 5: Corner (bottom-left)
            {
                    {0, 0, 0, 0, 0},
                    {1, 1, 0, 0, 0},
                    {1, 1, 1, 0, 0},
                    {1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 0}
            },
            // Tile 6: Corner (bottom-right)
            {
                    {0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 1},
                    {0, 0, 1, 1, 1},
                    {0, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1}
            },
            // Tile 7: T-junction (top)
            {
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0}
            },
            // Tile 8: T-junction (right)
            {
                    {0, 0, 0, 1, 1},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {0, 0, 0, 1, 1}
            },
            // Tile 9: T-junction (bottom)
            {
                    {0, 0, 0, 0, 0},
                    {0, 1, 1, 1, 0},
                    {0, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1}
            },
            // Tile 10: T-junction (left)
            {
                    {1, 1, 0, 0, 0},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1},
                    {1, 1, 0, 0, 0}
            },
            // Tile 11: Cross
            {
                    {1, 0, 0, 0, 1},
                    {0, 0, 1, 0, 0},
                    {0, 1, 1, 1, 0},
                    {0, 0, 1, 0, 0},
                    {1, 0, 0, 0, 1}
            }
    };

    public MazeGenerator(Level level) {
        if (level.isClientSide) {
            throw new IllegalArgumentException("Cannot generate maze on the client side");
        }
        this.level = level;
    }

    public MazeGenerator setMazeSize(int mazeSize) {
        this.mazeSize = mazeSize;
        return this;
    }

    public MazeGenerator setWallHeight(int wallHeight) {
        this.wallHeight = wallHeight;
        return this;
    }

    public MazeGenerator setWallThickness(int wallThickness) {
        this.wallThickness = wallThickness;
        return this;
    }

    public MazeGenerator setCenterSize(int centerSize) {
        this.centerSize = centerSize;
        return this;
    }

    public MazeGenerator setStart(BlockPos start, Direction startDirection) {
        this.start = start;
        this.startDirection = startDirection;
        return this;
    }

    public MazeGenerator setWallBlock(BlockState wallBlock) {
        this.wallBlock = wallBlock;
        return this;
    }

    public MazeGenerator setWallOuterBlock(BlockState wallOuterBlock) {
        this.wallOuterBlock = wallOuterBlock;
        return this;
    }

    public MazeGenerator setFloorBlock(BlockState floorBlock) {
        this.floorBlock = floorBlock;
        return this;
    }

    public MazeGenerator setCeilingBlock(BlockState ceilingBlock) {
        this.ceilingBlock = ceilingBlock;
        return this;
    }

    public void generate() {
        generateMaze();
        fillMaze();
    }

    private void generateMaze() {
        this.maze = new int[mazeSize][mazeSize];
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                this.maze[i][j] = -1;
            }
        }

        waveFunctionCollapse();
    }

    private void waveFunctionCollapse() {
        List<int[]> positions = new ArrayList<>();
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                positions.add(new int[]{i, j});
            }
        }

        RandomSource random = this.level.random;
        while (!positions.isEmpty()) {
            int[] pos = positions.remove(random.nextInt(positions.size()));
            int x = pos[0];
            int y = pos[1];

            List<Integer> possibleTiles = getPossibleTiles(x, y);

            if (!possibleTiles.isEmpty()) {
                this.maze[x][y] = possibleTiles.get(random.nextInt(possibleTiles.size()));
            } else {
                this.maze[x][y] = 0;
            }
        }
    }

    private List<Integer> getPossibleTiles(int x, int y) {
        List<Integer> possibleTiles = new ArrayList<>();
        for (int tile = 0; tile < tiles.length; tile++) {
            if (isValidTile(x, y, tile)) {
                possibleTiles.add(tile);
            }
        }
        return possibleTiles;
    }

    private boolean isValidTile(int x, int y, int tile) {
        return true;
    }

    private void fillMaze() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                int tile = this.maze[i][j];
                if (tile >= 0 && tile < tiles.length) {
                    fillTile(i, j, tiles[tile]);
                }
            }
        }
    }

    private void fillTile(int x, int y, int[][] tile) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                int blockX = x * gridSize + i;
                int blockZ = y * gridSize + j;
                int blockY = start.getY();

                if (tile[i][j] == 1) {
                    for (int h = 0; h < wallHeight; h++) {
                        this.level.setBlockAndUpdate(new BlockPos(blockX, blockY, blockZ), wallBlock);
                    }
                } else {
                    for (int h = 0; h < wallHeight; h++) {
                        this.level.setBlockAndUpdate(new BlockPos(blockX, blockY + h, blockZ), wallBlock);
                    }
                }
            }
        }
    }

}
