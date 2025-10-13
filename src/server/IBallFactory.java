package server;

/*
Factory interface for creating Ball instances.
 */
public interface IBallFactory {
    Ball create(double x, double y);
}