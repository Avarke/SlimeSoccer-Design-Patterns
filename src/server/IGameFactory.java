package server;

import java.awt.Color;

/*
Abstract factory interface for game objects
*/
public interface IGameFactory {
    Ball createBall(BallType type, double x, double y);
    Slime createSlime(double x, double y, Color color, boolean isLeft);
}
