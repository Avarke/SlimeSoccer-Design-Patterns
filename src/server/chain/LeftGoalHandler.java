package server.chain;

import server.Ball;
import server.Goal;
import server.SlimeSoccer;

public class LeftGoalHandler extends GameCheckHandler {
    @Override
    protected boolean handle(GameEventContext context, SlimeSoccer game) {
        Ball ball = context.getBall();
        Goal leftGoal = context.getLeftGoal();

        double by = ball.getY();
        double br = ball.getRadius();
        double bx = ball.getX();

        double lgy = leftGoal.getY();
        double lgh = leftGoal.getHeight();
        double lgx = leftGoal.getX();
        double lgw = leftGoal.getWidth();

        boolean inLeftMouthY = by - br >= lgy && by + br <= lgy + lgh;
        boolean leftGoalScored = inLeftMouthY && (bx + br < lgx + lgw);

        if (leftGoalScored) {
            game.handleGoal(true);
            return true;
        }
        return false;
    }
}
