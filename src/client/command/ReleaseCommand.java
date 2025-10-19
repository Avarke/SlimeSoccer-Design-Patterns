package client.command;

import client.GameData;

public final class ReleaseCommand implements InputCommand {
    private final InputAction action;

    public ReleaseCommand(InputAction action) {
        this.action = action;
    }

    @Override
    public void execute(GameData data) {
        action.apply(data, false);
    }
}
