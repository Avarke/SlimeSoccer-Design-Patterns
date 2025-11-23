package server.state;

import server.Slime;

/**
 * State pattern: player is recovering (forced rest). Transitions to Tired once
 * stamina rises above 40.
 */
public final class RecoveringState implements PlayerState {

    @Override
    public void enter(Slime player) {
        // Player enters recovering state
    }

    @Override
    public void update(Slime player, double deltaTime) {
        // Force recovery - movement disabled or very slow
        player.increaseStamina(5.0 * deltaTime);

        // Transition to tired once recovered enough
        if (player.getStamina() > 40) {
            player.setState(new TiredState());
        }
    }

    @Override
    public void exit(Slime player) {
        // Player exits recovering state
    }

    @Override
    public double getSpeedMultiplier() {
        return 0.2; // Minimal movement
    }

    @Override
    public String getStateName() {
        return "Recovering";
    }
}
