package server.state;

import server.Slime;

/**
 * State pattern: player is tired (moderate stamina). Transitions to Exhausted
 * below 20, back to Fresh above 80.
 */
public final class TiredState implements PlayerState {

    @Override
    public void enter(Slime player) {
        // Player enters tired state
    }

    @Override
    public void update(Slime player, double deltaTime) {
        // Stamina drains faster when tired
        if (Math.abs(player.getVelX()) > 0.1) {
            player.decreaseStamina(4.5 * deltaTime);
        } else {
            player.increaseStamina(3.0 * deltaTime);
        }

        // Transition to exhausted if stamina drops too low
        if (player.getStamina() < 20) {
            player.setState(new ExhaustedState());
        }
        // Transition back to fresh if recovered
        else if (player.getStamina() > 80) {
            player.setState(new FreshState());
        }
    }

    @Override
    public void exit(Slime player) {
        // Player exits tired state
    }

    @Override
    public double getSpeedMultiplier() {
        return 0.8; // Reduced speed
    }

    @Override
    public String getStateName() {
        return "Tired";
    }
}
