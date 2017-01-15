import java.awt.Color;

import javax.swing.JOptionPane;

public class GreenTile extends Tile {
	
	private String question;
	private int answer;
	
	public GreenTile() {
		setColor(Color.green);
	}
	
	public GreenTile(String q, int a){
		question = splitQuestion(q);
		answer = a;
		setColor(Color.green);
	}
	
	public String splitQuestion(String q){
		String[] data = q.split(";");
		String question = "";
		for (int i = 0; i < data.length; i++){
			question += data[i] + "\n";
		}
		return question;
	}
	
	public void act(Player player){
		String[] choices = {"A", "B", "C", "D"};
        int choice = JOptionPane.showOptionDialog
                (null, question, "Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, null);
        if (choice != answer)
            player.dies("A pile of homework crashed onto you, killing you instantly!");
        else JOptionPane.showMessageDialog(null, "You are correct!");
	}
}
