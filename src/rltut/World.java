package rltut;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class World {
    private Tile[][][] tiles;
    private List<Creature> creatures;

    private int width;
    public int width() { return width; }

    private int height;
    public int height() { return height; }

    private int depth;
    public int depth() { return depth; }

    public World(Tile[][][] tiles){
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.creatures = new ArrayList<Creature>();
    }

    public Tile tile(int x, int y, int z){
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
            return Tile.BOUNDS;
        else
            return tiles[x][y][z];
    }

    public Creature creature(int x, int y, int z){
        for (Creature c : creatures){
            if (c.x == x && c.y == y && c.z == z)
                return c;
        }
        return null;
    }

    public char glyph(int x, int y, int z){
        return tile(x, y, z).glyph();
    }


    public Color color(int x, int y, int z){
        return tile(x, y, z).color();
    }

    public void dig(int x, int y, int z) {
        if (tile(x, y, z).isDiggable())
            tiles[x][y][z] = Tile.FLOOR;
    }

    public void remove(Creature other) {
        creatures.remove(other);
    }

    public void update(){
        List<Creature> toUpdate = new ArrayList<Creature>(creatures);
        for (Creature creature : toUpdate){
            creature.update();
        }
    }

    public void addAtEmptyLocation(Creature creature, int z){
        int x;
        int y;

        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        }
        while (!tile(x,y,z).isGround() || creature(x,y,z) != null);

        creature.x = x;
        creature.y = y;
        creature.z = z;
        creatures.add(creature);
    }

    public void printWorld(){
        System.out.println(width + ", " + height + ", " + depth);

        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                for(int k = 0; k < depth; k++){
                    if(tiles[i][j][k] == Tile.FLOOR){
                        System.out.print("F");
                    } else if (tiles[i][j][k] == Tile.WALL) {
                        System.out.print("W");
                    } else if (tiles[i][j][k] == Tile.BOUNDS) {
                        System.out.print("B");
                    } else if (tiles[i][j][k] == Tile.STAIRS_DOWN) {
                        System.out.print(">");
                    }
                    else if (tiles[i][j][k] == Tile.STAIRS_UP) {
                        System.out.print("<");
                    }
                }
                //System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }
}