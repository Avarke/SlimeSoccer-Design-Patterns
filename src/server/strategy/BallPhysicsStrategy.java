package server.strategy;

import server.Ball;

public interface BallPhysicsStrategy {
    void update(Ball ball, int speedfactor);
}
