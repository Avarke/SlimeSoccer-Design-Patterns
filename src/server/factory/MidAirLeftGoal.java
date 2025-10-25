package server.factory;

import java.awt.Color;
import java.awt.Graphics;
import server.Goal;
import server.Window;


public class MidAirLeftGoal extends Goal {
    public MidAirLeftGoal(double x, double y, boolean isLeft) {
        super(x, y, isLeft);
    }

    @Override
    public void draw(Graphics g) {
        
        g.setColor(Color.ORANGE);

        double x = getX();
        double y = getY();
        double netThickness = 2;
        double xIntvl = (int) (0.005*Window.WIDTH);
        double yIntvl = (int) (0.009*Window.HEIGHT);
        double width = (int) (0.048*Window.WIDTH);
        
        double height = (int) (0.08*Window.HEIGHT);

        for (int i = 0; i < 10; i++) {
            g.fillRect((int)(x + (i * xIntvl)), (int)y, (int)netThickness, (int)height);
        }
        for (int i = 0; i < 10; i++) {
            g.fillRect((int)(x + width - netThickness), (int)(y + (i * yIntvl)), (int)netThickness, (int)yIntvl);
        }

        g.fillRect((int)x, (int)(y - 5), (int)width, (int)6);
        g.fillRect((int)(x + width - 6), (int)(y - 5), 6, (int)height);
    }
}
