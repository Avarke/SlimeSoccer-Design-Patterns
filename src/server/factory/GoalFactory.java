package server.factory;

import server.Goal;


public interface GoalFactory {
    Goal createGoal(double x, double y, boolean isLeft);
    
    Goal createMidAirGoal(double x, double y, boolean isLeft);
}
