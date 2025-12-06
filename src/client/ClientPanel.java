package client;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

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
        
        // Add mouse wheel listener for chat scrolling
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                GameData gameData = GameData.getInstance();
                int rotation = e.getWheelRotation();
                
                if (rotation < 0) {
                    // Scroll up (older messages)
                    gameData.scrollChatUp(1);
                } else if (rotation > 0) {
                    // Scroll down (newer messages)
                    gameData.scrollChatDown(1);
                }
            }
        });
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