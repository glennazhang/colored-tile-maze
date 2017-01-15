import javax.swing.JOptionPane;

import info.gridworld.actor.Actor;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import info.gridworld.gui.WorldFrame;

public class Player extends Actor implements KeyboardControllable
{
	private Actor tempActor;
    private Location tempLoc;
    private int deathCount;
    private String flavor;
    private KeyboardWorld world;
    
    public Player(KeyboardWorld w)
    {
    	tempActor = null;
    	tempLoc = null;
    	deathCount = 0;
    	flavor = "orange";
    	world = w;
    }
    
    public void setFlavor(String f){
    	flavor = f;
    	world.getWorldFrame().getController().setFlavor("Flavor: " + f);
    }
    
    public String getFlavor(){
        return flavor;
    }
    
    public void setDeaths(){
        deathCount++;
        world.getWorldFrame().getController().setDeaths("Deaths: " + deathCount);
    }
    
    public void dies(String message){
    	int choice = JOptionPane.showOptionDialog(null, message + 
                "\nTry again?", "Try Again?", 1, 3, null, null, null);
        if (choice==0){
            setFlavor("orange");
            setDeaths();
            this.moveTo(new Location(2,0));
            this.setDirection(Location.NORTH);
        }
        else System.exit(0);
    }
    
    public void moveTo(Location newLocation)
    {    
        if (getGrid() == null)
            throw new IllegalStateException("This actor is not in a grid.");
        if (getGrid().get(getLocation()) != this)
            throw new IllegalStateException(
                    "The grid contains a different actor at location "
                            + getLocation() + ".");
        if (!getGrid().isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(getLocation()))
            return;
        
        getGrid().remove(getLocation());
        
        if (tempActor != null && tempLoc != null){
        	getGrid().put(tempLoc, tempActor);
        }
        
        Actor other = getGrid().get(newLocation);
        if (other != null){
        	tempActor = getGrid().remove(newLocation);
        	tempLoc = newLocation;
        }
        
        setLocation(newLocation);
        getGrid().put(getLocation(), this);
    }
    
    public void scrolling(){
        WorldFrame frame = (WorldFrame)world.getFrame();
        Location currentLoc = getLocation();
        int col = currentLoc.getCol() - 8;
        Location loc = new Location(0,col);
        frame.display.recenter(loc);
    }

    public void act()
    {
    	if (canMove()){
        	Location next = getLocation().getAdjacentLocation(getDirection());
            Actor neighbor = getGrid().get(next);
            move();
            if (neighbor instanceof Tile)
            	((Tile)neighbor).act(this);
        }
    	if (this.getLocation().getCol() == 35){
			JOptionPane.showMessageDialog(null, "You win! \n"
					+ "Number of Deaths: " + deathCount);
			System.exit(0);
		}
    	scrolling();
    }

    public void move()
    {
    	Grid<Actor> gr = getGrid();
        if (gr == null)
            return;
        Location loc = getLocation();
        Location next = loc.getAdjacentLocation(getDirection());
        if (gr.isValid(next))
            moveTo(next);
        else
            removeSelfFromGrid();
    }

    public boolean canMove()
    {
    	Grid<Actor> gr = getGrid();
        if (gr == null)
            return false;
        Location loc = getLocation();
        Location next = loc.getAdjacentLocation(getDirection());
        if (!gr.isValid(next) || next.getRow() < 2 || next.getRow() > 5
        		|| next.getCol() > 35)
            return false;
        Actor neighbor = gr.get(next);
        return (!(neighbor instanceof RedTile));
    }
    
    public void actionToPerform(String description)
	{
		if(description.equals("UP")){
			setDirection(0);
			act();
		}
		else if(description.equals("DOWN")){
			setDirection(180);
			act();
		}
		else if(description.equals("LEFT")){
			setDirection(270);
			act();
		}
		else if(description.equals("RIGHT")){
			setDirection(90);
			act();
		}
	}
}
