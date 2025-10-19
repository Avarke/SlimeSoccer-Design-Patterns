package server;

import java.awt.Color;
import java.awt.Graphics;

public class Goal {
    private double x, y, xIntvl, yIntvl, height, width, netThickness, barThickness;
    private boolean isLeftGoal;

    public Goal(double x, double y, boolean isLeftGoal) {
        this.x = x;
        this.y = y;
        this.isLeftGoal = isLeftGoal;
        xIntvl = (int) (0.005*Window.WIDTH);
        yIntvl = (int) (0.009*Window.HEIGHT);
        width = (int) (0.048*Window.WIDTH);
        height = (int) (0.169*Window.HEIGHT);
        netThickness = 2;
        barThickness = 10;
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        for(int i = 0; i < 10; i++) {
            g.fillRect((int) (x + (i*xIntvl)), (int)y, (int)netThickness, (int)height);
        }
        for(int i = 0; i < 19; i++) {
            g.fillRect((int)x, (int) (y + (i*yIntvl)), (int)width, (int)netThickness);
        }
        if(isLeftGoal) {
            g.fillRect((int) (x + width), (int) (y - 5), (int)barThickness, (int)height);
        }
        else {
            g.fillRect((int) (x - barThickness), (int) (y - 5), (int)barThickness, (int)height);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getBarThickness() { return barThickness; }

}