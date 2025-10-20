package server.factory;

/*
Factory interface for creating Ball instances.
 */
import server.Ball;

public interface IBallFactory {
    Ball create(double x, double y);
}
