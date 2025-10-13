package server;

public class HeavyPhysics implements BallPhysicsStrategy {
    private static final double GRAVITY = 0.5;

    @Override
    public void update(Ball ball, int speedfactor) {
        double velY = ball.getVelY() + GRAVITY * speedfactor / 10.0;
        double velX = ball.getVelX() * 0.98;

        ball.setVelY(velY);
        ball.setVelX(velX);
        ball.setY(ball.getY() + velY * speedfactor / 10.0);
        ball.setX(ball.getX() + velX * speedfactor / 10.0);

        double lowBound = 0.814 * Window.HEIGHT;
        if (ball.getY() > lowBound && Math.abs(ball.getVelX()) < 0.25) {
            ball.setVelX(0);
        }
    }
}