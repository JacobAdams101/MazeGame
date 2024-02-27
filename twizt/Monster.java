/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twizt;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import twizt.Tile.Direction;

/**
 *
 * @author jacob
 */
public class Monster extends WorldObject
{
    
    public BufferedImage texture;
        
    private BufferedImage[] slice;
    
    private PathFinder pf;
    
    public Monster(float px, float py, Tile relativeTo)
    {
        super(px, py, 0, 0, relativeTo);
        
        pf = new PathFinder();

        try
            {
                this.texture = ImageIO.read(this.getClass().getResource("Monster.png"));
                slice = new BufferedImage[texture.getWidth()];
                
                for (int i = 0; i < slice.length; i++)
                {
                    slice[i] = texture.getSubimage(i, 0, 1, texture.getHeight());
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static final float WIDTH = 0.2f;
    public static final float RADIUS = WIDTH / 2f;
    
    public BufferedImage getLine(float x, float y)
    {
        
        int xPos = (int)(((x + (float)RADIUS) * ((float)texture.getWidth())) / ((float)WIDTH));
        int yPos = (int)(((y + (float)RADIUS) * ((float)texture.getWidth())) / ((float)WIDTH));
        
        if (xPos == 0 || xPos == slice.length-1)
        {
            if (yPos >= 0 && yPos < slice.length)
            {
                return slice[yPos];
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (xPos >= 0 && xPos < slice.length)
            {
                return slice[xPos];
            }
            else
            {
                return null;
            }
        }
        
        

    }
    
    float count = 0;
    float maxTime = 0;
    
    public void run(float time, Tile target)
    {
        if (this.relativeTo != null)
        {
            
            Direction d = pf.nextPath(target, this.relativeTo);
            
            //System.out.println("Direction: " + d);
            
            //System.out.println(count);
            
            final float SIZE = 3;
            if (count > this.maxTime)
            {
                maxTime = 0.1f-((float)Math.random()*1f);
                count = 0;
                if (d == null)
                {
                    vx = 0;
                    vy = 0;
                }
                else
                {
                    switch(d)
                    {
                        case NORTH:
                            vy = SIZE;
                            if (this.px > 0)
                            {
                                vx = -SIZE;
                            }
                            if (this.px < 0)
                            {
                                vx = SIZE;
                            }

                            break;
                        case SOUTH:
                            vy = -SIZE;
                            if (this.px > 0)
                            {
                                vx = -SIZE;
                            }
                            if (this.px < 0)
                            {
                                vx = SIZE;
                            }
                            break;
                        case EAST:
                            if (this.py > 0)
                            {
                                vy = -SIZE;
                            }
                            if (this.py < 0)
                            {
                                vy = SIZE;
                            }
                            vx = SIZE;
                            break;
                        case WEST:
                            if (this.py > 0)
                            {
                                vy = -SIZE;
                            }
                            if (this.py < 0)
                            {
                                vy = SIZE;
                            }
                            vx = -SIZE;
                            break;
                    }
                }
            }
            count += time;
            
            
            


            this.move(time);
        }
    }
    
    
}
