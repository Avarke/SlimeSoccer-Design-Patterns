package server.state;

import server.Slime;

/**
 * State Pattern - Interface for player fatigue states
 */
public interface PlayerState {
    void enter(Slime player);

    void update(Slime player, double deltaTime);

    void exit(Slime player);

    double getSpeedMultiplier();

    String getStateName();
}
