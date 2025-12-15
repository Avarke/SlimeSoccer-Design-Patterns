package server.chain;

import server.Ball;
import server.Goal;
import server.SlimeSoccer;

public class RightGoalHandler extends GameCheckHandler {
    @Override
    protected boolean handle(GameEventContext context, SlimeSoccer game) {
        Ball ball = context.getBall();
        Goal rightGoal = context.getRightGoal();

        double by = ball.getY();
        double br = ball.getRadius();
        double bx = ball.getX();

        double rgy = rightGoal.getY();
        double rgh = rightGoal.getHeight();
        double rgx = rightGoal.getX();

        boolean inRightMouthY = by - br >= rgy && by + br <= rgy + rgh;
        boolean rightGoalScored = inRightMouthY && (bx - br > rgx);

        if (rightGoalScored) {
            game.handleGoal(false);
            return true;
        }
        return false;
    }
}
