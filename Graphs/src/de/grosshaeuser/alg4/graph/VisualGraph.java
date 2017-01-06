package de.grosshaeuser.alg4.graph;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.grosshaeuser.alg4.Main;
import de.grosshaeuser.alg4.algorithms.AStarAlgorithm;
import de.grosshaeuser.alg4.algorithms.BellmanFord;
import de.grosshaeuser.alg4.algorithms.DijkstraAlgorithm;
import de.grosshaeuser.alg4.algorithms.PrimAlgorithm;



public class VisualGraph extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public static final int SCALE = 1;
	public static final double SUBWINDOW_SCALE = 0.8; 
	
	public static final int SIDE_OFFSET = 30;
	public static final int COORD_STEPSIZE = (Main.WINDOW_WIDTH - 2*SIDE_OFFSET)/11;
	public static final int NODE_RADIUS = 20;
	public static final int PIXEL_PER_STEP = 10;
	
	private static final Color SHADOW = new Color(0,0,0,120);
	private static final Color CLEAR = new Color (0,0,0,0);
	
	
	private Color baselayer;

	private ArrayList<Node> selectedNodes;
	private ArrayList<String> nodeBuffer;
	private ArrayList<String> edgeBuffer;
	
	private Graph graph;
	private JFrame overlayFrame;
	private VisualGraph self;
	
	
	
	public VisualGraph(){
		baselayer = CLEAR;
		selectedNodes = new ArrayList<>();
		nodeBuffer = new ArrayList<>();
		edgeBuffer = new ArrayList<>();
				
		graph= new Graph();
		readDataFromFile();
		readDataFromBuffers();
		
		overlayFrame = null;
		self = this;
				
		addMouseListener(new NodeSelectionMouseListener());
		addKeyListener(new KeyCommandListener());
	}
	
	
	
	private void resetBaseframe(){
		graph = new Graph();
		readDataFromBuffers();
		selectedNodes = new ArrayList<>();
		repaint();
	}
	
	
	private void readDataFromFile(){
		BufferedReader br = null;
		String line = "";
		
		try {
			br = new BufferedReader(new FileReader(new File(getClass().getResource("nodes.csv").getFile())));
			while ((line = br.readLine()) != null){
				nodeBuffer.add(line);
			}
		} catch (FileNotFoundException e1){			
		} catch (IOException e2) {
		}
		
		try {
			br = new BufferedReader (new FileReader(new File(getClass().getResource("edges.csv").getFile())));
			while ((line = br.readLine()) != null){
				edgeBuffer.add(line);
			}
		} catch (FileNotFoundException e1) {
		} catch (IOException e2) {
		}
	}
	
	
	private void readDataFromBuffers(){
		String splitBy = ", ";
		
		for (String line : nodeBuffer){
			String[] node = line.split(splitBy);
			char col = node[0].charAt(0);
			int row = ((int) node[0].charAt(1)) - '0';
			graph.addNode(new Node(col, row, 0));
		}
		
		for (String line : edgeBuffer){
			String[] edge = line.split(splitBy);
			char colA = edge[0].charAt(0);
			int rowA = ((int) edge[0].charAt(1)) - '0';
			char colB = edge[1].charAt(0);
			int rowB = ((int) edge[1].charAt(1)) - '0';
			Node nodeA = null;
			Node nodeB = null;
			for (Node n : graph.getNodes()){
				if (n.getCol() == colA   &&   n.getRow() == rowA){
					nodeA = n;
				}
				if (n.getCol() == colB   &&   n.getRow() == rowB){
					nodeB = n;
				}
			}
			graph.addEdge(nodeA, nodeB);
		}
	}
	

	
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		drawCoordinateSystem(g2d);
	
		for (Edge e : graph.getEdges()){
			e.drawEdge(g2d, SCALE, null);
		}
		
		for (Node n : graph.getNodes()){
			n.drawNode(g2d);
		}
		g2d.setColor(baselayer);
		g2d.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
	}
	
	
	private void drawCoordinateSystem(Graphics2D g2d){
		int startCol = SIDE_OFFSET;
		int startRow = SIDE_OFFSET;
		
		for (int col = startCol ; col <= Main.WINDOW_WIDTH ; col += COORD_STEPSIZE){
			g2d.setColor(Color.BLACK);
			String coord = ""+((col-SIDE_OFFSET) / PIXEL_PER_STEP);
			g2d.drawString(coord, col+5, 15);
			
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawLine(col, 0, col, Main.WINDOW_HEIGHT);
		}
		for (int row = startRow ; row <= Main.WINDOW_HEIGHT ; row += COORD_STEPSIZE){
			g2d.setColor(Color.BLACK);
			String coord = ""+((row - SIDE_OFFSET) / PIXEL_PER_STEP);
			g2d.drawString(coord, 5, row-5);
			
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.drawLine(0, row, Main.WINDOW_WIDTH, row);
		}
	}
	

	
	
	
	private class NodeSelectionMouseListener extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			
			if (baselayer != CLEAR){
				baselayer = CLEAR;
				resetBaseframe();
				overlayFrame.dispose();
				return;
			}
			
			if (e.getButton() == MouseEvent.BUTTON1){
				if (! selectNode(e)){
					insertOrRepositionNode(e);
				}
				repaint();
			}
			
			
			if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON3){
				if ( ! findNodeForDeletion(e) ) {
					selectOperation();
				}
			}
		}

		
		private boolean selectNode(MouseEvent e) {
			boolean hit = false;
			int xPos = e.getX();
			int yPos = e.getY();
			for (Node n : graph.getNodes()){
				int[] colRange = {n.getColVal() - NODE_RADIUS, n.getColVal() + NODE_RADIUS};
				int[] rowRange = {n.getRowVal() - NODE_RADIUS, n.getRowVal() + NODE_RADIUS};
				
				if ( (xPos > colRange[0])   &&   (xPos < colRange[1])   &&   (yPos > rowRange[0])   &   (yPos < rowRange[1]) ){
					if ( ! n.isHighlighted()){
						if (selectedNodes.size() < 2){
							n.setHighlighted(true);
							selectedNodes.add(n);
						} 
					} else {
						n.setHighlighted(false);
						selectedNodes.remove(n);
					}
					hit = true;
				}
			}
			return hit;
		}


		private void insertOrRepositionNode(MouseEvent e) {
			int x = e.getX() / COORD_STEPSIZE;
			int y = e.getY() / COORD_STEPSIZE;
			x = (e.getX() % COORD_STEPSIZE > (COORD_STEPSIZE / 2)) ? (x+1) : (x);
			y = (e.getY() % COORD_STEPSIZE > (COORD_STEPSIZE / 2)) ? (y+1) : (y);
			char col = (char) (x + 'A' -1);
			int row = y;
			Node run = null;
			for (Node n : graph.getNodes()){
				if (n.getCol() == col   &&   n.getRow() == row){
					run = n;
				}
			}
		
			if (run == null   &&   selectedNodes.isEmpty()){
				graph.addNode(new Node((char)(x+'A'-1),y, 0));
			}
			
			if (run == null   && selectedNodes.size() == 1){
				selectedNodes.get(0).setCol(col);
				selectedNodes.get(0).setRow(row);
				selectedNodes.get(0).setHighlighted(false);
				selectedNodes = new ArrayList<>();
			}
		}


		private boolean findNodeForDeletion(MouseEvent e) {
			boolean found = false;
			for (Node n : graph.getNodes()){
				int[] colRange = {n.getColVal() - NODE_RADIUS, n.getColVal() + NODE_RADIUS};
				int[] rowRange = {n.getRowVal() - NODE_RADIUS, n.getRowVal() + NODE_RADIUS};
				
				if ( (e.getX() > colRange[0])   &&   (e.getX() < colRange[1])   &&   (e.getY() > rowRange[0])   &   (e.getY() < rowRange[1]) ){
					n.setMarkedForDeletion(! n.isMarkedForDeletion() );
					found = true;
					repaint();
				}
			}
			return found;
		}


		private void selectOperation() {
			String operation = "";
			if (selectedNodes.size() == 1){
				Object[] operations = {"Minimum Cost Spanning Tree", "Dijkstra Shortest Path", "Bellman-Ford"};
				operation = (String)JOptionPane.showInputDialog(self, "Bitte Operation auswählen:", "Operationen", 
						JOptionPane.PLAIN_MESSAGE, null, operations, operations[0]);	
			} else if (selectedNodes.size() == 2){
				Object[] operations = {"A* Shortest Path"};
				operation = (String)JOptionPane.showInputDialog(self, "Bitte Operation auswählen:", "Operationen", 
						JOptionPane.PLAIN_MESSAGE, null, operations, operations[0]);
			} else {
				return;
			}
						
			if (operation == null){
				return;
			}
			
			backUpCurrentGraph();
			createOverlay(operation);
			baselayer = SHADOW;
			repaint();
		}

		
		private void backUpCurrentGraph(){
			nodeBuffer = new ArrayList<>();
			edgeBuffer = new ArrayList<>();
			for (Node node : graph.getNodes()){
				int tempCol = (node.getColVal() - SIDE_OFFSET) / PIXEL_PER_STEP;
				int tempRow = (node.getRowVal() - SIDE_OFFSET) / PIXEL_PER_STEP;
				String line = "" + node.getCol() + node.getRow() + ", " + tempCol + ", " + tempRow;
				nodeBuffer.add(line);
			}
			
			for (Edge edge : graph.getEdges()){
				Node a = edge.getA();
				Node b = edge.getB();
				String line = "" + a.getCol() + a.getRow() + ", " + b.getCol() + b.getRow();
				edgeBuffer.add(line);
			}
		}
		
		
		private void createOverlay(String operation) {
			overlayFrame = new JFrame();
			int width = (int) Math.round(Main.WINDOW_WIDTH * SUBWINDOW_SCALE);
			int height = (int) Math.round(Main.WINDOW_HEIGHT * SUBWINDOW_SCALE);
			overlayFrame.setSize(width, height);
			overlayFrame.setResizable(false);
			JFrame.setDefaultLookAndFeelDecorated(false);
			overlayFrame.setLocationRelativeTo(self);
			overlayFrame.setUndecorated(true);
			overlayFrame.setFocusable(true);
			overlayFrame.setVisible(true);
				
			applyAlgoToOverlay(operation);
		}


		private void applyAlgoToOverlay(String operation) {
			if (operation == "Minimum Cost Spanning Tree"){
				overlayFrame.setTitle("Minimum Cost Spanning Tree - Prim Algorithmus");
				PrimAlgorithm prim = new PrimAlgorithm(graph, selectedNodes.get(0));
				prim.addMouseListener(new RestoreOnClick());
				overlayFrame.getContentPane().add(prim);
			}
			
			if (operation == "Dijkstra Shortest Path"){
				overlayFrame.setTitle("Single Source Shortest Path - Dijkstra Algorithm");
				DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph, selectedNodes.get(0));
				dijkstra.addMouseListener(new RestoreOnClick());
				overlayFrame.getContentPane().add(dijkstra);
			}
			
			if (operation == "A* Shortest Path"){
				overlayFrame.setTitle("Single Source Shortest Path - A* Algorithm");
				AStarAlgorithm aStar = new AStarAlgorithm(graph, selectedNodes.get(0), selectedNodes.get(1));
				aStar.addMouseListener(new RestoreOnClick());
				overlayFrame.getContentPane().add(aStar);
			}
			
			if (operation == "Bellman-Ford"){
				overlayFrame.setTitle("Bellman-Ford Algorithm");
				BellmanFord bellmanFord = new BellmanFord(graph, selectedNodes.get(0));
				bellmanFord.addMouseListener(new RestoreOnClick());
				overlayFrame.getContentPane().add(bellmanFord);
			}
		}
	}
	
	
	
	
	
	private class KeyCommandListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			super.keyPressed(e);
			
			if (e.getKeyCode() == KeyEvent.VK_DELETE){
				deleteSelectedNodes();
			}
			
			if (e.getKeyCode() == KeyEvent.VK_R){
				nodeBuffer = new ArrayList<>();
				edgeBuffer = new ArrayList<>();
				readDataFromFile();
				resetBaseframe();
			}
			
			if (e.getKeyCode() == KeyEvent.VK_E){
				addOrRemoveEdge();
			}
		}

		
		private void deleteSelectedNodes() {
			ArrayList<Node> toBeRemoved = new ArrayList<>();
			for (Node n : graph.getNodes()){
				n.setHighlighted(false);
				if (n.isMarkedForDeletion()){
					toBeRemoved.add(n);
				}
			}
			for (Node n : toBeRemoved){
				graph.removeNode(n);
			}
			selectedNodes = new ArrayList<>();
			repaint();
		}


		private void addOrRemoveEdge() {
			if (selectedNodes.size() == 2){
				if (graph.getEdgeLength(selectedNodes.get(0), selectedNodes.get(1)) != null){
					graph.removeEdge(selectedNodes.get(0), selectedNodes.get(1));
				} else {
					graph.addEdge(selectedNodes.get(0), selectedNodes.get(1));
				}
				selectedNodes.get(0).setHighlighted(false);
				selectedNodes.get(1).setHighlighted(false);
				selectedNodes = new ArrayList<>();
				repaint();
			}
		}
	}
	
	
	
	
	
	private class RestoreOnClick extends MouseAdapter{
		
		@Override
		public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			baselayer = CLEAR;
			overlayFrame.dispose();
			resetBaseframe();
		}
	}
}
