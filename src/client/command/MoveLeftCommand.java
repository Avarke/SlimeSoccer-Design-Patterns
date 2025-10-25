package client.command;

import client.GameData;

import java.awt.event.KeyEvent;

public class MoveLeftCommand extends InputCommand {


    public MoveLeftCommand()
    {
        super(KeyEvent.VK_LEFT);
    }

    @Override
    public void execute(GameData g) {
        g.setLeftPressed(true);
    }

    @Override
    public void undo(GameData g) {
        g.setLeftPressed(false);
    }
}
