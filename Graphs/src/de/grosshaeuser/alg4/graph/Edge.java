package de.grosshaeuser.alg4.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;

public class Edge {
		private Node a;
		private Node b;
		private double length;
		private int num;
		private boolean highlight;
		

		
		public Edge (Node a, Node b, Double length){
			this.a = a;
			this.b = b;
			this.length = length;
			this.num = 0;
			this.highlight = false;
		}
		
		
		
		public Node getA()				{ 	return this.a;			}
		public Node getB()				{ 	return this.b;			}
		public Double getLength()		{ 	return this.length;		}
		public int getNum()				{	return this.num;		}
		public boolean isHighlighted()	{	return this.highlight;	}
		
		
		public void setLength(Double length)		{	this.length = length;		}
		public void setNum(int num)					{	this.num = num;				}
		public void setHighlighted (boolean bool)	{	this.highlight = bool;		}
		
			
		public void drawEdge(Graphics2D g2d, double scale, String text){
			int colValA = (int) Math.round(a.getColVal() * scale);
			int rowValA = (int) Math.round(a.getRowVal() * scale);
			int colValB = (int) Math.round(b.getColVal() * scale);
			int rowValB = (int) Math.round(b.getRowVal() * scale);
			
			Stroke st = g2d.getStroke();
			if (highlight){
				g2d.setColor(Color.CYAN);
				g2d.setStroke(new BasicStroke(Math.round(15* scale)));
				g2d.drawLine(colValA, rowValA, colValB, rowValB);
			}
			
			g2d.setColor(Color.GRAY);
			g2d.setStroke(new BasicStroke(Math.round(5*scale)));
			g2d.drawLine(colValA, rowValA, colValB, rowValB);
			g2d.setStroke(st);

			int colStart = (colValA < colValB) ? (colValA) : (colValB);
			int rowStart = (rowValA < rowValB) ? (rowValA) : (rowValB);
			int colOffset = (colValA < colValB) ? (colValB - colValA) : (colValA - colValB);
			int rowOffset = (rowValA < rowValB) ? (rowValB - rowValA) : (rowValA - rowValB);
			
			g2d.setColor(Color.BLACK);
			if (text == null){
				g2d.setFont(new Font("TimesRoman", Font.BOLD, 12));
				g2d.drawString(String.format("%4.2f", length), colStart + (colOffset/2)-15, rowStart + (rowOffset/2));
			} else if (text == ""){
				g2d.setFont(new Font("TimesRoman", Font.BOLD, 10));
				g2d.drawString(String.format("%4.1f", length), colStart + (colOffset/2)-15, rowStart + (rowOffset/2));				
			} else {
				g2d.setFont(new Font("TimesRoman", Font.BOLD, 12));
				g2d.drawString(text, colStart + (colOffset/2)-15, rowStart + (rowOffset/2));				
			}
		}
		

}
