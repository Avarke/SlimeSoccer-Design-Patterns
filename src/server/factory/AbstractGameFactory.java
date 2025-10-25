package server.factory;

import java.awt.Color;

/**
 * Provides shared construction logic for families of game objects.
 */
import server.Goal;
import server.PowerUpManager;
import server.Rectangle;
import server.Slime;
import server.Text;

public abstract class AbstractGameFactory implements IGameFactory {

    @Override
    public BallFactory createBallFactory() {
        return new DefaultBallFactory();
    }

    @Override
    public SlimeFactory createSlimeFactory() {
        return new DefaultSlimeFactory();
    }

    @Override
    public Slime createSlime(double x, double y, Color color, boolean isLeft) {
        
        return createSlimeFactory().createSlime(x, y, color, isLeft);
    }

    @Override
    public Rectangle createRectangle(double x, double y, double width, double height, Color color) {
        return new Rectangle(x, y, width, height, color);
    }

    @Override
    public Goal createGoal(double x, double y, boolean isLeft) {
        // Delegate Goal creation to the GoalFactory (Factory Method)
        return new DefaultGoalFactory().createGoal(x, y, isLeft);
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
