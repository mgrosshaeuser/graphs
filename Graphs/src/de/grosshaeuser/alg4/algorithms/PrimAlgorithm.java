package de.grosshaeuser.alg4.algorithms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.grosshaeuser.alg4.FibonacciHeap;
import de.grosshaeuser.alg4.Main;
import de.grosshaeuser.alg4.graph.Edge;
import de.grosshaeuser.alg4.graph.Graph;
import de.grosshaeuser.alg4.graph.Node;
import de.grosshaeuser.alg4.graph.VisualGraph;



public class PrimAlgorithm extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public static final double SCALE = VisualGraph.SUBWINDOW_SCALE;

	private Graph graph;
	private ArrayList<Edge> primEdges;
	private ArrayList<Node> primNodes;
					
	private int mouseWheelCounter;
	
	
	
	public  PrimAlgorithm(Graph graph, Node startNode){
		this.graph = graph;
		this.primEdges = new ArrayList<>();
		this.primNodes = new ArrayList<>();
		this.mouseWheelCounter = 0;
		
		prim(this.graph, startNode);
		this.addMouseWheelListener(new MouseWheelWalkThrough());
	}	

	
	
	
	private void prim(Graph graph, Node startNode){
		FibonacciHeap fib = new FibonacciHeap();
		for (Node n : graph.getNodes()){
			n.setKey(Double.POSITIVE_INFINITY);
			n.setParent(null);
		}
		startNode.setKey(0.0);
		startNode.setParent(null);
		
		for (Node n : graph.getNodes()){
			fib.insert(n);
		}
	
		while (! fib.isEmpty()){
			Node u = fib.extractMin();
			for (Node v : u.getNeighbors()){
				if (!primNodes.contains(v)   &&   (u.getKey() + graph.getEdgeLength(u, v)) < v.getKey()){
					Edge temp = graph.getDirectedEdge(u, v);
					for (Edge e : graph.getDirectedEdgesTo(v)){
						primEdges.remove(e);
					}
					primEdges.add(temp);
										
					fib.decreaseKey(v, graph.getEdgeLength(u, v));
					v.setKey(graph.getEdgeLength(u, v));
					v.setParent(u);
				}
			}
			primNodes.add(u);
		}
		repaint();
	}
	
	

	
	    
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		drawCoordinateSystem(g2d);
		drawElements(g2d);
		g2d.setColor(Color.BLACK);
		g2d.setFont(new Font("TimesRoman", Font.BOLD, 18));
		g2d.drawString("Minimum Cost Spanning Tree - Prim Algorithmus", 20, 20);		
	}
	

	
	private void drawCoordinateSystem(Graphics2D g2d){
			int startCol = (int) Math.round(VisualGraph.SIDE_OFFSET * SCALE);
			int startRow = (int) Math.round(VisualGraph.SIDE_OFFSET * SCALE);
			int windowWidth = (int) Math.round(Main.WINDOW_WIDTH * SCALE);
			int windowHeight = (int) Math.round(Main.WINDOW_HEIGHT * SCALE);;
			int coordStepsize = (int) Math.round(VisualGraph.COORD_STEPSIZE * SCALE);
			
			for (int col = startCol ; col <= windowWidth ; col += coordStepsize){
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(col, 0, col, windowHeight);
			}
			
			for (int row = startRow ; row <= windowHeight ; row += coordStepsize){
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.drawLine(0, row, windowWidth, row);
			}
	}
	

	
	private void drawElements(Graphics2D g2d){
		for (Edge e :graph.getEdges()){
			e.drawEdge(g2d, SCALE, " ");
		}
		
		g2d.setColor(new Color(255,255,255,160));
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		

		for (Edge e : primEdges){
			e.drawEdge(g2d, SCALE, "");
		}
	
		for (Node n : graph.getNodes()){
			String val = String.format("%4.0f", n.getKey());
			n.drawCustomNode(g2d, SCALE, val);
		}
	}
		
	
	
	
	
	private class MouseWheelWalkThrough extends MouseAdapter{
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			super.mouseWheelMoved(e);
			int mod = e.getWheelRotation();
			if (mod == 1   &&   mouseWheelCounter > 0){
				mouseWheelCounter--;
				Edge x = primEdges.get(mouseWheelCounter);
				x.setHighlighted(false);
			}
			if (mod == -1   &&   mouseWheelCounter < primEdges.size()){
				Edge x = primEdges.get(mouseWheelCounter);
				x.setHighlighted(true);
				mouseWheelCounter++;
			}
			repaint();
		}
	}
}
