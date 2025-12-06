package server.Visitor;

import server.Ball;
import server.Slime;
import server.PowerUp;
import server.Goal;


public interface GameElementVisitor {
    void visitBall(Ball ball);
    void visitSlime(Slime slime);
    void visitPowerUp(PowerUp powerUp);
    void visitGoal(Goal goal);
}
