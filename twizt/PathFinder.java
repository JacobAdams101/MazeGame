
package twizt;

import java.util.ArrayList;
import twizt.Tile.Direction;

/**
 *
 * @author jacob
 */
public class PathFinder
{
    public PathFinder()
    {
        
    }
    
    public Direction nextPath(Tile start, Tile end)
    {
        
        
        ArrayList<Tile>observedTiles = new ArrayList<>();
        observedTiles.add(start);
        while (observedTiles.size() > 0)
        {
            Tile current = observedTiles.remove(0);
            
            Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
            
            for (Direction d : directions)
            {
                Tile adjacentTile = current.nextTile(d);
                if (adjacentTile != null)
                {
                    if (adjacentTile.distance != -1)
                    {
                        adjacentTile.distance = -1;
                        observedTiles.add(adjacentTile);
                    }
                }
            }
        }
        
        observedTiles = new ArrayList<>();
        observedTiles.add(start);
        while (observedTiles.size() > 0)
        {
            int smallestIndex = 0;
            int smallestScore = -1;
            for (int i = 0; i < observedTiles.size(); i++)
            {
                Tile d = observedTiles.get(i);
                if (smallestScore == -1 || d.distance < smallestScore)
                {
                    smallestScore = d.distance;
                    smallestIndex = i;
                }
            }
            Tile current = observedTiles.remove(smallestIndex);
            
            int newDistance = current.distance + 1;
            
            Direction[] directions = Direction.values();
            
            for (Direction d : directions)
            {
                Tile adjacentTile = current.nextTile(d);
                if (adjacentTile != null)
                {
                    boolean[][] solid = adjacentTile.getData().ISSOLID;
                    boolean transparent = true;
                    for (int ix = 0; ix < solid.length; ix++)
                    {
                        for (int iy = 0; iy < solid[0].length; iy++)
                        {
                            if (solid[ix][iy] == true)
                            {
                                transparent = false;
                            }
                        }
                    }
                    if (transparent)
                    {
                        if (adjacentTile.distance == -1)
                        {
                            adjacentTile.distance = newDistance;
                            observedTiles.add(adjacentTile);
                        }
                        if (adjacentTile.distance > newDistance)
                        {
                            adjacentTile.distance = newDistance;
                        }
                        if (adjacentTile == end)
                        {
                            for (Direction moveDirection : directions)
                            {
                                if (adjacentTile.nextTile(moveDirection) == current)
                                {
                                    Music.setVolume(adjacentTile.distance);
                                    return moveDirection;
                                }
                            }
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }

}
