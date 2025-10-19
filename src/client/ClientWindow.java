package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import client.command.CommandInvoker;
import client.command.InputAction;
import client.command.PressCommand;
import client.command.ReleaseCommand;

public class ClientWindow extends JFrame
{
    SlimeSoccer slimesoccerclient;
    ClientPanel panel;
    private final CommandInvoker commandInvoker;

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
        commandInvoker = new CommandInvoker(gameData);
        commandInvoker.register(KeyEvent.VK_RIGHT, new PressCommand(InputAction.MOVE_RIGHT), new ReleaseCommand(InputAction.MOVE_RIGHT));
        commandInvoker.register(KeyEvent.VK_LEFT, new PressCommand(InputAction.MOVE_LEFT), new ReleaseCommand(InputAction.MOVE_LEFT));
        commandInvoker.register(KeyEvent.VK_UP, new PressCommand(InputAction.JUMP), new ReleaseCommand(InputAction.JUMP));

        // Register observer
        gameData.addObserver(panel);

        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                commandInvoker.onKeyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                commandInvoker.onKeyReleased(e.getKeyCode());
            }

            @Override
            public void keyTyped(KeyEvent e) { }
        });

        pack();
        setVisible(true);
    }
}
