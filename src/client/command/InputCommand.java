package client.command;

import client.GameData;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public abstract class InputCommand {
    private static final HashMap<Integer,  InputCommand> registry = new HashMap<>();

    private final int keyCode;

    protected InputCommand(int keycode)
    {
        this.keyCode = keycode;
        registry.put(keycode, this);
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public abstract void execute(GameData g);
    public abstract void undo(GameData g);

    public static InputCommand getCommandForKey(int keyCode)
    {
        return registry.get(keyCode);
    }

}
