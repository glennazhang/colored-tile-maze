import java.awt.Color;

public class PurpleTile extends Tile {
	
	public PurpleTile(){
		setColor(new Color(138,43,226));
	}
	
	public void act(Player player){
		player.setFlavor("lemon");
		player.act();
	}
}
