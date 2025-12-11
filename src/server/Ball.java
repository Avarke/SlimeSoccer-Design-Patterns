package server;

import java.awt.Color;
import java.awt.Graphics;

import server.strategy.BallPhysicsStrategy;
import server.strategy.BallPhysicsStrategies;
import server.Visitor.GameElement;
import server.Visitor.GameElementVisitor;

public class Ball implements GameElement {
    private double x, y, lowBound, rightBound, leftCBarX, rightCBarX, cBarY;
    private double radius;
    private Color color;

    private double velY, velX;

    private int lastTouchingSlot = 0; // 0 = no one yet



    // Strategy for physics
    private BallPhysicsStrategy physics = BallPhysicsStrategies.normal();

    public Ball(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;

        lowBound = 0.814*Window.HEIGHT;
        rightBound = Window.WIDTH;
        leftCBarX = 0.093*Window.WIDTH;
        rightCBarX = Window.WIDTH - leftCBarX;
        cBarY = 0.648*Window.HEIGHT;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int) (x - radius), (int) (y - radius), (int) (radius*2), (int) (radius*2));
    }

    public void boundaries() {
        if (y + radius > lowBound) {
            y = lowBound - (radius + 1);
            velY *= -0.5;
        }
        if (y - radius < 0) {
            y = radius + 1;
            velY *= -0.5;
        }
        if (x - radius < 0) {
            x = 1 + radius;
            velX = -velX;
        }
        if (x + radius > rightBound) {
            x = rightBound - (radius + 1);
            velX = -velX;
        }
    }

    public void update(int speedfactor) {
        if (physics != null) {
            physics.update(this, speedfactor);
        }
    }

    public void crossBarCheck() {
        if(y < cBarY + 10 && y > cBarY - 10) {
            if(x < leftCBarX || x > rightCBarX) {
                velY = -velY;
            }
        }
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.velX = 0;
        this.velY = 0;
        this.physics = BallPhysicsStrategies.normal(); // normalize physics on new round
    }

    public BallPhysicsStrategy getPhysicsStrategy() { return physics; }
    public void setPhysicsStrategy(BallPhysicsStrategy physics) {
        this.physics = (physics != null) ? physics : BallPhysicsStrategies.normal();
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }
    public double getVelX() { return velX; }
    public void setVelX(double velX) { this.velX = velX; }
    public double getVelY() { return velY; }
    public void setVelY(double velY) { this.velY = velY; }
    public Color getColor() { return color; }

    public Ball copyWithPosition(double newX, double newY) {
        Ball clone = new Ball(newX, newY, radius, color);
        clone.setVelX(velX);
        clone.setVelY(velY);
        clone.setPhysicsStrategy(getPhysicsStrategy());
        return clone;
    }


    public int getLastTouchingSlot() {
        return lastTouchingSlot;
    }

    public void setLastTouchingSlot(int slot) {
        this.lastTouchingSlot = slot;
    }

    // optionally, a helper:
    public void clearLastTouchingSlot() {
        this.lastTouchingSlot = 0;
    }


    @Override
    public void accept(GameElementVisitor visitor) {
        visitor.visitBall(this);
    }

}
