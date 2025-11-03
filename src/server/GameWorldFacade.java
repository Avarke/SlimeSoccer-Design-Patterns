package server;

import java.awt.Color;

/**
 * Facade that assembles a ready-to-use Slime Soccer "world"
 * (players, ball, goals, pitch, UI bars) with sensible defaults.
 *
 * Use this to build a fully wired game state in one call,
 * without touching factories or coordinates directly.
 */
public class GameWorldFacade {

    private final IGameFactory factory;

    public GameWorldFacade() {
        this(DefaultGameFactory.INSTANCE);
    }

    public GameWorldFacade(IGameFactory factory) {
        this.factory = factory;
    }

    /** Build a standard world with NORMAL ball and default colors. */
    public World createDefaultWorld() {
        return createWorld(
                BallType.NORMAL,
                Color.GREEN, Color.CYAN, Color.RED, new Color(255, 110, 20)
        );
    }

    /**
     * Build a world with a given ball type and four player colors.
     * Uses Window.WIDTH/HEIGHT so everything fits current UI sizing.
     */
    public World createWorld(BallType ballType, Color p1, Color p2, Color p3, Color p4) {
        // Pitch & background
        Rectangle background = new Rectangle(0, 0, Window.WIDTH, Window.HEIGHT, Color.BLUE);
        Rectangle floor = new Rectangle(
                0, 0.814 * Window.HEIGHT,
                Window.WIDTH, Window.HEIGHT - 0.814 * Window.HEIGHT,
                Color.GRAY
        );

        // Players
        Slime player1 = factory.createSlime(Window.WIDTH / 2 - (2 * Window.WIDTH / 5), 0.814 * Window.HEIGHT, p1, true);
        Slime player2 = factory.createSlime(Window.WIDTH / 2 - (Window.WIDTH / 5),      0.814 * Window.HEIGHT, p2, true);
        Slime player3 = factory.createSlime(Window.WIDTH / 2 + (Window.WIDTH / 5),      0.814 * Window.HEIGHT, p3, false);
        Slime player4 = factory.createSlime(Window.WIDTH / 2 + (2 * Window.WIDTH / 5),  0.814 * Window.HEIGHT, p4, false);

        // Ball + aiming arrow
        Ball ball = factory.createBall(ballType, Window.WIDTH / 2, 0.278 * Window.HEIGHT);
        ball.setRadius(20);
        Ball ballArrow = factory.createBall(BallType.NORMAL, Window.WIDTH / 2, 0.046 * Window.HEIGHT);
        ballArrow.setRadius(20);

        // Goals & foul zones
        Goal leftGoal = new Goal(0, 0.667 * Window.HEIGHT, true);
        Goal rightGoal = new Goal(0.952 * Window.WIDTH, 0.667 * Window.HEIGHT, false);

        Rectangle leftGoalFoulZone  = new Rectangle(0,                 0.835 * Window.HEIGHT, 0.104 * Window.WIDTH, 0.009 * Window.HEIGHT, Color.WHITE);
        Rectangle rightGoalFoulZone = new Rectangle(0.896 * Window.WIDTH, 0.835 * Window.HEIGHT, 0.104 * Window.WIDTH, 0.009 * Window.HEIGHT, Color.WHITE);

        // Error bars (visualization of fouls/boosts)
        Rectangle leftErrorBar  = new Rectangle(0,                  0.861 * Window.HEIGHT, Window.WIDTH / 2, 10, player1.getColor());
        Rectangle rightErrorBar = new Rectangle(Window.WIDTH / 2.0, 0.861 * Window.HEIGHT, Window.WIDTH / 2, 10, player4.getColor());

        return new World(
                background, floor,
                player1, player2, player3, player4,
                ball, ballArrow,
                leftGoal, rightGoal,
                leftGoalFoulZone, rightGoalFoulZone,
                leftErrorBar, rightErrorBar
        );
    }

    /** Immutable DTO with everything a renderer/simulation needs. */
    public static class World {
        public final Rectangle background, floor;
        public final Slime player1, player2, player3, player4;
        public final Ball ball, ballArrow;
        public final Goal leftGoal, rightGoal;
        public final Rectangle leftGoalFoulZone, rightGoalFoulZone;
        public final Rectangle leftErrorBar, rightErrorBar;

        public World(
                Rectangle background, Rectangle floor,
                Slime player1, Slime player2, Slime player3, Slime player4,
                Ball ball, Ball ballArrow,
                Goal leftGoal, Goal rightGoal,
                Rectangle leftGoalFoulZone, Rectangle rightGoalFoulZone,
                Rectangle leftErrorBar, Rectangle rightErrorBar
        ) {
            this.background = background;
            this.floor = floor;
            this.player1 = player1;
            this.player2 = player2;
            this.player3 = player3;
            this.player4 = player4;
            this.ball = ball;
            this.ballArrow = ballArrow;
            this.leftGoal = leftGoal;
            this.rightGoal = rightGoal;
            this.leftGoalFoulZone = leftGoalFoulZone;
            this.rightGoalFoulZone = rightGoalFoulZone;
            this.leftErrorBar = leftErrorBar;
            this.rightErrorBar = rightErrorBar;
        }
    }
}
