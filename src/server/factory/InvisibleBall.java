package server.factory;

import java.awt.Color;
import server.Ball;

public class InvisibleBall extends Ball {
    public InvisibleBall(double x, double y) {
        super(x, y, 10, new Color(0, 0, 0, 0));
    }
}
