import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;

import java.awt.Color;

public class SpiralBugRunner
{
    public static void main(String[] args)
    {
        ActorWorld world = new ActorWorld();
        
        SpiralBug a = new SpiralBug(4);
        a.setColor(Color.ORANGE);
        SpiralBug b = new SpiralBug(3);
        b.setColor(Color.BLUE);
        world.add(new Location(7, 2), a);
        world.add(new Location(5, 5), b);
        world.show();
    }
}