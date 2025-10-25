package client.command;
import client.GameData;
import java.awt.event.KeyEvent;

public class MoveRightCommand extends InputCommand{

    public MoveRightCommand(){
        super(KeyEvent.VK_RIGHT);
    }
    @Override
    public void execute(GameData g) {
        g.setRightPressed(true);
    }

    @Override
    public void undo(GameData g) {
        g.setRightPressed(false);
    }
}
