package server.chain;

import server.Ball;
import server.Goal;
import server.Rectangle;

public class GameEventContext {
    private final Ball ball;
    private final Rectangle leftErrorBar;
    private final Rectangle rightErrorBar;
    private final Goal leftGoal;
    private final Goal rightGoal;

    public GameEventContext(Ball ball, Rectangle leftErrorBar, Rectangle rightErrorBar, Goal leftGoal, Goal rightGoal) {
        this.ball = ball;
        this.leftErrorBar = leftErrorBar;
        this.rightErrorBar = rightErrorBar;
        this.leftGoal = leftGoal;
        this.rightGoal = rightGoal;
    }

    public Ball getBall() {
        return ball;
    }

    public Rectangle getLeftErrorBar() {
        return leftErrorBar;
    }

    public Rectangle getRightErrorBar() {
        return rightErrorBar;
    }

    public Goal getLeftGoal() {
        return leftGoal;
    }

    public Goal getRightGoal() {
        return rightGoal;
    }
}
