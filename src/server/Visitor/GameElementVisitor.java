package server.Visitor;

import server.Ball;
import server.Slime;
import server.PowerUp;
import server.Goal;

/**
 * Visitor interface for game elements.
 * Allows operations to be performed on different game objects without modifying their classes.
 */
public interface GameElementVisitor {
    void visitBall(Ball ball);
    void visitSlime(Slime slime);
    void visitPowerUp(PowerUp powerUp);
    void visitGoal(Goal goal);
}
