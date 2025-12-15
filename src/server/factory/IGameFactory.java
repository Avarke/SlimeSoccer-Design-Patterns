package server.factory;

import java.awt.Color;

import server.Ball;
import server.Goal;
import server.IPowerUpManager;
import server.PowerUpManager;
import server.Rectangle;
import server.Slime;
import server.Text;

public interface IGameFactory {
    // Sub-factory accessors
    BallFactory createBallFactory();

    SlimeFactory createSlimeFactory();

    Slime createSlime(double x, double y, Color color, boolean isLeft);

    Rectangle createRectangle(double x, double y, double width, double height, Color color);

    Goal createGoal(double x, double y, boolean isLeft);

    Text createText(String content, double x, double y, int fontSize, Color color, String fontName);

    IPowerUpManager createPowerUpManager();
}
