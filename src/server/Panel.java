package server;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;

public class Panel extends JPanel
{
	private static final long serialVersionUID = 7042280602681239925L;
	SlimeSoccer definitelynotslimesoccer;
	
	final int SCREENWIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	final int SCREENHEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	final int PANELWIDTH = SCREENWIDTH/2;
	final int PANELHEIGHT = SCREENHEIGHT;
	
	Panel(SlimeSoccer temp) {
		definitelynotslimesoccer = temp;
		setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
	}
	
	public void paintComponent(Graphics g)
	{
		definitelynotslimesoccer.draw(g);
	}
}