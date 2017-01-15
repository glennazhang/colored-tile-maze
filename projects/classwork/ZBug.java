import info.gridworld.actor.Bug;

public class ZBug extends Bug
{
    private int steps;
    private int sideLength;
    private int turnNumber;

    public ZBug(int length)
    {
        steps = 0;
        sideLength = length;
        turnNumber = 0;
        setDirection(90);
    }
    
    public void act()
    {
        if (steps < sideLength && canMove())
        {
            move();
            steps++;
        }
        else if (steps == sideLength)
        {
            if (turnNumber == 0){
            	turnBug(3);
            	turnNumber++;
            }
            else if(turnNumber == 1){
            	turnBug(5);
            	turnNumber++;
            }
            else if (turnNumber == 2){
            	sideLength = 0;
            }
        	steps = 0;
        }
    }
    
    public void turnBug(int turns){
    	for (int a = 0; a < turns; a++){
    		turn();
    	}
    }
}
