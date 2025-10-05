package server;

import java.awt.Color;


public final class BallFactory {

    private BallFactory() { }

    public static Ball createBall(BallType type, double x, double y) {
        if (type == null) return NormalFactory.INSTANCE.create(x, y);

        switch (type) {
            case HEAVY:
                return HeavyFactory.INSTANCE.create(x, y);
            case LIGHT:
                return LightFactory.INSTANCE.create(x, y);
            case FAST:
                return FastFactory.INSTANCE.create(x, y);
            case INVISIBLE:
                return InvisibleFactory.INSTANCE.create(x, y);
            case NORMAL:
            default:
                return NormalFactory.INSTANCE.create(x, y);
        }
    }

    // Default 
    private static class NormalFactory implements IBallFactory {
        static final NormalFactory INSTANCE = new NormalFactory();
        @Override public Ball create(double x, double y) {
            double radius = 10;
            Color color = Color.WHITE;
            Ball b = new Ball(x, y, radius, color);
            b.setVelX(0);
            b.setVelY(0);
            return b;
        }
    }

    private static class HeavyFactory implements IBallFactory {
        static final HeavyFactory INSTANCE = new HeavyFactory();
        @Override public Ball create(double x, double y) {
            double radius = 12;
            Color color = Color.DARK_GRAY;
            Ball b = new Ball(x, y, radius, color);
            b.setVelX(0);
            b.setVelY(0);
            return b;
        }
    }

    private static class LightFactory implements IBallFactory {
        static final LightFactory INSTANCE = new LightFactory();
        @Override public Ball create(double x, double y) {
            double radius = 8;
            Color color = Color.LIGHT_GRAY;
            Ball b = new Ball(x, y, radius, color);
            return b;
        }
    }

    private static class FastFactory implements IBallFactory {
        static final FastFactory INSTANCE = new FastFactory();
        @Override public Ball create(double x, double y) {
            Ball b = NormalFactory.INSTANCE.create(x, y);
            b.setVelX(5);
            b.setVelY(-5);
            return b;
        }
    }

    private static class InvisibleFactory implements IBallFactory {
        static final InvisibleFactory INSTANCE = new InvisibleFactory();
        @Override public Ball create(double x, double y) {
            double radius = 10;
            Color invisible = new Color(0,0,0,0);
            Ball b = new Ball(x, y, radius, invisible);
            return b;
        }
    }
}
