/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twizt;

/**
 *
 * @author jacob
 */
public abstract class WorldObject
{
    float px, py;
    float vx, vy;
    Tile relativeTo;
    
    public WorldObject(float px, float py, float vx, float vy, Tile relativeTo)
    {
        this.px = px;
        this.py = py;
        this.vx = vx;
        this.vy = vy;
        this.relativeTo = relativeTo;
    }
    
    public void move()
    {
        move(1);
    }
    
    public void move(float time)
    {
        move(vx*time, vy*time);
    }
    public boolean testLocation(float tempX, float tempY, Tile tempTile)
    {
        while (tempX > Tile.RADIUS)
        {
            tempX -= Tile.DIAMETER;
            tempTile = tempTile.nextTile(Tile.Direction.EAST); //Go to next tile
            if (tempTile == null)
            {
                return false;
            }
        }
        while (tempX < -Tile.RADIUS)
        {
            tempX += Tile.DIAMETER;
            tempTile = tempTile.nextTile(Tile.Direction.WEST); //Go to next tile
            if (tempTile == null)
            {
                return false;
            }
        }
        while (tempY > Tile.RADIUS)
        {
            tempY -= Tile.DIAMETER;
            tempTile = tempTile.nextTile(Tile.Direction.NORTH); //Go to next tile
            if (tempTile == null)
            {
                return false;
            }
        }
        while (tempY < -Tile.RADIUS)
        {
            tempY += Tile.DIAMETER;
            tempTile = tempTile.nextTile(Tile.Direction.SOUTH); //Go to next tile
            if (tempTile == null)
            {
                return false;
            }
        }
        return !tempTile.getData().getSolid(tempX, tempY);
    }
    public boolean canMove(float vx, float vy)
    {
        final float CAMRADIUS = 0.1f;
        float tempX = px + vx;
        float tempY = py + vy;
        Tile tempTile = relativeTo;
        return !((!testLocation(tempX + CAMRADIUS, tempY + CAMRADIUS, tempTile)) || 
                (!testLocation(tempX - CAMRADIUS, tempY + CAMRADIUS, tempTile)) ||
                (!testLocation(tempX + CAMRADIUS, tempY - CAMRADIUS, tempTile)) ||
                (!testLocation(tempX - CAMRADIUS, tempY - CAMRADIUS, tempTile)));
    }
    public void move(float vx, float vy, float time)
    {
        move(vx*time, vy*time);
    }
    public void move(float vx, float vy)
    {
        if (canMove(vx, vy) == false)
        {
            
            if (canMove(vx, 0) == true)
            {
                 vy = 0;
            }
            else if (canMove(0, vy) == true)
            {
                 vx = 0;
            }
            else
            {
                return;
            }
        }
        
        px += vx;
        py += vy;
        while (px > Tile.RADIUS)
        {
            px -= Tile.DIAMETER;
            
            int rot = relativeTo.getRotationDifference(Tile.Direction.EAST);
            for (int i2 = 0; i2 < rot; i2++)
            {
                rotate();
            }
            relativeTo = relativeTo.nextTile(Tile.Direction.EAST); //Go to next tile
            if (relativeTo == null)
            {
                return;
            }
        }
        while (px < -Tile.RADIUS)
        {
            px += Tile.DIAMETER;
            
            int rot = relativeTo.getRotationDifference(Tile.Direction.WEST);
            for (int i2 = 0; i2 < rot; i2++)
            {
                rotate();
            }
            relativeTo = relativeTo.nextTile(Tile.Direction.WEST); //Go to next tile
            if (relativeTo == null)
            {
                return;
            }
        }
        while (py > Tile.RADIUS)
        {
            py -= Tile.DIAMETER;
            
            int rot = relativeTo.getRotationDifference(Tile.Direction.NORTH);
            for (int i2 = 0; i2 < rot; i2++)
            {
                rotate();
            }
            relativeTo = relativeTo.nextTile(Tile.Direction.NORTH); //Go to next tile
            if (relativeTo == null)
            {
                return;
            }
        }
        while (py < -Tile.RADIUS)
        {
            py += Tile.DIAMETER;
            
            int rot = relativeTo.getRotationDifference(Tile.Direction.SOUTH);
            for (int i2 = 0; i2 < rot; i2++)
            {
                rotate();
            }
            relativeTo = relativeTo.nextTile(Tile.Direction.SOUTH); //Go to next tile
            if (relativeTo == null)
            {
                return;
            }
        }
    }
    
    public void rotate()
    {
        float temp;
        temp = px;
        px = py;
        py = -temp;
        temp = vx;
        vx = vy;
        vy = -temp;
    }
}
