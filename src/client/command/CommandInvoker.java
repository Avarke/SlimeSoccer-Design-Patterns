package client.command;

import client.GameData;

import java.util.HashMap;
import java.util.Map;

public class CommandInvoker {
    private final GameData data;
    private final Map<Integer, InputCommand> pressCommands = new HashMap<>();
    private final Map<Integer, InputCommand> releaseCommands = new HashMap<>();

    public CommandInvoker(GameData data) {
        this.data = data;
    }

    public void register(int keyCode, InputCommand pressCommand, InputCommand releaseCommand) {
        if (pressCommand != null) {
            pressCommands.put(keyCode, pressCommand);
        }
        if (releaseCommand != null) {
            releaseCommands.put(keyCode, releaseCommand);
        }
    }

    public void onKeyPressed(int keyCode) {
        execute(pressCommands.get(keyCode));
    }

    public void onKeyReleased(int keyCode) {
        execute(releaseCommands.get(keyCode));
    }

    private void execute(InputCommand command) {
        if (command != null) {
            command.execute(data);
        }
    }
}
