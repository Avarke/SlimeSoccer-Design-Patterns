package server;

import java.awt.Color;


public class DefaultGameFactory implements IGameFactory {

    public static final DefaultGameFactory INSTANCE = new DefaultGameFactory();

    private DefaultGameFactory() {}

    @Override
    public Ball createBall(BallType type, double x, double y) {
        return BallFactory.createBall(type, x, y);
    }

    @Override
    public Slime createSlime(double x, double y, Color color, boolean isLeft) {
        return SlimeFactory.createSlime(x, y, color, isLeft);
    }
}
