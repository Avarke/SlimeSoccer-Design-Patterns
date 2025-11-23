package server;

import java.awt.Color;
import java.awt.Graphics;

public class Slime {
    private double x, y, velX, velY, eyeX, eyeY;
    private static double radius, pupilSize, lowBound, jumpVel;
    private Color color;
    private boolean isFacingRight, onFloor;

    // State Pattern for fatigue management
    private server.state.PlayerState currentState;
    private double stamina;

    public Slime(double x, double y, Color color, boolean isFacingRight) {
        this.x = x;
        this.y = y;
        radius = 0.039 * Window.WIDTH;
        pupilSize = 0.187 * radius;
        lowBound = 0.814 * Window.HEIGHT;
        jumpVel = 0.012 * Window.HEIGHT;
        this.color = color;
        this.isFacingRight = isFacingRight;
        eyeX = isFacingRight ? x + 0.467 * radius : x - 0.467 * radius;
        eyeY = y - 0.467 * radius;

        // Initialize with fresh state and full stamina
        this.stamina = 100.0;
        this.currentState = new server.state.FreshState();
    }

    public void draw(Graphics g, double ballX, double ballY) {
        g.setColor(color);
        g.fillArc((int) (x - radius), (int) (y - radius), (int) (radius * 2), (int) (radius * 2), 0, 180);

        float ballDist = (float) Math.sqrt(Math.pow(ballX - eyeX, 2) + Math.pow(ballY - eyeY, 2));
        g.setColor(Color.WHITE);
        g.fillOval((int) (eyeX - 15), (int) (eyeY - 15), 30, 30);
        g.setColor(Color.BLACK);
        g.fillOval((int) (eyeX + 6 * (ballX - eyeX) / ballDist - 7), (int) (eyeY + 6 * (ballY - eyeY) / ballDist - 7),
                (int) pupilSize, (int) pupilSize);
    }

    public void downMovement() {
        if (!onFloor) {
            y += velY;
        } else {
            velY = 0;
        }
    }

    public void floorCheck() {
        if (y >= lowBound) {
            y = lowBound;
            onFloor = true;
        } else
            onFloor = false;
    }

    public void jump() {
        if (onFloor) {
            velY = -jumpVel;
            onFloor = false;
        }
    }

    public void gravity() {
        if (!onFloor) {
            velY += 0.5;
        }
    }

    public void updateEyes() {
        eyeX = isFacingRight ? x + 0.467 * radius : x - 0.467 * radius;
        eyeY = y - 0.467 * radius;
    }

    public void updateState(double deltaTime) {
        if (currentState != null) {
            currentState.update(this, deltaTime);
        }
    }

    public void reset(double x, double y) {
        this.x = x;
        this.y = y;
        this.stamina = 100.0;
        this.currentState = new server.state.FreshState();
    }

    public boolean foulCheckLeft() {
        return x < 0.048 * Window.WIDTH;
    }

    public boolean foulCheckRight() {
        return x > Window.WIDTH - 0.048 * Window.WIDTH;
    }

    // Getters and setters
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        // Apply speed multiplier from current state
        this.velX = velX * (currentState != null ? currentState.getSpeedMultiplier() : 1.0);
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public Color getColor() {
        return color;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    // State pattern methods
    public void setState(server.state.PlayerState state) {
        if (this.currentState != null) {
            this.currentState.exit(this);
        }
        this.currentState = state;
        if (this.currentState != null) {
            this.currentState.enter(this);
        }
    }

    public server.state.PlayerState getState() {
        return currentState;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = Math.max(0, Math.min(100, stamina));
    }

    public void decreaseStamina(double amount) {
        setStamina(stamina - amount);
    }

    public void increaseStamina(double amount) {
        setStamina(stamina + amount);
    }
}
