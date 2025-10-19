package server;

import java.awt.Color;
import java.awt.Graphics;

public class PowerUp {
    private final double x, y;
    private final int radius;
    private final PowerUpType type;
    private final long durationMs;
    private final Color color;

    public PowerUp(double x, double y, int radius, PowerUpType type, long durationMs) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.durationMs = durationMs;
        this.color = colorFor(type);
    }

    private Color colorFor(PowerUpType t) {
        switch (t) {
            case LOW_GRAVITY: return new Color(135, 206, 250);
            case HEAVY: return new Color(80, 80, 80);
            case REVERSE_GRAVITY: return new Color(255, 105, 180);
            default: return Color.WHITE;
        }
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), radius * 2, radius * 2);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getRadius() { return radius; }
    public Color getColor() { return color; }

    public boolean collides(Slime s) {
        double dx = x - s.getX();
        double dy = y - s.getY();
        double dist2 = dx*dx + dy*dy;
        double r = radius + s.getRadius();
        return dist2 <= r*r;
    }

    public BallPhysicsStrategy createStrategy() {
        return BallPhysicsStrategies.forType(type);
    }

    public PowerUpType getType() { return type; }
    public long getDurationMs() { return durationMs; }
}
