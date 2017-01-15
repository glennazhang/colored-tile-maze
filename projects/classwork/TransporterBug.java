import java.awt.Color;

import info.gridworld.actor.Actor;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Flower;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
/**
 * A <code>BoxBug</code> traces out a square "box" of a given size. <br />
 * The implementation of this class is testable on the AP CS A and AB exams.
 */
public class TransporterBug extends Bug
{

	/**
	 * Constructs a box bug that traces a square of a given side length
	 * @param length the side length
	 */
	public TransporterBug()
	{
		setColor(Color.green);
	}

	public void move(Location newLoc)
	{
		Grid<Actor> gr = getGrid();
		if (gr == null)
			return;
		if (gr.isValid(newLoc))
			moveTo(newLoc);
		else
			removeSelfFromGrid();
	}

	public void transport()
	{
		Location newLoc;
		Grid gr = getGrid();
		do{
			newLoc = new Location(
					(int)(Math.random()*gr.getNumRows()),
					(int)(Math.random()*gr.getNumCols()));
		}while(getGrid().get(newLoc) !=null);
		move(newLoc);
	}
/**
 * Moves to the next location of the square.
 */
public void act()
{
	Location l = new Location(0,0);
	Grid gr = getGrid();

	if (getLocation().equals(l))
		if(gr.getNumRows() * gr.getNumCols() > 	gr.getOccupiedLocations().size())
			transport();
		else setDirection(getDirection()+180);
	else super.act();
}
}
