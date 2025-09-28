package server;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Arc 
{
	private double x, y;
	
	public Arc(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(10));
		g.drawArc((int)x, (int)y, 50, 50, 10, 180);
	}
}
