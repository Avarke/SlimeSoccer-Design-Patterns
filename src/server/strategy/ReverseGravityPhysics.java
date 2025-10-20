package server.strategy;

import server.Ball;

public class ReverseGravityPhysics implements BallPhysicsStrategy {
    private static final double GRAVITY = -0.3; // upward pull

    @Override
    public void update(Ball ball, int speedfactor) {
        double velY = ball.getVelY() + GRAVITY * speedfactor / 10.0;
        double velX = ball.getVelX();

        ball.setVelY(velY);
        ball.setY(ball.getY() + velY * speedfactor / 10.0);
        ball.setX(ball.getX() + velX * speedfactor / 10.0);
    }
}
