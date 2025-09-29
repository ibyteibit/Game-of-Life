import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class GameOfLife {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JFrame frame = new JFrame("Game of Life");
		
		frame.setSize(1200, 800);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().setBackground(Color.black);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		Simulation sim = new Simulation(new Dimension(1200, 800), new Dimension(1000, 1000));
		
		frame.getContentPane().add(sim);
		
		frame.setVisible(true);
	

	}
	
	

}
