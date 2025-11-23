package server.state;

import server.Slime;

/**
 * State pattern: player is exhausted (very low stamina). Transitions to
 * Recovering when stamina hits 0, back to Tired above 30.
 */
public final class ExhaustedState implements PlayerState {

    @Override
    public void enter(Slime player) {
        // Player enters exhausted state
    }

    @Override
    public void update(Slime player, double deltaTime) {
        // Stamina drains very fast, can hit zero
        if (Math.abs(player.getVelX()) > 0.1) {
            player.decreaseStamina(6.0 * deltaTime);
        } else {
            player.increaseStamina(2.0 * deltaTime);
        }

        // Transition to recovering if stamina hits zero
        if (player.getStamina() <= 0) {
            player.setStamina(0);
            player.setState(new RecoveringState());
        }
        // Transition to tired if recovers a bit
        else if (player.getStamina() > 30) {
            player.setState(new TiredState());
        }
    }

    @Override
    public void exit(Slime player) {
        // Player exits exhausted state
    }

    @Override
    public double getSpeedMultiplier() {
        return 0.5; // Significantly reduced speed
    }

    @Override
    public String getStateName() {
        return "Exhausted";
    }
}
