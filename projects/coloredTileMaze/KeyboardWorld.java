import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

public class KeyboardWorld extends ActorWorld
{
	public KeyboardWorld()
	{
		super();
	}
	public KeyboardWorld(Grid g)
	{
		super(g);
	}

	public void showSolution(){
		int numRows, numCols, r, c;
		String color;
		ActorWorld world = null;
		try{
			Scanner inFile = new Scanner(new File("Solution.txt"));
			numRows = inFile.nextInt();
			numCols = inFile.nextInt();
			inFile.nextLine();
			String[] data;
			world = new ActorWorld(new BoundedGrid<Actor>(numRows, numCols));
			System.setProperty("info.gridworld.gui.selection", "hide");
			world.setMessage("Solution");
			world.show();
			world.getFrame().setSize(740, 340);
			world.getWorldFrame().getController().getGridPanel().zoomOut();
			world.setMessage("Colored-Tile Maze\nSolution");
			world.getWorldFrame().getController().hideButtonsAndLabels();
			world.getWorldFrame().getController().getExitButton().setText("Close");
			while (inFile.hasNext()) {
				color = inFile.nextLine();
				data = inFile.nextLine().split(",");
				r = Integer.parseInt(data[0]);
				c = Integer.parseInt(data[1]);
				if (color.equals("red"))
					world.add(new Location(r, c), new RedTile());
				else if (color.equals("orange"))
					world.add(new Location(r, c), new OrangeTile());
				else if (color.equals("yellow"))
					world.add(new Location(r, c), new YellowTile());
				else if (color.equals("green"))
					world.add(new Location(r, c), new GreenTile());
				else if (color.equals("blue"))
					world.add(new Location(r, c), new BlueTile());
				else if (color.equals("purple"))
					world.add(new Location(r, c), new PurpleTile());
				else if (color.equals("pink"))
					world.add(new Location(r, c), new PinkTile());
			}
			inFile.close();    
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public boolean keyPressed(String description, Location loc)
	{
		Grid<Actor> gr=getGrid();
		ArrayList<Actor> actors= new ArrayList<Actor>();
		for(Location location : gr.getOccupiedLocations())
			actors.add(gr.get(location));
		for(Actor a : actors)
			if(a.getGrid() == gr && a instanceof KeyboardControllable)
				((KeyboardControllable) a).actionToPerform(description);
		return true;
	}
}