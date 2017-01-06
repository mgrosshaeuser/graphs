package de.grosshaeuser.alg4.graph;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.NoSuchElementException;



public class Graph{
	
	private ArrayList<Node> nodes;
	private ArrayList<Edge> edges;
	
	
	
	public Graph(){
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
	}
		

	

	
	
	public boolean isEmpty(){
		return nodes.size() == 0;
	}
	
	

	
	/***********************************/
	/*****     NODE OPERATIONS     *****/
	/***********************************/
	
	public ArrayList<Node> getNodes()		{		return nodes;					}
	public int getNumberOfNodes()			{		return nodes.size();			}
	public boolean containsNode(Node node)	{		return nodes.contains(node);	}

	
	public void addNode(Node node){
		if ( ! nodes.contains(node)){
			nodes.add(node);
		}
	}
	
	
	public void removeNode(Node node){
		ArrayList<Edge> toBeDeleted = new ArrayList<>();
		for (Edge e : edges){
			if (e.getA() == node    ||    e.getB() == node){
				e.getA().dropNeighbor(node);
				e.getB().dropNeighbor(node);
				toBeDeleted.add(e);
			}
		}
		edges.removeAll(toBeDeleted);
		nodes.remove(node);
	}
	
	
	public ArrayList<Node> getNeighbors (Node node){
		if (nodes.contains(node)){
			ArrayList<Node> neighbors = new ArrayList<>();
			for (Edge e : edges){
				if (e.getA() == node){
					neighbors.add(e.getA());
				}
			}
			return neighbors;
		} else {
			throw new NoSuchElementException("Node isn't part of this graph");
		}
	}



	
	
	
	
	/***********************************/
	/*****     EDGE OPERATIONS     *****/
	/***********************************/

	
	public ArrayList<Edge> getEdges()			{		return edges;						}
	public int getNumberOfEdges()				{		return edges.size();				}
	public boolean containsEdge(Edge edge)		{		return edges.contains(edge);		}
	public boolean containsEdge(Node a, Node b)	{		return getEdgeLength(a,b) != null;	}
	
	
	
	
	public void addEdge(Edge edge){
		if (nodes.contains(edge.getA())   &&   nodes.contains(edge.getB())){
			edges.add(edge);
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
	public void addEdge(Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			addDirectedEdge(a, b);
			addDirectedEdge(b, a);
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
	
	public void addDirectedEdge(Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			double length = calcEdgeLength(a, b);
			edges.add(new Edge(a, b, length));
			a.addNeighbor(b);
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
		
	public void removeEdge (Edge edge){
		if (edges.contains(edge)){
			edges.remove(edge);
		} else {
			throw new NoSuchElementException("Edge is not part of this Graph");
		}
	}
		
	
	public void removeEdge (Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			ArrayList<Edge> toBeRemoved = new ArrayList<>();
			for (Edge e : edges){
				if ( ((e.getA() == a)   &&   (e.getB() == b))   ||   ((e.getA() == b)   &&   (e.getB() == a)) ){
					e.getA().dropNeighbor(b);
					e.getB().dropNeighbor(a);
					toBeRemoved.add(e);
				}
			}
			edges.removeAll(toBeRemoved);
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}

	
	
	
	public void setEdgeHighlighted (Node a, Node b, boolean bool){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			for (Edge e : edges){
				if ( ((e.getA() == a)   &&   (e.getB() == b))   ||   ((e.getA() == b)   &&   (e.getB() == a)) ){
					e.setHighlighted(bool);
				}
			}
		} else {
			throw new NoSuchElementException("At least one node missing");

		}
	}

	
	public boolean isEdgeHighlighted (Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			for (Edge e : edges){
				if ( ((e.getA() == a)   &&   (e.getB() == b))   ||   ((e.getA() == b)   &&   (e.getB() == a)) ){
					return e.isHighlighted();
				}
			}
			throw new NoSuchElementException("No known Edge between given nodes");
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
	
	
	public Double getEdgeLength(Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			for (Edge e : edges){
				if ( ((e.getA() == a)   &&   (e.getB() == b))   ||   ((e.getA() == b)   &&   (e.getB() == a)) ){
					return e.getLength();
				}
			}
			return null;
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
	public Double getNodeDistance(Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			return calcEdgeLength(a, b);
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
	
	
	
	
	public Edge getDirectedEdge(Node a, Node b){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			for (Edge e : edges){
				if ( ((e.getA() == a)   &&   (e.getB() == b)) ) {
					return e;
				}
			}
		}
		return null;
	}
	
	
	public ArrayList<Edge> getDirectedEdgesTo (Node a){
		ArrayList<Edge> result = new ArrayList<>();
		if (nodes.contains(a)){
			for (Edge e : edges){
				if (e.getB() == a){
					result.add(e);
				}
			}
		} 
		return result;
	}

	
	
	
	private double calcEdgeLength(Node a, Node b){
		int colDiff = Math.abs(a.getColVal() - b.getColVal());
		int rowDiff = Math.abs(a.getRowVal() - b.getRowVal());
		return (Math.sqrt((colDiff*colDiff) + (rowDiff*rowDiff))) / VisualGraph.PIXEL_PER_STEP;
	}
	
	
	
	
	public void drawEdge(Graphics2D g2d, double scale, Node a, Node b, String text){
		if (nodes.contains(a)   &&   nodes.contains(b)){
			Edge e1 = null;
			Edge e2 = null;
			for (Edge e : edges){
				if (e.getA() == a   &&   e.getB() == b){
					e1 = e;
				}
				if (e.getA() == b   &&   e.getB() == a){
					e2 = e;
				}
			}
			if (e1 != null   &&   e2 != null){
				e1.drawEdge(g2d, scale, text);
				e2.drawEdge(g2d, scale, "");
			} else {
				throw new NoSuchElementException("No known edge between given nodes");
			}
		} else {
			throw new NoSuchElementException("At least one node missing");
		}
	}
		
}
