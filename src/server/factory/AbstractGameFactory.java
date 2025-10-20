package server.factory;

import java.awt.Color;

/**
 * Provides shared construction logic for families of game objects.
 */
import server.Ball;
import server.BallType;
import server.Goal;
import server.PowerUpManager;
import server.Rectangle;
import server.Slime;
import server.Text;

public abstract class AbstractGameFactory implements IGameFactory {

    @Override
    public Ball createBall(BallType type, double x, double y) {
        return BallFactory.createBall(type, x, y);
    }

    @Override
    public Slime createSlime(double x, double y, Color color, boolean isLeft) {
        return SlimeFactory.createSlime(x, y, color, isLeft);
    }

    @Override
    public Rectangle createRectangle(double x, double y, double width, double height, Color color) {
        return new Rectangle(x, y, width, height, color);
    }

    @Override
    public Goal createGoal(double x, double y, boolean isLeft) {
        return new Goal(x, y, isLeft);
    }

    @Override
    public Text createText(String content, double x, double y, int fontSize, Color color, String fontName) {
        return new Text(content, x, y, fontSize, color, fontName);
    }

    @Override
    public PowerUpManager createPowerUpManager() {
        return new PowerUpManager();
    }
}
