import info.gridworld.actor.Actor;
import info.gridworld.actor.Critter;
import info.gridworld.grid.Location;
import java.util.ArrayList;

public class BlusterCritter extends Critter{

	private int courage;

	public BlusterCritter(int c){
		courage = c;
	}

	public int getCourage(){
		return courage;
	}

	public ArrayList<Actor> getActors(){
		ArrayList<Actor> list = new ArrayList<Actor>();
		Location loc = getLocation();
		for (int i = loc.getRow()-2; i <= loc.getRow()+2; i++){
			for (int j = loc.getCol()-2; j <= loc.getCol()+2; j++){
				Location temp = new Location(i,j);
				if(getGrid().isValid(temp) && getGrid().get(temp) != this 
						&& getGrid().get(temp) != null)
					list.add(getGrid().get(temp));
			}
		}
		return list;
	}

	public void processActors(ArrayList<Actor> actors){
		int count = 0;
		for (Actor a : actors){
			if (a instanceof Critter)
				count++;
		}
		if (count < courage)
			setColor(getColor().brighter());
		else
			setColor(getColor().darker());
	}
}
