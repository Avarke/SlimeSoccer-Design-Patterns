package client.command;

import client.GameData;

public final class PressCommand implements InputCommand {
    private final InputAction action;

    public PressCommand(InputAction action) {
        this.action = action;
    }

    @Override
    public void execute(GameData data) {
        action.apply(data, true);
    }
}
