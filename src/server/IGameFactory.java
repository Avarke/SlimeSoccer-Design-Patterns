package server;

import java.awt.Color;

/*
Abstract factory interface for game objects
*/
public interface IGameFactory {
    Ball createBall(BallType type, double x, double y);
    Slime createSlime(double x, double y, Color color, boolean isLeft);
    Rectangle createRectangle(double x, double y, double width, double height, Color color);
    Goal createGoal(double x, double y, boolean isLeft);
    Text createText(String content, double x, double y, int fontSize, Color color, String fontName);
    PowerUpManager createPowerUpManager();
}
