package client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.awt.Color;
import client.audio.SoundManager;
import client.audio.SoundManager.Sfx;

public class ClientWindow extends JFrame
{
    SlimeSoccer slimesoccerclient;
    ClientPanel panel;
    private boolean jumpHeld = false;

    ClientWindow(SlimeSoccer temp) {
        this.slimesoccerclient = temp;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFocusable(false);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);

        panel = new ClientPanel(slimesoccerclient);
        panel.setBackground(Color.BLACK);
        getContentPane().add(panel);

        panel.setFocusable(true);
        panel.requestFocus();

        GameData gameData = GameData.getInstance();

        gameData.addObserver(panel);
        gameData.addObserver(new ClientAudioObserver()); // add audio observer

        SoundManager.load();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        gameData.setRightPressed(true);
                        break;
                    case KeyEvent.VK_LEFT:
                        gameData.setLeftPressed(true);
                        break;
                    case KeyEvent.VK_UP:
                        gameData.setUpPressed(true);
                        break;
                    default:
                        break;
                }

                if (e.getKeyCode() == KeyEvent.VK_UP && !jumpHeld) {
                    SoundManager.play(Sfx.JUMP);
                    jumpHeld = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        gameData.setRightPressed(false);
                        break;
                    case KeyEvent.VK_LEFT:
                        gameData.setLeftPressed(false);
                        break;
                    case KeyEvent.VK_UP:
                        gameData.setUpPressed(false);
                        break;
                    default:
                        break;
                }

                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    jumpHeld = false;
                }
            }
        });

        pack();
        setVisible(true);
        panel.requestFocusInWindow();
    }
}
