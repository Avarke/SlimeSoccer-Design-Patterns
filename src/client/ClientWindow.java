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
        panel.setFocusTraversalKeysEnabled(false);
        panel.requestFocus();

        GameData gameData = GameData.getInstance();

        gameData.addObserver(panel);
        gameData.addObserver(new ClientAudioObserver()); // add audio observer

        SoundManager.load();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char ch = e.getKeyChar();
                int code = e.getKeyCode();

                if (gameData.isChatInputActive()) {
                    // ENTER -> send
                    if (code == KeyEvent.VK_ENTER) {
                        gameData.submitChat();
                        return;
                    }

                    // ESC -> cancel chat
                    if (code == KeyEvent.VK_ESCAPE) {
                        gameData.closeChatInput();
                        return;
                    }

                    // TAB -> toggle TEAM/GLOBAL
                    if (code == KeyEvent.VK_TAB) {
                        gameData.toggleChatScope();
                        System.out.println("TAB pressed, chatActive=" + gameData.isChatInputActive());
                        e.consume();
                        return;
                    }

                    // BACKSPACE -> delete last char
                    if (code == KeyEvent.VK_BACK_SPACE) {
                        gameData.backspaceChatChar();
                        return;
                    }

                    // other printable characters -> append
                    if (!Character.isISOControl(ch)) {
                        gameData.appendChatChar(ch);
                    }
                    return; // do NOT forward to InputCommand
                }


                // Open chat with '>' (shift + .) or '/' if you prefer
                if (ch == '>' || ch == '/') {
                    gameData.openChatInput();
                    return;
                }

                // Otherwise, treat as game movement input
                InputCommand cmd = InputCommand.getCommandForKey(e.getKeyCode());
                if (cmd != null) cmd.execute(gameData);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // If chat is active, ignore key releases for movement
                if (gameData.isChatInputActive()) {
                    return;
                }

                InputCommand cmd = InputCommand.getCommandForKey(e.getKeyCode());
                if (cmd != null) cmd.undo(gameData);
            }
        });

        pack();
        setVisible(true);
        panel.requestFocusInWindow();
    }
}
