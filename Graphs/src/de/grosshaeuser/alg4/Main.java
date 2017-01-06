package de.grosshaeuser.alg4;

import javax.swing.JFrame;

import de.grosshaeuser.alg4.graph.VisualGraph;

public class Main {
	public static final int WINDOW_WIDTH = 1160;
	public static final int WINDOW_HEIGHT =960;
	
	private static JFrame frame;
	private static VisualGraph vGraph;

	public static void main(String[] args) {
		frame = new JFrame("Algorithmik Praktikum - AT 4");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		vGraph = new VisualGraph();
		frame.getContentPane().add(vGraph);
		vGraph.setFocusable(true);
	}
		
}
