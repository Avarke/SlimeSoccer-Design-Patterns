package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientPanel extends JPanel implements GameObserver
{
    private static final long serialVersionUID = 7042280602681239925L;
    SlimeSoccer definitelynotslimesoccer;

    final int SCREENWIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    final int SCREENHEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    final int PANELWIDTH = SCREENWIDTH/2;
    final int PANELHEIGHT = SCREENHEIGHT;

    ClientPanel(SlimeSoccer temp)
    {
        definitelynotslimesoccer = temp;
        setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
        // coalesce paints to ~60 FPS to avoid repaint storms
        new javax.swing.Timer(16, e -> repaint()).start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        definitelynotslimesoccer.draw(g);
    }

    // Repaint is timer-driven; avoid per-packet repaint
    @Override
    public void onGameDataChanged(GameData data) { }


}