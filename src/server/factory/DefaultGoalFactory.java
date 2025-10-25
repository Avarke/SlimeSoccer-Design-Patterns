package server.factory;

import server.Goal;

public class DefaultGoalFactory implements GoalFactory {

    @Override
    public Goal createGoal(double x, double y, boolean isLeft) {
        if (isLeft) {
            return new LeftGoal(x, y, true);
        }
        return new RightGoal(x, y, false);
    }

    @Override
    public Goal createMidAirGoal(double x, double y, boolean isLeft) {
        if (isLeft) {
            return new MidAirLeftGoal(x, y, true);
        }
        return new MidAirRightGoal(x, y, false);
    }
}
