package server.factory;

import java.awt.Color;

import server.Ball;
import server.BallType;
import server.PowerUpType;
import server.strategy.BallPhysicsStrategies;

public final class BallFactory {

    private BallFactory() { }

    public static Ball createBall(BallType type, double x, double y) {
        Ball ball;
        switch (type) {
            case HEAVY:
                ball = new Ball(x, y, 12, Color.DARK_GRAY);
                ball.setPhysicsStrategy(BallPhysicsStrategies.forType(PowerUpType.HEAVY));
                break;
            case LIGHT:
                ball = new Ball(x, y, 8, Color.LIGHT_GRAY);
                ball.setPhysicsStrategy(BallPhysicsStrategies.forType(PowerUpType.LOW_GRAVITY));
                break;
            case FAST:
                ball = new Ball(x, y, 10, Color.WHITE);
                ball.setVelX(5);
                ball.setVelY(-5);
                break;
            case INVISIBLE:
                ball = new Ball(x, y, 10, new Color(0, 0, 0, 0));
                break;
            case NORMAL:
            default:
                ball = new Ball(x, y, 10, Color.WHITE);
                break;
        }
        return ball;
    }
}
