import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Location;

import java.awt.Color;

public class TransportBugRunner
{
    public static void main(String[] args)
    {
    	BoundedGrid<Actor> bg = new BoundedGrid<Actor>(2,2);
        ActorWorld world = new ActorWorld(bg);
  //      ActorWorld world = new ActorWorld();
        TransporterBug scotty = new TransporterBug();
        scotty.setDirection(Location.WEST);
        world.add(new Location(1,1), scotty);
        world.show();
    }
}