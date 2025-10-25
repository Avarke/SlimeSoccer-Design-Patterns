package server.strategy;

import server.Ball;
import server.Window;

public class NormalPhysics implements BallPhysicsStrategy {
    private static final double GRAVITY = 0.3;

    @Override
    public void update(Ball ball, int speedfactor) {
        double velY = ball.getVelY() + GRAVITY * speedfactor / 10.0;
        double velX = ball.getVelX();

        ball.setVelY(velY);
        ball.setY(ball.getY() + velY * speedfactor / 10.0);
        ball.setX(ball.getX() + velX * speedfactor / 10.0);

        double lowBound = 0.814 * Window.HEIGHT;
        if (ball.getY() > lowBound) {
            if (velX > 0) ball.setVelX(velX - 0.2);
            else if (velX < 0) ball.setVelX(velX + 0.2);
            if (Math.abs(ball.getVelX()) < 0.2) ball.setVelX(0);
        }
    }
}
