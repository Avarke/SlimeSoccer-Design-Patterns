package server.builder;

import java.awt.Color;
import server.Ball;
import server.strategy.BallPhysicsStrategy;
import server.strategy.BallPhysicsStrategies;

public final class ClassicBallBuilder implements BallBuilder {
    private double x, y;
    private double radius = 20;
    private Color color = Color.WHITE;
    private double vx = 0, vy = 0;
    private BallPhysicsStrategy physics = BallPhysicsStrategies.normal();

    @Override public BallBuilder at(double x, double y) { this.x = x; this.y = y; return this; }
    @Override public BallBuilder radius(double r) { if (r > 0) this.radius = r; return this; }
    @Override public BallBuilder color(Color c) { if (c != null) this.color = c; return this; }
    @Override public BallBuilder velocity(double vx, double vy) { this.vx = vx; this.vy = vy; return this; }
    @Override public BallBuilder physics(BallPhysicsStrategy s) { if (s != null) this.physics = s; return this; }

    @Override
    public Ball build() {
        Ball b = new Ball(x, y, radius, color);
        b.setVelX(vx);
        b.setVelY(vy);
        b.setPhysicsStrategy(physics);
        return b;
    }
}
