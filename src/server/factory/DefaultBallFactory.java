package server.factory;

import java.awt.Color;

import server.Ball;
import server.PowerUpType;
import server.strategy.BallPhysicsStrategies;


public class DefaultBallFactory implements BallFactory {

    @Override
    public Ball createNormal(double x, double y) {
        return new NormalBall(x, y);
    }

    @Override
    public Ball createHeavy(double x, double y) {
        Ball ball = new HeavyBall(x, y);
        ball.setPhysicsStrategy(BallPhysicsStrategies.forType(PowerUpType.HEAVY));
        return ball;
    }

    @Override
    public Ball createLight(double x, double y) {
        Ball ball = new LightBall(x, y);
        ball.setPhysicsStrategy(BallPhysicsStrategies.forType(PowerUpType.LOW_GRAVITY));
        return ball;
    }

    @Override
    public Ball createFast(double x, double y) {
        Ball ball = new FastBall(x, y);
        ball.setVelX(5);
        ball.setVelY(-5);
        return ball;
    }

    @Override
    public Ball createInvisible(double x, double y) {
        return new InvisibleBall(x, y);
    }

    @Override
    public Ball createBall(double x, double y) {
        return createNormal(x, y);
    }
}
