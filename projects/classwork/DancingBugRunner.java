import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.Location;

import java.awt.Color;

public class DancingBugRunner
{
    public static void main(String[] args)
    {
        ActorWorld world = new ActorWorld();
        
        int[] turnArray = {1,1,1,1,1,1,1,1,1};
        DancingBug a = new DancingBug(turnArray);
        DancingBug b = new DancingBug(turnArray);
        DancingBug c = new DancingBug(turnArray);
        DancingBug d = new DancingBug(turnArray);
        DancingBug e = new DancingBug(turnArray);
        
        b.setColor(Color.yellow);
        c.setColor(Color.green);
        d.setColor(Color.blue);
        e.setColor(Color.orange);
        
        world.add(new Location(2, 1), a);
        world.add(new Location(2, 5), b);
        world.add(new Location(2, 9), c);
        world.add(new Location(5, 3), d);
        world.add(new Location(5, 7), e);
        world.show();
    }
}