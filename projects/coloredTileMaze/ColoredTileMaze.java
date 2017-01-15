import info.gridworld.actor.Actor;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import info.gridworld.gui.WorldFrame;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ColoredTileMaze
{
	private static Player player;

	public static void changeJOP()
	{
		UIManager.put("Label.font", new FontUIResource (new Font("Calibri", Font.PLAIN, 20)));
		UIManager.put("OptionPane.messageForeground",new Color(219,112,147));

		UIManager.put("TextField.background", new Color(230,230,250));
		UIManager.put("TextField.font", new FontUIResource(new Font("Calibri", Font.PLAIN, 14)));
		UIManager.put("TextField.foreground", Color.black);

		UIManager.put("Panel.background",new Color (255,228,225));
		UIManager.put("OptionPane.background",new Color(152,251,152));

		UIManager.put("Button.background",new Color(219,112,147));
		UIManager.put("Button.foreground", Color.black);
		UIManager.put("Button.font", new FontUIResource	(new Font("Calibri", Font.PLAIN, 14)));
	}

	

	public static KeyboardWorld readIn(KeyboardWorld world){
		int numRows, numCols, answer;
		String color, question;
		try{
			Scanner inFile = new Scanner(new File("Tiles.txt"));
			numRows = inFile.nextInt();
			numCols = inFile.nextInt();
			inFile.nextLine();
			world = new KeyboardWorld(new BoundedGrid<Actor>(numRows, numCols));
			System.setProperty("info.gridworld.gui.selection", "hide");
			world.add(new Location(2,0),new Player(world));
			Grid<Actor> gr= world.getGrid();
			ArrayList<Actor> actors= new ArrayList<Actor>();
			for(Location location : gr.getOccupiedLocations())
				actors.add(gr.get(location));
			for(Actor a : actors){
				if(a.getGrid() == gr && a instanceof KeyboardControllable)
					player = (Player) a;
			}
			
			world.show();
			WorldFrame frame = (WorldFrame)world.getFrame();
	        Location currentLoc = player.getLocation();
	        int col = currentLoc.getCol() - 8;
	        Location loc = new Location(0,col);
	        frame.display.recenter(loc);
			world.getWorldFrame().getController().getGridPanel().zoomIn();
			while (inFile.hasNext()) {
				for (int r = 2; r < 6; r++){
					for (int c = 1; c < numCols; c++){
						color = inFile.nextLine();
						if (color.equals("red"))
							world.add(new Location(r, c), new RedTile());
						else if (color.equals("orange"))
							world.add(new Location(r, c), new OrangeTile());
						else if (color.equals("yellow"))
							world.add(new Location(r, c), new YellowTile());
						else if (color.equals("green")){
							question = inFile.nextLine();
							answer = inFile.nextInt();
							inFile.nextLine();
							world.add(new Location(r, c), new GreenTile(question, answer));
						}
						else if (color.equals("blue"))
							world.add(new Location(r, c), new BlueTile());
						else if (color.equals("purple"))
							world.add(new Location(r, c), new PurpleTile());
						else if (color.equals("pink"))
							world.add(new Location(r, c), new PinkTile());
					}
				}
			}
			inFile.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		return world;
	}

	public static void printDirections()
	{
		String answer = "Hello, and welcome to the Colored-Tile Maze! :3\n"
				+ "Here are the rules:\n"
				+ "Use the arrow keys to move through the maze.\n"
				+ "Red tiles are impassable- you cannot walk on them!\n"
				+ "Yellow tiles are electric- they will electrocute you!\n"
				+ "Green tiles are alarm tiles- if you step on them... \n"
				+ "you will have to answer a tough multiple-choice question!\n"
				+ "Orange tiles are orange scented- "
				+ "they will make you smell delicious!\n"
				+ "Blue tiles are water tiles- swim through them if you like,\n"
				+ "but if you smell like oranges! the piranhas will bite you.\n"
				+ "Also, if a blue tile is next to a yellow tile, "
				+ "the water will also zap you!\n"
				+ "Purple tiles are slippery- you will slide to the next tile!\n"
				+ "However, the slippery soap... smells like lemons!\n"
				+ "Which piranhas do not like, so purple and blue are okay!\n"
				+ "Finally, pink tiles. They don't do anything. Step on them all you like. :3";
		JOptionPane.showMessageDialog(null, answer);
	}

	public static void main(String[] args)
	{
		changeJOP();
		printDirections();
		KeyboardWorld world = null;
		world = readIn(world);
		world.setMessage("Colored-Tile Maze \nMake it to the end without dying!");
	}
}