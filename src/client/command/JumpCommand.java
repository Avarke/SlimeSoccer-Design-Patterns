package client.command;
import client.GameData;
import client.audio.SoundManager;

import java.awt.event.KeyEvent;

public class JumpCommand extends InputCommand{
    private boolean isHeld = false; // track whether the key is already held

    public JumpCommand()
    {
        super(KeyEvent.VK_UP);
    }

    @Override
    public void execute(GameData g) {
        SoundManager.load();
        if (!isHeld) {
            SoundManager.play(SoundManager.Sfx.JUMP);
            isHeld = true;
        }
        g.setUpPressed(true);
    }

    @Override
    public void undo(GameData g) {
        g.setUpPressed(false);
        isHeld = false;
    }
}
