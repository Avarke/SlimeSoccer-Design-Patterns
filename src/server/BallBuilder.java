package server;

import java.awt.Color;

public final class BallBuilder {
    private double x;
    private double y;
    private double radius = 20;
    private Color color = Color.WHITE;
    private double velX = 0;
    private double velY = 0;
    private BallPhysicsStrategy physics = BallPhysicsStrategies.normal();

    public BallBuilder atPosition(double x, double y) {
        this.x = x; this.y = y;
        return this;
    }

    public BallBuilder withRadius(double r) {
        this.radius = r;
        return this;
    }

    public BallBuilder withColor(Color c) {
        if (c != null) this.color = c;
        return this;
    }

    public BallBuilder withVelocity(double vx, double vy) {
        this.velX = vx; this.velY = vy;
        return this;
    }

    public BallBuilder withPhysicsStrategy(BallPhysicsStrategy s) {
        if (s != null) this.physics = s;
        return this;
    }

    // Optional: seed from a prototype
    public BallBuilder fromPrototype(BallType type) {
        Ball proto = BallPrototypeRegistry.getInstance().create(type, 0, 0);
        this.radius = proto.getRadius();
        this.color  = proto.getColor();
        this.velX   = proto.getVelX();
        this.velY   = proto.getVelY();
        this.physics = proto.getPhysicsStrategy();
        return this;
    }

    public Ball build() {
        Ball b = new Ball(x, y, radius, color);
        b.setVelX(velX);
        b.setVelY(velY);
        b.setPhysicsStrategy(physics);
        return b;
    }
}