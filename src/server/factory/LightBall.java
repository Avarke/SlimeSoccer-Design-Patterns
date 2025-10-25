package server.factory;

import java.awt.Color;
import server.Ball;

public class LightBall extends Ball {
    public LightBall(double x, double y) {
        super(x, y, 8, Color.LIGHT_GRAY);
    }
}
