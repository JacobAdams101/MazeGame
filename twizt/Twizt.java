 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twizt;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import twizt.Tile.Direction;

/**
 *
 * @author jacob
 */
public class Twizt

{

    
    public static final float FUNVALUE = (float)Math.random();
    
    static Monster m;
    static Tile THEDOOR;
    static Camera cam;
    static Tile SPAWN;
    
    public static float fps = 0;
    
    public static boolean hasPlayedTheNote = false;
    
    public static void triggerTheNote()
    {
        if (hasPlayedTheNote == false)
        {
            hasPlayedTheNote = true;
            m.relativeTo = SPAWN;
            Sounds.playTheNote();
            Music.playRoar();
            
        }
    }
    
    public static int exitTime = -1;
    
    public static Window window;
    public static void monsterGotMe()
    {
        //Sounds.playTheMonster();
        //cam.relativeTo = SPAWN;
        //window.trigger();
        exitTime = 10000;
        Sounds.playTheMonster();
    }
    
    public static void noEscape()
    {
        cam.relativeTo = SPAWN;
        exitTime = 500000;
        Sounds.playTheMonster();
        window.trigger();
    }
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public synchronized static void main(String[] args) throws InterruptedException
    {
        if (FUNVALUE < 0.01)
        {
            JOptionPane.showMessageDialog(null, "Unexpected Unexpected Unexpected!\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT\nIT WON'T GET OUT");
            System.exit(0);
        }
        else if (FUNVALUE < 0.1)
        {
            JOptionPane.showMessageDialog(null, "Unexpected Crash!\nSending Report...");
            System.exit(0);
        }
        
        Music.playHum();
        
        KeyPressedEvent key = new KeyPressedEvent(); 
        
        window = new Window();
        /*
        Tile[][] room = Tile.makeRoom(3, 3, (byte)0, (byte)1, (byte)2, (byte)3, (byte)4);
        
        room[2][0].update((byte)0);
        room[2][4].update((byte)0);
        Tile.join(room[2][4], Tile.Direction.NORTH, room[2][0], Tile.Direction.SOUTH);
        room[0][2].update((byte)0);
        room[4][2].update((byte)0);
        Tile.join(room[4][2], Tile.Direction.EAST, room[0][2], Tile.Direction.WEST);
        
        Camera cam = new Camera(0, 0, 0f, room[2][2], window.getWidth(), window.getHeight());
        */
        
        
        
        SPAWN = generateMaze(256);
        //SPAWN = generateMaze(10);
        final int SCALEDOWN = 2;
        cam = new Camera(0f, 0f, 0f, SPAWN, window.getWidth()/SCALEDOWN, window.getHeight()/SCALEDOWN, m);
        
        int frameCount = 0;
        
        long frameStart = System.nanoTime();
        final long COUNTFRAMES = 1000000000;
        
        long start;
        long end;
        float duration = 0;
        while (true)
        {
            
            start = System.nanoTime();
            window.render(cam);
            m.run(duration, cam.relativeTo);
            if (key.lookLeft)
            {
                cam.rotate(-3f, duration);
            }
            if (key.lookRight)
            {
                cam.rotate(3f, duration);
            }
            float speed = 2f;
            
            boolean bob = false;
            float bobSpeed = 1;
            
            if (key.shift)
            {
                speed *= 1.6f;
                bobSpeed = 2;
            }
            if (key.forward)
            {
                cam.moveInDirection(0, speed, duration);
                bob = true;
            }
            if (key.backward)
            {
                cam.moveInDirection(0, -speed, duration);
                bob = true;
            }
            if (key.strafeleft)
            {
                cam.moveInDirection(-speed, 0, duration);
                bob = true;
            }
            if (key.straferight)
            {
                cam.moveInDirection(speed, 0, duration);
                bob = true;
            }
            
            
            if (bob)
            {
                cam.updateHeight(duration*bobSpeed);
            }
            if (m.relativeTo == cam.relativeTo)
            {
                monsterGotMe();
            }
            end = System.nanoTime();
            duration = (float)(end - start)*0.000000001f;
            frameCount++;
            if (end - frameStart > COUNTFRAMES)
            {
                fps = ((float)frameCount)/((float)(end - frameStart)*0.000000001f);
                frameCount = 0;
                frameStart = end;
            }
            
            if (exitTime >= 0)
            {
                if (exitTime == 0)
                {
                    System.exit(0);
                }
                exitTime--;
            }
        }
    }
    
    public static class Pair<A, B>
    {
        public final A A;
        public final B B;
        
        public Pair(A a, B b)
        {
            this.A = a;
            this.B = b;
        }
        
        @Override
        public boolean equals(Object o)
        {
            if (o instanceof Pair)
            {
                Pair p = (Pair)o;
                return this.A.equals(p.A) && this.B.equals(p.B);
            }
            else
            {
                return false;
            }
        }
    }
    
    public static Tile generateMaze(int noOfRooms)
    {
        
        ArrayList<Pair<int[], Direction>>doors = new ArrayList<>();
        
        Tile[][][] tiles = new Tile[noOfRooms][][];
        
        for (int i = 0; i < noOfRooms; i++)
        {
            int width = random(5, 12);
            int height = random(5, 12);
            if (i == noOfRooms-1)
            {
                width = 12;
                height = 12;
            }
            
            tiles[i] = Tile.makeRoom(width, height, (byte)0, (byte)1, (byte)1, (byte)1, (byte)1);
            
            
            for (int i2 = 0; i2 < random(1,10); i2++)
            { //Pillar 1
                int x = random(2, width-1);
                int y = random(2, height-1);
                tiles[i][x][y].update((byte)random(16,18));
            }
            if (Math.random() > 0.95)
            { //Crate
                int x = random(1, width);
                int y = random(1, height);
                tiles[i][x][y].update((byte)19);
            }
            if (Math.random() > 0.95)
            { //Something on the carpet
                int x = random(1, width);
                int y = random(1, height);
                tiles[i][x][y].update((byte)15);
            }
             
            
            if (i == noOfRooms - 1)
            {
                Tile[][] escape = Tile.makeRoom(10, 10, (byte)14, (byte)13, (byte)13, (byte)13, (byte)13);
                escape[4][4].update((byte)12);
                escape[4][6].update((byte)12);
                escape[6][4].update((byte)12);
                escape[6][6].update((byte)12);
                
                THEDOOR = tiles[i][6][6];
                tiles[i][5][5].update((byte)12);
                tiles[i][5][7].update((byte)12);
                tiles[i][7][5].update((byte)12);
                tiles[i][7][7].update((byte)12);
                
                Tile.join(tiles[i][6][5], Direction.NORTH, escape[5][5], Direction.SOUTH);
                m = new Monster(0, 0, null);
            }
            ArrayList<Pair<Integer, Integer>>usedCords = new ArrayList<>();
            
            int noOfDoors;
            
            if (width+height > 10)
            {
                noOfDoors = 2 * random(2, 4);
            }
            else if (width+height > 8)
            {
                noOfDoors = 2 * random(1, 3);
            }
            else
            {
                noOfDoors = 2 * random(1, 2);
            }
            
            if (Math.random() > 0.9) //0.9
            {
                int x;
                int y;
                if (Math.random() > 0.5)
                {
                    x = random(1, width);
                    y = height+1;
                }
                else
                {
                    x = width+1;
                    y = random(1, height);
                }
                int result = random(2, 12);
                if (result > 11)
                {
                    result = 11;
                }
                //System.out.println("Updating" + x + y + result);
                tiles[i][x][y].update((byte)result);
            }
            
            for (int i2 = 0; i2 < noOfDoors; i2++)
            {
                int x = 0;
                int y = 0;
                Direction face = null;
                do
                {
                    face = Direction.values()[random(0, 3)];
                    if (null != face)
                    switch (face)
                    {
                        case NORTH:
                            x = random(1, width);
                            y = height+1;
                            break;
                        case SOUTH:
                            x = random(1, width);
                            y = 0;
                            break;
                        case EAST:
                            x = width + 1;
                            y = random(1, height);
                            break;
                        case WEST:
                            x = 0;
                            y = random(1, height);
                            break;
                        default:
                            break;
                    }
                }
                while (usedCords.contains(new Pair<>(x, y)) || usedCords.contains(new Pair<>(x+1, y)) || usedCords.contains(new Pair<>(x-1, y)) || usedCords.contains(new Pair<>(x, y+1)) || usedCords.contains(new Pair<>(x, y-1)));
                tiles[i][x][y].update((byte)0); 
                usedCords.add(new Pair<>(x, y));
                //doors.add(new Pair<>(tiles[i][x][y], face));
                doors.add(new Pair<>(new int[]{i,x,y}, face));
            }
        }
        while (doors.size() > 0)
        {
            int index = random(0, doors.size()-1);
            Pair<int[], Direction> a = doors.get(index);
            Tile aTile = tiles[a.A[0]][a.A[1]][a.A[2]];
            doors.remove(index);
            index = random(0, doors.size()-1);
            Pair<int[], Direction> b = doors.get(index);
            
            Tile aTile1;
            Tile aTile2;
            
            Tile bTile;
            Tile bTile1;
            Tile bTile2;
            
            int xChangeA = 0;
            int yChangeA = 0;
            
            int xChangeB = 0;
            int yChangeB = 0;
            
            Direction directionA1 = null;
            Direction directionA2 = null;
            
            Direction directionB1 = null;
            Direction directionB2 = null;
            switch(a.B)
            {
                case NORTH:
                    yChangeA = 1;
                    directionA1 = Direction.EAST;
                    directionA2 = Direction.WEST;
                    break;
                case SOUTH:
                    yChangeA = 1;
                    directionA1 = Direction.EAST;
                    directionA2 = Direction.WEST;
                    break;
                case EAST:
                    xChangeA = 1;
                    directionA1 = Direction.NORTH;
                    directionA2 = Direction.SOUTH;
                    break;
                case WEST:
                    xChangeA = 1;
                    directionA1 = Direction.NORTH;
                    directionA2 = Direction.SOUTH;
                    break;
                default:
                    break;
            }
            
            
            switch(b.B)
            {
                case NORTH:
                    yChangeB = -1;
                    directionB1 = Direction.EAST;
                    directionB2 = Direction.WEST;
                    break;
                case SOUTH:
                    yChangeB = 1;
                    directionB1 = Direction.EAST;
                    directionB2 = Direction.WEST;
                    break;
                case EAST:
                    xChangeB = -1;
                    directionB1 = Direction.NORTH;
                    directionB2 = Direction.SOUTH;
                    break;
                case WEST:
                    xChangeB = 1;
                    directionB1 = Direction.NORTH;
                    directionB2 = Direction.SOUTH;
                    break;
                default:
                    break;
            }
            
            bTile = tiles[b.A[0]][b.A[1]+xChangeB][b.A[2]+yChangeB];
            
            bTile1 = tiles[b.A[0]][b.A[1]+xChangeB+Math.abs(yChangeB)][b.A[2]+yChangeB+Math.abs(xChangeB)];
            bTile2 = tiles[b.A[0]][b.A[1]+xChangeB-Math.abs(yChangeB)][b.A[2]+yChangeB-Math.abs(xChangeB)];
            
            
            //aTile1 = tiles[a.A[0]][a.A[1]+Math.abs(yChangeA)][a.A[2]+Math.abs(xChangeA)];
            //aTile2 = tiles[a.A[0]][a.A[1]-Math.abs(yChangeA)][a.A[2]-Math.abs(xChangeA)];
            
            doors.remove(index);
            Tile.join(aTile, a.B, bTile, b.B);
            
            //Tile.join(aTile1, a.B, bTile1, b.B);
            //Tile.join(aTile2, a.B, bTile2, b.B);
            
            //Join for collision inaccuracy redundancy
            //Tile.join(aTile, directionA1, aTile1, directionB1);
            //Tile.join(aTile, directionA2, aTile2, directionB2);
        }
        return tiles[0][2][2];
    }
    
    
    public static int random(int min, int max)
    {
        double range = Math.random()*(double)(max-min+1);
        
        return min + (int)range;
    }
    
}
