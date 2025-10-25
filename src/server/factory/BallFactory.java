package server.factory;

import server.Ball;


public interface BallFactory {
    Ball createNormal(double x, double y);
    Ball createHeavy(double x, double y);
    Ball createLight(double x, double y);
    Ball createFast(double x, double y);
    Ball createInvisible(double x, double y);
    
    
    default Ball createBall(double x, double y) {
        return createNormal(x, y);
    }
}
