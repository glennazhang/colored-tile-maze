import java.awt.Color;

public class YellowTile extends Tile {
	public YellowTile(){
		setColor(Color.yellow);
	}
	
	public void act(Player player){
		player.dies("You were electrocuted!");
	}
}
