package server;

import java.awt.Color;
import java.awt.Graphics;

public class Rectangle {
    private double x, y, width, height;
    private static double shrinkSpeed;
    private Color color;

    public Rectangle(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        shrinkSpeed = 0.005*Window.WIDTH;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
    }

    public void shrinkLeft() {
        width -= shrinkSpeed;
    }
    public void shrinkRight() {
        x += shrinkSpeed;
        width -= shrinkSpeed;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
}