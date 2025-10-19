package client.command;

import client.GameData;

public enum InputAction {
    MOVE_LEFT {
        @Override
        public void apply(GameData data, boolean active) {
            data.setLeftPressed(active);
        }
    },
    MOVE_RIGHT {
        @Override
        public void apply(GameData data, boolean active) {
            data.setRightPressed(active);
        }
    },
    JUMP {
        @Override
        public void apply(GameData data, boolean active) {
            data.setUpPressed(active);
        }
    };

    public abstract void apply(GameData data, boolean active);
}
