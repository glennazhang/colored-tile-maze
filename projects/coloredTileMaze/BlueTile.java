import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.Color;
import java.util.ArrayList;

public class BlueTile extends Tile {
	
	public BlueTile(){
		setColor(Color.blue);
	}
	
	public void processActors(ArrayList<Actor> actors, Player player){
		boolean nearYellow = false;
		for (Actor a : actors)
        {
            if (a instanceof YellowTile)
            	nearYellow = true;
        }
		
		if (nearYellow && player.getFlavor().equals("lemon"))
			player.dies("You were electrocuted!");
		else if (player.getFlavor().equals("orange") && !nearYellow){
			player.dies("You were eaten by piranhas!");
		}
		else if (nearYellow && player.getFlavor().equals("orange"))
			player.dies("You were electrocuted and eaten by piranhas!");
		
	}

	public ArrayList<Actor> getActors()
    {
        ArrayList<Actor> actors = new ArrayList<Actor>();
        int[] dirs =
            { Location.NORTH, Location.EAST, Location.SOUTH, Location.WEST};
        for (Location loc : getLocationsInDirections(dirs))
        {
            Actor a = getGrid().get(loc);
            if (a != null)
                actors.add(a);
        }

        return actors;
    }
	
	public ArrayList<Location> getLocationsInDirections(int[] directions)
    {
        ArrayList<Location> locs = new ArrayList<Location>();
        Grid<Actor> gr = getGrid();
    
        for (int d : directions)
        {
            Location neighborLoc = getLocation().getAdjacentLocation(d);
            if (gr.isValid(neighborLoc))
                locs.add(neighborLoc);
        }
        return locs;
    }  
	
	public void act(Player player){
		processActors(getActors(), player);
	}
}
