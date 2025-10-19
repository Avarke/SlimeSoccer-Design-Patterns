package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import java.awt.Color;
import client.command.CommandInvoker;
import client.command.InputAction;
import client.command.PressCommand;
import client.command.ReleaseCommand;
import client.audio.SoundManager;
import client.audio.SoundManager.Sfx;

public class ClientWindow extends JFrame
{
    SlimeSoccer slimesoccerclient;
    ClientPanel panel;
    private final CommandInvoker commandInvoker;

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
        commandInvoker = new CommandInvoker(gameData);
        commandInvoker.register(KeyEvent.VK_RIGHT, new PressCommand(InputAction.MOVE_RIGHT), new ReleaseCommand(InputAction.MOVE_RIGHT));
        commandInvoker.register(KeyEvent.VK_LEFT, new PressCommand(InputAction.MOVE_LEFT), new ReleaseCommand(InputAction.MOVE_LEFT));
        commandInvoker.register(KeyEvent.VK_UP, new PressCommand(InputAction.JUMP), new ReleaseCommand(InputAction.JUMP));

        gameData.addObserver(panel);
        gameData.addObserver(new ClientAudioObserver()); // add audio observer

        SoundManager.load();

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                commandInvoker.onKeyPressed(e.getKeyCode());
                // Play jump sound once per press
                if (e.getKeyCode() == KeyEvent.VK_UP && !jumpHeld) {
                    SoundManager.play(Sfx.JUMP);
                    jumpHeld = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                commandInvoker.onKeyReleased(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    jumpHeld = false;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) { }
        });

        pack();
        setVisible(true);
        panel.requestFocusInWindow();
    }
}
