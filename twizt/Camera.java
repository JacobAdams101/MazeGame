/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twizt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jacob
 */
public class Camera extends WorldObject
{
    private float FOV = (90f)*( 2f*(float)Math.PI/360f);
    private float angle;
    private BufferedImage frame;
    private int[] framePixelData;

    private final RayCaster[] RAYCASTERS;
    
    private final int MIDX;
    private final int MIDY;
    
    private final Color EMPTYCOLOR = Color.GRAY;
    private final int EMPTYCOLORINT = EMPTYCOLOR.getRGB();

    private Monster myMonster;

    
    
    int OFFSETX = 500;
    int OFFSETY = 500;

    float SCALE = 5;
    
    @Override
    public void rotate()
    {
        float temp;
        temp = px;
        px = py;
        py = -temp;
        temp = vx;
        vx = vy;
        vy = -temp;
        angle += (float)Math.PI/2f;
    }
    
    public Camera(float px, float py, float angle, Tile relativeTo, int frameWidth, int frameHeight, Monster myMonster)
    {
        super(px, py, 0, 0, relativeTo);

        this.angle = angle;
        
        this.myMonster = myMonster;
        
        frame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
        framePixelData = ((DataBufferInt)frame.getRaster().getDataBuffer()).getData();
        
        MIDX = frame.getWidth()/2;
        MIDY = frame.getHeight()/2;
        
        RAYCASTERS = new RayCaster[8];
        
        
        try
        { //Load a font
            GraphicsEnvironment ge =  GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResource("VCR_OSD_MONO.ttf").openStream()));
        }
        catch (IOException|FontFormatException e)
        {
            System.out.println("FAILED");
        }
        //Load the font files
        FONT = new Font("VCR OSD Mono", Font.BOLD, 15);
    }
    
    
    
    public synchronized void rotate(float theta, float time)
    {
        float fullCircle = (float)(Math.PI*2d);
        angle += theta * time;
        while (angle > fullCircle)
        {
            angle -= fullCircle;
        }
        while (angle < 0)
        {
            angle += fullCircle;
        }
    }
    
    public synchronized void moveInDirection(float x, float y, float time)
    {
        this.move((float)(Math.cos(angle)*x)+(float)(Math.sin(angle)*y), -(float)(Math.sin(angle)*x)+(float)(Math.cos(angle)*y), time);
    }
    
    
    public int height = 0;
    public float cycle = 0;
    
    public void updateHeight(float time)
    {
        cycle += time;
        final int SIZE = 10;
        height = (int) (SIZE*Math.sin(cycle*5));
    }
    
    public synchronized BufferedImage getFrame()
    {
        return frame;
    }
    
    private final Font FONT;
    
    public synchronized void updateFrame()
    {
        Graphics g = frame.getGraphics();
        g.setColor(EMPTYCOLOR);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
        
        final int WIDTH = frame.getWidth() / RAYCASTERS.length;
        for (int ix = 0; ix < RAYCASTERS.length; ix++)
        {
            RAYCASTERS[ix] = new RayCaster(WIDTH*ix, WIDTH*(ix+1), frame, (int)height);
        }

        for (int ix = 0; ix < RAYCASTERS.length; ix++)
        {
            if (RAYCASTERS[ix].isAlive() == false)
            {
                RAYCASTERS[ix].start();
            }
        }
        for (int ix = 0; ix < RAYCASTERS.length; ix++)
        {
            try
            {
                RAYCASTERS[ix].join();
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /*
        for (int ix = 0; ix < frame.getWidth(); ix += 4)
        {
            for (int iy = 0; iy < frame.getHeight(); iy += 2)
            {
                
                g.setColor(new Color(50, 50, 50, (int)(Math.random()*50)));

                g.fillRect(ix, iy, 4, 2);
            }
        }
        */
        g.setFont(FONT);
        g.setColor(Color.BLACK);
        g.drawString("Maze Game ALPHA", 0, 15);
        g.drawString("V0.0.1", 0, 30);
        g.drawString("Copyright Jacob Adams", 0, 45);
        g.drawString("Do NOT Distribute!", 0, 60);
        g.drawString("FPS: " + Twizt.fps, 0, 75);
        g.setColor(new Color(130, 130, 130));
        if (Twizt.FUNVALUE > 0.97)
        {
            g.drawString("Michael what were you doing in the code?", 10, 8);
        }
        else if (Twizt.FUNVALUE > 0.94)
        {
            g.drawString("I just made some optimisations!", 10, 8);
        }
        else if (Twizt.FUNVALUE > 0.91)
        {
            g.drawString("Where were you the day it happened?", 10, 8);
        }
        else if (Twizt.FUNVALUE > 0.88)
        {
            g.drawString("Jacob didn't really make this game did he?", 10, 8);
        }
        else if (Twizt.FUNVALUE > 0.85)
        {
            g.drawString("Where did you find this game?", 10, 8);
        }
        else if (Twizt.FUNVALUE > 0.82)
        {
            g.drawString("Where did *HE* find this game?", 10, 8);
        }
        
        g.setColor(new Color(75, 100, 40, 70));
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());
        
        
        /*
        int SIZE = 6;
        int THICKNESS = 2;
        g.setXORMode(Color.WHITE);
        g.fillRect(MIDX-((SIZE-THICKNESS)/2), MIDY, SIZE, THICKNESS);
        g.fillRect(MIDX, MIDY-((SIZE-THICKNESS)/2), THICKNESS, SIZE);
        */
        
    }
    
    public class RayCaster extends Thread
    {
        private final int MINX, MAXX;
        private final Graphics G;
        private final int HEIGHTOFFSET;
        
        public RayCaster(final int MINX, final int MAXX, final BufferedImage image, final int HEIGHTOFFSET)
        {
            this.MINX = MINX;
            this.MAXX = MAXX;
            this.G = image.getGraphics();
            this.HEIGHTOFFSET = HEIGHTOFFSET;
        }
        
        @Override
        public synchronized void run()
        {
            final int STEP = 1;
            for (int ix = MINX; ix < MAXX; ix += STEP)
            {

                float angleOffSet = FOV * (((float)(ix-MIDX))/((float)MIDX*2f));
                float theta = angle + angleOffSet;
                castRay(G, ix, STEP, MIDY+HEIGHTOFFSET, MIDY, 1.1f / (float) Math.cos(angleOffSet), theta, px, py, relativeTo); //Find the tile that ray collides with
            }
        }
        
        public synchronized void castRay(Graphics g, final int DRAWX, final int WIDTH, final int MIDY, final int YRADIUS, final float ANGLESCALE, final float RAYANGLE, final float CASTX, final float CASTY, final Tile CASTTILE)
        {
            //ArrayList<Float>distance = new ArrayList<>();
            //ArrayList<Tile>otherRenders = new ArrayList<>();
            float rayX = CASTX;
            float rayY = CASTY;
            Tile rayTile = CASTTILE;
            float RAYSPEED = 0.0025f;
            float rayXChange = RAYSPEED*(float)Math.sin(RAYANGLE);
            float rayYChange = RAYSPEED*(float)Math.cos(RAYANGLE);

            float dist = 0;
            float Pdist = 0;
            float Mdist = 0;
            float MRX = 0;
            float MRY = 0;
            
            boolean drawPlayer = false;
            boolean drawMonster = false;
            int distFromMidLast = -1;
            for (int i = 0; i < 20000; i++)
            {
                dist += RAYSPEED;
                rayX += rayXChange;
                rayY += rayYChange;
                if (rayX > Tile.RADIUS)
                {
                    rayX -= Tile.DIAMETER;
                    
                    int rot = rayTile.getRotationDifference(Tile.Direction.EAST);
                    for (int i2 = 0; i2 < rot; i2++)
                    {
                        float temp;
                        temp = rayX;
                        rayX = rayY;
                        rayY = -temp;
                        temp = rayXChange;
                        rayXChange = rayYChange;
                        rayYChange = -temp;
                    }
                    
                    rayTile = rayTile.nextTile(Tile.Direction.EAST);
                }
                if (rayTile == null)
                {
                    break;
                }
                if (rayX < -Tile.RADIUS)
                {
                    rayX += Tile.DIAMETER;
                    
                    int rot = rayTile.getRotationDifference(Tile.Direction.WEST);
                    for (int i2 = 0; i2 < rot; i2++)
                    {
                        float temp;
                        temp = rayX;
                        rayX = rayY;
                        rayY = -temp;
                        temp = rayXChange;
                        rayXChange = rayYChange;
                        rayYChange = -temp;
                    }
                    
                    rayTile = rayTile.nextTile(Tile.Direction.WEST);
                }
                if (rayTile == null)
                {
                    break;
                }
                if (rayY > Tile.RADIUS)
                {
                    rayY -= Tile.DIAMETER;
                    
                    int rot = rayTile.getRotationDifference(Tile.Direction.NORTH);
                    for (int i2 = 0; i2 < rot; i2++)
                    {
                        float temp;
                        temp = rayX;
                        rayX = rayY;
                        rayY = -temp;
                        temp = rayXChange;
                        rayXChange = rayYChange;
                        rayYChange = -temp;
                    }
                    
                    rayTile = rayTile.nextTile(Tile.Direction.NORTH);
                }
                if (rayTile == null)
                {
                    break;
                }
                if (rayY < -Tile.RADIUS)
                {
                    rayY += Tile.DIAMETER;
                    
                    int rot = rayTile.getRotationDifference(Tile.Direction.SOUTH);

                    for (int i2 = 0; i2 < rot; i2++)
                    {
                        float temp;
                        temp = rayX;
                        rayX = rayY;
                        rayY = -temp;
                        temp = rayXChange;
                        rayXChange = rayYChange;
                        rayYChange = -temp;
                    }

                    rayTile = rayTile.nextTile(Tile.Direction.SOUTH);
                }
                if (rayTile == null)
                {
                    break;
                }
                if (rayTile == relativeTo && rayX < px + 0.1 && rayX > px - 0.1 && rayY < py + 0.1 && rayY > py - 0.1 && dist > 0.3 && false)
                {
                    
                    drawPlayer = true;
                    Pdist = dist;
                }
                if (rayTile == myMonster.relativeTo && rayX - myMonster.px < Monster.RADIUS && rayX - myMonster.px > -Monster.RADIUS && rayY - myMonster.py < Monster.RADIUS && rayY - myMonster.py  > -Monster.RADIUS && dist > 0.1)
                {
                    drawMonster = true;
                    Mdist = dist;
                    MRX = rayX;
                    MRY = rayY;
                }
                /*
                Can only add after single cast version made
                if (rayTile.getData().HASTEXTURE)
                {
                    distance.add(dist);
                    otherRenders.add(rayTile);
                }
                */
                if (rayTile.getData().getSolid(rayX, rayY))
                {
                    break;
                }
                int distFromMid = (int)((float)YRADIUS*ANGLESCALE/(float)dist);
                if (distFromMid != distFromMidLast)
                {
                    int screenY = distFromMid + MIDY;
                    if (screenY < frame.getHeight())
                    {
                        //set(DRAWX, screenY, rayTile.getData().getPixel2(rayX, rayY));
                        framePixelData[DRAWX+(screenY*frame.getWidth())] = rayTile.getData().getFloorPixel(rayX, rayY);
                        //frame.setRGB(DRAWX, screenY, rayTile.getData().getPixel(rayX, rayY));
                    }
                    screenY = MIDY - distFromMid;
                    if (screenY >= 0)
                    {
                        //set(DRAWX, screenY, rayTile.getData().getPixel2(rayX, rayY));
                        framePixelData[DRAWX+(screenY*frame.getWidth())] = rayTile.getData().getCeilingPixel(rayX, rayY);
                        //frame.setRGB(DRAWX, screenY, rayTile.getData().getPixel2(rayX, rayY));
                    }
                }
                
                distFromMidLast = distFromMid;
            }
            
            if (rayTile != null)
            {
                float height = ((float)YRADIUS)/(float)dist;
                height *= ANGLESCALE;
                //System.out.println("Result diost: " + resultDist  + "Height: " +height);
                int heightInt = (int)height;
                g.drawImage(rayTile.getData().getLine(rayX, rayY), DRAWX, MIDY-heightInt, WIDTH, heightInt*2, null);
                
                if (rayTile.getData().TRIGGERSTHENOTE && dist < 3)
                {
                    Twizt.triggerTheNote();
                }
                if (rayTile.getData().TRIGGERSTHERESPAWN && dist < 5)
                {
                    Twizt.noEscape();
                }
                /*
                Silly lighting
                final float ACCURACY = 0.01f;
                if (rayX < ACCURACY-Tile.RADIUS || rayX > Tile.RADIUS - ACCURACY)
                {
                    g.setColor(new Color(0, 0, 0, 100));
                    g.fillRect(DRAWX, MIDY-heightInt, WIDTH, heightInt*2);
                }
                */
            }
            if (drawPlayer)
            {
                float height = ((float)YRADIUS)/(float)Pdist;
                height *= ANGLESCALE;
                //System.out.println("Result diost: " + resultDist  + "Height: " +height);
                int heightInt = (int)height;
                int heightTop = (int)height/2;
                g.setColor(Color.PINK);
                g.fillRect(DRAWX, MIDY-heightTop, WIDTH, heightInt+heightTop);
            }
            if (drawMonster)
            {
                float height = ((float)YRADIUS)/(float)Mdist;
                height *= ANGLESCALE;
                //System.out.println("Result diost: " + resultDist  + "Height: " +height);
                int heightInt = (int)height;
                int heightTop = (int)(height*1.5f);
                BufferedImage i = myMonster.getLine(MRX-myMonster.px, MRY-myMonster.py);
                if (i != null)
                {
                    g.drawImage(i, DRAWX, MIDY-heightTop, WIDTH, heightInt+heightTop, null);
                }
                if (Mdist < 3)
                {
                    Twizt.monsterGotMe();
                }
                //g.setColor(Color.PINK);
                //g.fillRect(DRAWX, MIDY-heightTop, WIDTH, heightInt+heightTop);
            }
            
        }
        /*
        public void castRay(Graphics g, final int DRAWX, final int WIDTH, final int MIDY, final int YRADIUS, final float ANGLESCALE, final float RAYANGLE, final float CASTX, final float CASTY, final Tile CASTTILE)
        {
            float rayX;
            float rayY;
            
            float rayXChange;
            float rayYChange;
            Tile rayCurrentTile;

            
            final float SIN = (float) Math.sin(RAYANGLE);
            final float COS = (float) Math.cos(RAYANGLE);
            final float NEGATIVETAN = -(float)Math.tan(RAYANGLE);
            final float TANPERPANDICULAR = -1f/(float)Math.tan(RAYANGLE);
            
            float distHorizont;
            float distVert;
            
            Tile tileHorizont;
            Tile tileVert;
            
            float totalX;
            float totalY;
            
            
            float resultDist;
            Tile resultTile;
            
            if (COS < 0)
            {
                rayY = -Tile.RADIUS;
                rayX = ((CASTY - rayY)*TANPERPANDICULAR)+CASTX;
                rayYChange = -Tile.DIAMETER;
                rayXChange = -rayYChange*TANPERPANDICULAR;
                rayCurrentTile = CASTTILE;
            }
            else if (COS > 0)
            {
                rayY = Tile.RADIUS;
                rayX = ((CASTY - rayY)*TANPERPANDICULAR)+CASTX;
                rayYChange = Tile.DIAMETER;
                rayXChange = -rayYChange*TANPERPANDICULAR;
                rayCurrentTile = CASTTILE;
            }
            else
            {
                rayX = 0;
                rayY = 0;
                rayXChange = 0;
                rayYChange = 0;
                rayCurrentTile = null;
            }
            totalX = rayX;
            totalY = rayY;
            RayCastLoop:
            for (int i = 0; i < 10;  i++)
            {
                if (rayCurrentTile == null)
                { //Null
                    break RayCastLoop;
                }
                else if (rayCurrentTile.getData().ISSOLID)
                { //HIt wall
                    break RayCastLoop;
                }
                else
                {
                    rayX += rayXChange;
                    totalX += rayXChange;
                    totalY += rayYChange;
                    while (rayX > Tile.RADIUS)
                    {
                        rayX -= Tile.DIAMETER;
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.EAST);
                        if (rayCurrentTile == null)
                        { //Null
                            break RayCastLoop;
                        }
                    }
                    while (rayX < -Tile.RADIUS)
                    {
                        rayX += Tile.DIAMETER;
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.WEST);
                        if (rayCurrentTile == null)
                        { //Null
                            break RayCastLoop;
                        }
                    }
                    if (rayYChange > 0)
                    {
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.NORTH);
                    }
                    if (rayYChange < 0)
                    {
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.SOUTH);
                    }
                }
            }
            tileHorizont = rayCurrentTile;
            distHorizont = (float)Math.sqrt(((totalX-CASTX)*(totalX-CASTX))+((totalY-CASTY)*(totalY-CASTY)));
            
            
            
            
            //g.setColor(Color.blue);
            //g.drawLine(OFFSETX, OFFSETY, OFFSETX + (int)((totalX-CASTX)*SCALE), OFFSETY + (int)((totalY-CASTY)*SCALE));
            
            //System.out.println("HORIZONT DIST: " + distHorizont);
            
            //Vertical check
            
            if (SIN < 0)
            {
                rayX = -Tile.RADIUS;
                rayY = ((CASTX - rayX)*NEGATIVETAN)+CASTY;
                rayXChange = -Tile.DIAMETER;
                rayYChange = -rayXChange*NEGATIVETAN;
                rayCurrentTile = CASTTILE;
            }
            else if (SIN > 0)
            {
                rayX = Tile.RADIUS;
                rayY = ((CASTX - rayX)*NEGATIVETAN)+CASTY;
                rayXChange = Tile.DIAMETER;
                rayYChange = -rayXChange*NEGATIVETAN;
                rayCurrentTile = CASTTILE;
            }
            else
            {
                rayX = 0;
                rayY = 0;
                rayXChange = 0;
                rayYChange = 0;
                rayCurrentTile = null;
            }
            
            totalX = rayX;
            totalY = rayY;
            RayCastLoop:
            for (int i = 0; i < 10;  i++)
            {
                if (rayCurrentTile == null)
                { //Null
                    break RayCastLoop;
                }
                else if (rayCurrentTile.getData().ISSOLID)
                { //HIt wall
                    break RayCastLoop;
                }
                else
                {
                    rayX += rayXChange;
                    totalX += rayXChange;
                    totalY += rayYChange;
                    while (rayX > Tile.RADIUS)
                    {
                        rayX -= Tile.DIAMETER;
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.EAST);
                        if (rayCurrentTile == null)
                        { //Null
                            break RayCastLoop;
                        }
                    }
                    while (rayX < -Tile.RADIUS)
                    {
                        rayX += Tile.DIAMETER;
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.WEST);
                        if (rayCurrentTile == null)
                        { //Null
                            break RayCastLoop;
                        }
                    }
                    if (rayYChange > 0)
                    {
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.NORTH);
                    }
                    if (rayYChange < 0)
                    {
                        rayCurrentTile = rayCurrentTile.nextTile(Tile.Direction.SOUTH);
                    }
                }
            }
            tileVert = rayCurrentTile;
            distVert = (float)Math.sqrt(((totalX-CASTX)*(totalX-CASTX))+((totalY-CASTY)*(totalY-CASTY)));
           // System.out.println("vERT DIST: " + distVert);

            
            if (distHorizont < distVert)
            {
                resultDist = distHorizont;
                resultTile = tileHorizont;
            }
            else
            {
                resultDist = distVert;
                resultTile = tileVert;
            }
            
            
            
            
            //g.setColor(Color.red);
            //g.drawLine(OFFSETX, OFFSETY, OFFSETX + (int)((totalX-CASTX)*SCALE), OFFSETY + (int)((totalY-CASTY)*SCALE));
            
            
            if (resultTile != null)
            {

                float height = ((float)YRADIUS)/(float)resultDist;
                height *= ANGLESCALE;
                //System.out.println("Result diost: " + resultDist  + "Height: " +height);
                int heightInt = (int)height;
                g.setColor(resultTile.getData().COLOR);
                g.fillRect(DRAWX, MIDY-heightInt, WIDTH, heightInt*2);
            }
            
        }
*/
    }
}
