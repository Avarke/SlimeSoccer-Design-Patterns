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
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        definitelynotslimesoccer.draw(g);
    }

    // Observer callback — repaint on EDT
    @Override
    public void onGameDataChanged(GameData data) {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint();
        } else {
            SwingUtilities.invokeLater(this::repaint);
        }
    }
}