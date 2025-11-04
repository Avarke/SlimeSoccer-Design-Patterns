package server.builder;

import java.awt.Color;
import server.Ball;
import server.strategy.BallPhysicsStrategy;

public interface BallBuilder {
    BallBuilder at(double x, double y);
    BallBuilder radius(double r);
    BallBuilder color(Color c);
    BallBuilder velocity(double vx, double vy);
    BallBuilder physics(BallPhysicsStrategy s);
    Ball build();
}
