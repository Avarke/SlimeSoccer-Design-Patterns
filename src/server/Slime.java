package server;

import java.awt.Color;
import java.awt.Graphics;

import server.bridge.GraphicsSlimeRenderBridge;
import server.bridge.SlimeRenderBridge;

public class Slime {
    private double x, y, velX, velY, eyeX, eyeY;
    private static double radius, pupilSize, lowBound, jumpVel;
    private Color color;
    private boolean isFacingRight, onFloor;

    public Slime(double x, double y, Color color, boolean isFacingRight) {
        this.x = x;
        this.y = y;
        radius = 0.039*Window.WIDTH;
        pupilSize = 0.187*radius;
        lowBound = 0.814*Window.HEIGHT;
        jumpVel = 0.012*Window.HEIGHT;
        this.color = color;
        this.isFacingRight = isFacingRight;
        eyeX = isFacingRight ? x + 0.467*radius : x - 0.467*radius;
        eyeY = y - 0.467*radius;
    }

    public void draw(Graphics g, double ballX, double ballY) {
        draw(new GraphicsSlimeRenderBridge(g), ballX, ballY);
    }

    public void draw(SlimeRenderBridge bridge, double ballX, double ballY) {
        bridge.setColor(color);
        bridge.fillArc((int) (x - radius), (int) (y - radius), (int) (radius*2), (int) (radius*2), 0, 180);

        float ballDist = (float)Math.sqrt(Math.pow(ballX-eyeX, 2) + Math.pow(ballY-eyeY, 2));
        bridge.setColor(Color.WHITE);
        bridge.fillOval((int) (eyeX - 15), (int) (eyeY - 15), 30, 30);
        bridge.setColor(Color.BLACK);
        bridge.fillOval((int)(eyeX + 6*(ballX-eyeX)/ballDist - 7), (int)(eyeY + 6*(ballY-eyeY)/ballDist - 7), (int)pupilSize, (int)pupilSize);
    }

    public void downMovement() {
        if(!onFloor) {
            y += velY;
        } else {
            velY = 0;
        }
    }

    public void floorCheck() {
        if(y >= lowBound) {
            y = lowBound;
            onFloor = true;
        }
        else onFloor = false;
    }

    public void jump() {
        if(onFloor) {
            velY = -jumpVel;
            onFloor = false;
        }
    }

    public void gravity() {
        if(!onFloor) {
            velY += 0.5;
        }
    }

    public void updateEyes() {
        eyeX = isFacingRight ? x + 0.467*radius : x - 0.467*radius;
        eyeY = y - 0.467*radius;
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean foulCheckLeft() {
        return x < 0.048*Window.WIDTH;
    }
    public boolean foulCheckRight() {
        return x > Window.WIDTH - 0.048*Window.WIDTH;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public double getRadius() { return radius; }
    public double getVelX() { return velX; }
    public void setVelX(double velX) { this.velX = velX; }
    public double getVelY() { return velY; }
    public Color getColor() { return color; }
    public boolean isFacingRight() { return isFacingRight; }
}
