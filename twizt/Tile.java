
package twizt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jacob
 */
public class Tile
{
    
    public int distance = 0;
    
    private final Tile[]SURROUNDINGS;
    
    private byte TYPE;
    
    public final static float RADIUS = 0.5f;
    public final static float DIAMETER = RADIUS*2f;
    
    public static final int NOOFDIRECTIONS = Direction.values().length;

    public static class TileData
    {
        public final boolean[][] ISSOLID;
        public final boolean SEETHROUGH;
        public final boolean HASTEXTURE;
        public final boolean TRIGGERSTHENOTE;
        public final boolean TRIGGERSTHERESPAWN;
        public final Color COLOR;
        
        public BufferedImage texture;
        public BufferedImage floorTexture;
        public BufferedImage ceilingTexture;
        
        private BufferedImage[] slice;
        
        public TileData(boolean[][] isSolid, boolean seeThrough, boolean hasTexture, boolean triggersTheNote, boolean triggersTheRespawn, String textureName, String floorTextureName, String ceilingTextureName)
        {
            this.ISSOLID = isSolid;
            this.SEETHROUGH = seeThrough;
            this.HASTEXTURE = hasTexture;
            this.TRIGGERSTHENOTE = triggersTheNote;
            this.TRIGGERSTHERESPAWN = triggersTheRespawn;
            this.COLOR = null;
            try
            {
                this.texture = ImageIO.read(this.getClass().getResource(textureName));
                slice = new BufferedImage[texture.getWidth()];
                this.floorTexture = ImageIO.read(this.getClass().getResource(floorTextureName));
                this.ceilingTexture = ImageIO.read(this.getClass().getResource(ceilingTextureName));
                for (int i = 0; i < slice.length; i++)
                {
                    slice[i] = texture.getSubimage(i, 0, 1, texture.getHeight());
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
            }
            GETSOLIDSCALEX = (float)ISSOLID.length / (float)Tile.DIAMETER;
            GETSOLIDSCALEY = (float)ISSOLID[0].length / (float)Tile.DIAMETER;
            TEXTUREWIDTHSCALEX = (float)texture.getWidth() / (float)Tile.DIAMETER;
            TEXTUREHEIGHTSCALEY = (float)texture.getWidth() / (float)Tile.DIAMETER;
            FLOORTEXTUREWIDTHSCALEX = (float)floorTexture.getWidth() / (float)Tile.DIAMETER;
            FLOORTEXTUREHEIGHTSCALEY = (float)floorTexture.getHeight() / (float)Tile.DIAMETER;
            CEILINGTEXTUREWIDTHSCALEX = (float)ceilingTexture.getWidth() / (float)Tile.DIAMETER;
            CEILINGTEXTUREHEIGHTSCALEY = (float)ceilingTexture.getHeight() / (float)Tile.DIAMETER;
        }
        private final float TILERADIUSFLOAT = (float)Tile.RADIUS;
        private final float GETSOLIDSCALEX;
        private final float GETSOLIDSCALEY;
        public boolean getSolid(float x, float y)
        {
            int xPos = (int)((x + TILERADIUSFLOAT) * GETSOLIDSCALEX);
            int yPos = (int)((y + TILERADIUSFLOAT) * GETSOLIDSCALEY);
            if (xPos >= ISSOLID.length)
            {
                xPos = ISSOLID.length-1;
            }
            if (yPos >= ISSOLID[0].length)
            {
                yPos = ISSOLID[0].length-1;
            }
            return ISSOLID[xPos][yPos];
        }
        final float TEXTUREWIDTHSCALEX;
        final float TEXTUREHEIGHTSCALEY;
        public BufferedImage getLine(float x, float y)
        {
            int xPos = (int)((x + TILERADIUSFLOAT) * TEXTUREWIDTHSCALEX);
            int yPos = (int)((y + TILERADIUSFLOAT) * TEXTUREHEIGHTSCALEY);
            

            return slice[(yPos+xPos)%slice.length];

            
        }
        final float FLOORTEXTUREWIDTHSCALEX;
        final float FLOORTEXTUREHEIGHTSCALEY;
        public int getFloorPixel(float x, float y)
        {
            int xPos = (int)((x + TILERADIUSFLOAT) * FLOORTEXTUREWIDTHSCALEX);
            int yPos = (int)((y + TILERADIUSFLOAT) * FLOORTEXTUREHEIGHTSCALEY);
            if (xPos >= floorTexture.getWidth())
            {
                xPos = floorTexture.getWidth()-1;
            }
            if (yPos >= floorTexture.getHeight())
            {
                yPos = floorTexture.getHeight()-1;
            }
            return floorTexture.getRGB(xPos, yPos);
        }
        final float CEILINGTEXTUREWIDTHSCALEX;
        final float CEILINGTEXTUREHEIGHTSCALEY;
        public int getCeilingPixel(float x, float y)
        {
            int xPos = (int)((x + TILERADIUSFLOAT) * CEILINGTEXTUREWIDTHSCALEX);
            int yPos = (int)((y + TILERADIUSFLOAT) * CEILINGTEXTUREHEIGHTSCALEY);
            if (xPos >= ceilingTexture.getWidth())
            {
                xPos = ceilingTexture.getWidth()-1;
            }
            if (yPos >= ceilingTexture.getHeight())
            {
                yPos = ceilingTexture.getHeight()-1;
            }
            return ceilingTexture.getRGB(xPos, yPos);
        }
    }
    
    
    
    public void update(byte TYPE)
    {
        this.TYPE = TYPE;
    }
    
    public static TileData getData(byte ID)
    {
        return TILEDATA[ID];
    }
    
    public TileData getData()
    {
        return getData(this.TYPE);
    }

    private static final TileData[] TILEDATA;
    
    static
    {
        TILEDATA = new TileData[]
        {
            new TileData(new boolean[][]{{false}}, true, false, false, false, "Air.png", "Floor.png", "Ceiling.png"),
            new TileData(new boolean[][]{{true}}, false, true, false, false, "Wall.png", "Air.png", "Air.png"), //1
            new TileData(new boolean[][]{{true}}, false, true, false, false, "WallCracked.png", "Air.png", "Air.png"), //2
            new TileData(new boolean[][]{{true}}, false, true, false, false, "WallHelpUs.png", "Air.png", "Air.png"), //3
            new TileData(new boolean[][]{{true}}, false, true, false, false, "WallNoEscape.png", "Air.png", "Air.png"), //4
            new TileData(new boolean[][]{{true}}, false, true, false, false, "WallArrow.png", "Air.png", "Air.png"), //5
            new TileData(new boolean[][]{{true}}, false, true, false, false, "WallArrow2.png", "Air.png", "Air.png"), //6
            new TileData(new boolean[][]{{true}}, false, true, false, false, "TheTruth.png", "Air.png", "Air.png"), //7
            new TileData(new boolean[][]{{true}}, false, true, false, false, "Map.png", "Air.png", "Air.png"), //8
            new TileData(new boolean[][]{{true}}, false, true, false, false, "Arrow3.png", "Air.png", "Air.png"), //9
            new TileData(new boolean[][]{{true}}, false, true, false, false, "Warning.png", "Air.png", "Air.png"), //10
            new TileData(new boolean[][]{{true}}, false, true, true, false, "WallMonster.png", "Air.png", "Air.png"), //11
            new TileData(new boolean[][]{{true}}, false, true, false, false, "TheDoor.png", "Air.png", "Air.png"), //12
            new TileData(new boolean[][]{{true}}, false, true, false, true, "Bricks.png", "Air.png", "Air.png"), //13
            new TileData(new boolean[][]{{false}}, true, false, false, false, "Air.png", "Ceiling.png", "Ceiling.png"), //14
            new TileData(new boolean[][]{{false}}, true, false, false, false, "Air.png", "SomethingOnTheFloor.png", "Ceiling.png"), //15
            new TileData(new boolean[][]{{false, false, false},{false, true, false},{false, false, false}}, false, true, false, false, "Wall.png", "Floor.png", "Ceiling.png"), //16
            new TileData(new boolean[][]{{false, false, false},{false, true, true},{false, true, false}}, false, true, false, false, "Wall.png", "Floor.png", "Ceiling.png"), //17
            new TileData(new boolean[][]{{false, true, false},{false, true, false},{false, true, false}}, false, true, false, false, "Wall.png", "Floor.png", "Ceiling.png"), //18
            new TileData(new boolean[][]{{true, false},{false, true}}, false, true, false, false, "Crate.png", "Floor.png", "Ceiling.png"), //19
            
        };
    }
    
    
    public enum Direction
    {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    }
    
    public Tile nextTile(Direction dir)
    {
        return SURROUNDINGS[dir.ordinal()];
    }
    
    public int getRotationDifference(Direction dir)
    {
        Tile nextTile = this.nextTile(dir);
        if (nextTile == null)
        {
            return 0;
        }
        Direction otherDirection = null;
        Direction[] directions = Direction.values();
        for (int i = 0; i < nextTile.SURROUNDINGS.length; i++)
        {
            if (nextTile.nextTile(directions[i]) == this)
            {
                otherDirection = directions[i];
                break;
            }
        }
        if (otherDirection == null)
        {
            return 0;
        }
        //System.out.println("A: " + dir + " B: " + flip(otherDirection));
        //System.out.println("Value: " + (flip(otherDirection).ordinal() - dir.ordinal() + NOOFDIRECTIONS) % NOOFDIRECTIONS);
        return (flip(otherDirection).ordinal() - dir.ordinal() + NOOFDIRECTIONS) % NOOFDIRECTIONS;
    }

    
    public static Direction rotate(Direction dir, int rot)
    {
        Direction[] directions = Direction.values();
        
        return directions[(dir.ordinal() + NOOFDIRECTIONS + rot) % NOOFDIRECTIONS];
    }
    
    public static Direction flip(Direction dir)
    {
        return rotate(dir, NOOFDIRECTIONS/2);
    }

    public Tile(byte type)
    {
        this.TYPE = type;
        this.SURROUNDINGS = new Tile[NOOFDIRECTIONS];
    }
    
    public static void join(Tile a, Direction dirA, Tile b, Direction dirB)
    {
        a.SURROUNDINGS[dirA.ordinal()] = b;
        b.SURROUNDINGS[dirB.ordinal()] = a;
    }
    
    public static Tile[][] makeRoom(int width, int height, byte air, byte wall1, byte wall2, byte wall3, byte wall4)
    {
        Tile[][] room = new Tile[width+2][height+2];
        for (int ix = 0; ix < width+2; ix++)
        {
            for (int iy = 0; iy < height+2; iy++)
            {
                if (ix == 0)
                {
                    room[ix][iy] = new Tile(wall1);
                }
                else if (iy == 0)
                {
                    room[ix][iy] = new Tile(wall2);
                }
                else if (ix == width+1)
                {
                    room[ix][iy] = new Tile(wall3);
                }
                else if (iy == height+1)
                {
                    room[ix][iy] = new Tile(wall4);
                }
                else
                {
                    room[ix][iy] = new Tile(air);
                }
            }
        }
        for (int ix = 0; ix < width+2; ix++)
        {
            for (int iy = 0; iy < height+2; iy++)
            {
                if (iy+1 < height + 2)
                {
                    join(room[ix][iy], Direction.NORTH, room[ix][iy+1], Direction.SOUTH);
                }
                if (iy-1 >= 0)
                {
                    join(room[ix][iy], Direction.SOUTH, room[ix][iy-1], Direction.NORTH);
                }
                if (ix+1 < width + 2)
                {
                    join(room[ix][iy], Direction.EAST, room[ix+1][iy], Direction.WEST);
                }
                if (ix-1 >= 0)
                {
                    join(room[ix][iy], Direction.WEST, room[ix-1][iy], Direction.EAST);
                }
            }
        }
        return room;
    }
    


    
    
   
}
