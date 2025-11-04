package server.factory;

import java.awt.Color;

import server.Ball;
import server.PowerUpType;
import server.strategy.BallPhysicsStrategies;
import server.builder.BallDirector;
import server.builder.ClassicBallBuilder;
import server.builder.HeavyBallBuilder;



public class DefaultBallFactory implements BallFactory {

    private final BallDirector director = new BallDirector();

    @Override
    public Ball createNormal(double x, double y) {
        return director.construct(new ClassicBallBuilder(), x, y);
    }

    @Override
    public Ball createHeavy(double x, double y) {
        return director.construct(new HeavyBallBuilder(), x, y);
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
