import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;

import java.awt.Color;

public class ZBugRunner
{
    public static void main(String[] args)
    {
        ActorWorld world = new ActorWorld();
        
        ZBug z = new ZBug(5);
        z.setColor(Color.orange);
        ZBug y = new ZBug(3);
        y.setColor(Color.blue);
        ZBug x = new ZBug(5);
        x.setColor(Color.pink);
        world.add(new Location(4, 7), z);
        world.add(new Location(0, 6), y);
        world.add(new Location(5, 4), x);
        world.show();
    }
}