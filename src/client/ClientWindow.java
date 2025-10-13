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

        // Match server window behavior
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(false);

        panel = new ClientPanel(slimesoccerclient);
        panel.setBackground(new java.awt.Color(8,16,64)); // non-white fallback
        getContentPane().add(panel);

        panel.setFocusable(true);
        panel.requestFocus();

        GameData gameData = GameData.getInstance();

        // Observers
        gameData.addObserver(panel);
        gameData.addObserver(new SfxController());

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT: gameData.setRightPressed(true); break;
                    case KeyEvent.VK_LEFT:  gameData.setLeftPressed(true);  break;
                    case KeyEvent.VK_UP:
                        if (!gameData.isUpPressed()) {
                            SoundManager.play("jump.wav");
                        }
                        gameData.setUpPressed(true);
                        break;
                }
            }
            @Override public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT: gameData.setRightPressed(false); break;
                    case KeyEvent.VK_LEFT:  gameData.setLeftPressed(false);  break;
                    case KeyEvent.VK_UP:    gameData.setUpPressed(false);    break;
                }
            }
            @Override public void keyTyped(KeyEvent e) {}
        });

        pack();
        setVisible(true);
    }

}