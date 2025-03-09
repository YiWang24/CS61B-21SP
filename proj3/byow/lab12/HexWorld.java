package byow.lab12;

import org.junit.Test;

import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    private static class Position {
        int x;
        int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(x + dx, y + dy);
        }
    }


    public static void fillBoardWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        switch (tileNum) {
            case 0:
                return Tileset.WALL;
            case 1:
                return Tileset.FLOWER;
            case 2:
                return Tileset.GRASS;
            default:
                return Tileset.TREE;
        }
    }

    public static Position getTopRightNeighbor(Position p, int size) {
        return p.shift(2 * size - 1, size);
    }

    public static Position getBottomRightNeighbor(Position p, int size) {
        return p.shift(2 * size - 1, -size);
    }

    public static Position getBottomNeighbor(Position p, int size) {
        return p.shift(0, -2 * size);
    }


    public static void addHexagon(TETile[][] tiles, Position p, TETile tile, int size) {
        if (size < 2) {
            return;
        }
        addHexagonHelper(tiles, p, size - 1, size, tile);
    }

    public static void addHexColumn(TETile[][] tiles, Position p, int size, int n) {
        if (n < 1) {
            return;
        }
        addHexagon(tiles, p, randomTile(), size);
        if (n > 1) {
            Position bottomNeighbor = getBottomNeighbor(p, size);
            System.out.println(bottomNeighbor.y);
            addHexColumn(tiles, bottomNeighbor, size, n - 1);
        }
    }


    private static void addHexagonHelper(TETile[][] tiles, Position p, int blank, int length, TETile tileType) {
        drawRow(tiles, p.shift(blank, 0), tileType, length);
        if (blank > 0) {
            addHexagonHelper(tiles, p.shift(0, -1), blank - 1, length + 2, tileType);
        }
        drawRow(tiles, p.shift(blank, -2 * blank - 1), tileType, length);
    }

    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    public static void drawWorld(TETile[][] tiles, Position p, int hexSize, int tileSize) {

        addHexColumn(tiles, p, hexSize, tileSize);

        for (int i = 1; i < tileSize; i++) {
            p = getTopRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tileSize + i);
        }
        for (int i = tileSize - 2; i >= 0; i--) {
            p = getBottomRightNeighbor(p, hexSize);
            addHexColumn(tiles, p, hexSize, tileSize + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        fillBoardWithNothing(world);
        Position p = new Position(20, 50);
        drawWorld(world, p, 3, 3);
        ter.renderFrame(world);
    }

}
