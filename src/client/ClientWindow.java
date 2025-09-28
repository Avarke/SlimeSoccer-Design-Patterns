package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class ClientWindow extends JFrame
{
	SlimeSoccer slimesoccerclient;
	ClientPanel panel;

    ClientWindow(SlimeSoccer temp) {
        this.slimesoccerclient = temp;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(false);

        panel = new ClientPanel(slimesoccerclient);
        getContentPane().add(panel);

        panel.setFocusable(true);
        panel.requestFocus();

        // Use GameData singleton
        GameData gameData = GameData.getInstance();

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT -> gameData.setRightPressed(true);
                    case KeyEvent.VK_LEFT  -> gameData.setLeftPressed(true);
                    case KeyEvent.VK_UP    -> gameData.setUpPressed(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT -> gameData.setRightPressed(false);
                    case KeyEvent.VK_LEFT  -> gameData.setLeftPressed(false);
                    case KeyEvent.VK_UP    -> gameData.setUpPressed(false);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // not used
            }
        });

        pack();
        setVisible(true);
    }

}