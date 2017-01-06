package de.grosshaeuser.alg4.algorithms;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.grosshaeuser.alg4.FibonacciHeap;
import de.grosshaeuser.alg4.Main;
import de.grosshaeuser.alg4.graph.Edge;
import de.grosshaeuser.alg4.graph.Graph;
import de.grosshaeuser.alg4.graph.Node;
import de.grosshaeuser.alg4.graph.VisualGraph;

public class AStarAlgorithm extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static final double SCALE = VisualGraph.SUBWINDOW_SCALE;


	private Graph graph;
	private ArrayList<Node> log;
	private ArrayList<Edge> routeEdges;
	private ArrayList<Node> routeNodes;

	
	
	public  AStarAlgorithm(Graph graph, Node startNode, Node endNode){
		this.graph = graph;
		this.log = new ArrayList<>();
		this.routeEdges = new ArrayList<>();
		this.routeNodes = new ArrayList<>();
		
		if (aStar(graph, startNode, endNode)){
			constructRoute(startNode, endNode);
		}
	}	

	
	
	private boolean aStar(Graph graph, Node startNode, Node endNode){
		FibonacciHeap openList = new FibonacciHeap();
		ArrayList<Node>closedList = new ArrayList<>();
		
		for (Node n : graph.getNodes()){
			n.setKey(Double.POSITIVE_INFINITY);
		}
		
		startNode.setKey(0.0);
		openList.insert(startNode);
		log.add(startNode);
		
		while ( ! openList.isEmpty() ){
			Node u = openList.extractMin();
			log.remove(u);
			
			if (u == endNode){
				return true;
			}
			closedList.add(u);
			expandNode (graph, closedList, openList, u, startNode);
		}
		return false;
	}
	
	private void expandNode(Graph graph, ArrayList<Node> closedList, FibonacciHeap openList, Node u, Node startNode){
		for (Node v : u.getNeighbors()){
			if (closedList.contains(v) ){
				continue;
			}
			double g = u.getKey() + graph.getEdgeLength(u, v);
			if (log.contains(v)   &&   g >= v.getKey()){
				continue;
			}
			v.setParent(null);
			double f = g + graph.getNodeDistance(startNode, u);
			v.setKey(f);
			if ( ! log.contains(v) ){
				openList.insert(v);
				log.add(v);
			}
		}
	}
	
	private void constructRoute (Node startNode, Node endNode){
		Node temp = endNode;
		while (temp != startNode   &&   temp.getNeighbors() != null   &&   !routeNodes.contains(temp)){
			Node minNode = temp.getNeighbors().get(0);
			for (Node n : temp.getNeighbors()){
				if (n.getKey() < minNode.getKey()){
					minNode = n;
				}
			}
			minNode.setHighlighted(true);
			double length = graph.getEdgeLength(temp, minNode);
			routeEdges.add(new Edge(temp, minNode, length));
			routeNodes.add(temp);
			temp = minNode;
		}
		for (int i = routeEdges.size() -2   ;   i >= 0   ;   i--){
			double length = routeEdges.get(i).getLength();
			routeEdges.get(i).setLength(length + routeEdges.get(i+1).getLength());
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
		g2d.drawString("Single Source Shortest Path - A* Algorithm", 20, 20);
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
		for (Edge e : graph.getEdges()){
			e.drawEdge(g2d, SCALE, " ");
		}
		
		for (Edge e : routeEdges){
			e.setHighlighted(true);
			e.drawEdge(g2d, SCALE, "");
		}
		
		for (Node n : graph.getNodes()){
			String key = String.format("%4.0f", n.getKey());
			n.drawCustomNode(g2d, SCALE, key);;
		}
	}
	

		
}
