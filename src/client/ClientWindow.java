package client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.awt.Color;
import client.audio.SoundManager;
import client.audio.SoundManager.Sfx;
import client.command.InputCommand;
import client.command.JumpCommand;
import client.command.MoveLeftCommand;
import client.command.MoveRightCommand;

public class ClientWindow extends JFrame
{
    SlimeSoccer slimesoccerclient;
    ClientPanel panel;
    private boolean jumpHeld = false;

    ClientWindow(SlimeSoccer temp) {
        this.slimesoccerclient = temp;

        new MoveLeftCommand();
        new MoveRightCommand();
        new JumpCommand();

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
                InputCommand cmd = InputCommand.getCommandForKey(e.getKeyCode());
                if (cmd != null) cmd.execute(gameData);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                InputCommand cmd = InputCommand.getCommandForKey(e.getKeyCode());
                if (cmd != null) cmd.undo(gameData);
            }
        });

        pack();
        setVisible(true);
        panel.requestFocusInWindow();
    }
}
