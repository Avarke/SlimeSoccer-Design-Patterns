package server.state;

import server.Slime;

/**
 * State pattern: player is fresh (full stamina). Transitions to Tired when
 * stamina drops below 50; regains stamina while idle.
 */
public final class FreshState implements PlayerState {

    @Override
    public void enter(Slime player) {
        // Player enters fresh state
    }

    @Override
    public void update(Slime player, double deltaTime) {
        // Decrease stamina when moving
        if (Math.abs(player.getVelX()) > 0.1) {
            player.decreaseStamina(3.0 * deltaTime);
        } else {
            // Recover stamina when idle
            player.increaseStamina(4.0 * deltaTime);
        }

        // Transition to tired state if stamina drops
        if (player.getStamina() < 50) {
            player.setState(new TiredState());
        }
    }

    @Override
    public void exit(Slime player) {
        // Player exits fresh state
    }

    @Override
    public double getSpeedMultiplier() {
        return 1.0; // Normal speed
    }

    @Override
    public String getStateName() {
        return "Fresh";
    }
}
