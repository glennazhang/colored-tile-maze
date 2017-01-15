import java.awt.Color;

public class OrangeTile extends Tile {
	
	public OrangeTile(){
		setColor(Color.orange);
	}
	
	public void act(Player player){
		player.setFlavor("orange");		
	}
}
