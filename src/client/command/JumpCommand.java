package client.command;

import client.GameData;

import java.awt.event.KeyEvent;

public class JumpCommand extends InputCommand{

    public JumpCommand()
    {
        super(KeyEvent.VK_UP);
    }

    @Override
    public void execute(GameData g) {
        g.setUpPressed(true);
    }

    @Override
    public void undo(GameData g) {
        g.setUpPressed(false);
    }
}
