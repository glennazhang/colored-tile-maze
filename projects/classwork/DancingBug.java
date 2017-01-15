import info.gridworld.actor.Bug;

public class DancingBug extends Bug
{
    private int[] turnArray;
    private int index;

    public DancingBug(int[] array)
    {
        turnArray = array;
        index = 0;
    }
    
    public void act()
    {
        if (index == turnArray.length)
        	index = 0;
        if (index < turnArray.length){
        	turnBug(turnArray[index]);
        	index++;
        }
        super.act();
    }
    
    public void turnBug(int turns){
    	for (int a = 0; a < turns; a++){
    		turn();
    	}
    }
}
