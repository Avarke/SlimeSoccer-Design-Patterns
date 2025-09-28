package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Text 
{
	private double x, y;
	private int fontSize;
	private String content;
	private Color color;
	private String font;
	
	public Text(String content, double x, double y, int fontSize, Color color, String font) {
		this.x = x;
		this.y = y;
		this.fontSize = fontSize;
		this.content = content;
		this.color = color;
		this.font = font;
	}
	
	public void drawString(Graphics g) {
		g.setFont(new Font(font, Font.PLAIN, fontSize));
		g.setColor(color);
		g.drawString(content, (int)x, (int)y);
	}
}