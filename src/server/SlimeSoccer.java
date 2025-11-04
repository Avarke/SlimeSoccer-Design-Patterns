package server;

import common.GameConfiguration;

import common.net.GameStateJson;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.swing.SwingUtilities;



import server.builder.BallDirector;
import server.builder.ClassicBallBuilder;
import server.builder.HeavyBallBuilder;
import server.factory.DefaultGameFactory;
import server.factory.DefaultGoalFactory;
import server.factory.IGameFactory;
import server.strategy.BallPhysicsStrategies;

public class SlimeSoccer {
    Window window;
    Slime player1, player2, player3, player4, smile;
    Rectangle background, floor, leftGoalFoulZone, rightGoalFoulZone, leftErrorBar, rightErrorBar, test;
    Ball ball, ballArrow;
    Goal leftGoal, rightGoal;
    Text goalScoredText, foulText, team1ScoreText, team2ScoreText, fpsCounter;
    Picture image;
    ArrayList<ClientData> clients;
    Font scoreFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 80);

    static int gamestate;
    static boolean goalScored = false;
    static boolean foul = false;
    static boolean runGame = true;
    final static int SCREENWIDTH = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
    final static int SCREENHEIGHT = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
    final static int SCREENRESOLUTION = SCREENWIDTH*SCREENHEIGHT;

    static int player1Score = 0;
    static int player2Score = 0;
    private long autoResetAtMs = 0;

    // throttle server painting to reduce CPU/GPU
    private static final long PAINT_INTERVAL_NS = 16_666_667L; // ~60 FPS
    private long lastPaintNs = 0;

    // Power-ups
    PowerUpManager powerUps;
    private final GameConfiguration configuration;

    public SlimeSoccer() {
        this(GameConfiguration.builder().build());
    }

    public SlimeSoccer(GameConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");

        // Initialize game objects BEFORE showing the window to avoid nulls on first paint
        init();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                SlimeSoccer slimeSoccer;
                Runnable init(SlimeSoccer slimeSoccer) { this.slimeSoccer = slimeSoccer; return this; }
                @Override public void run() { window = new Window(slimeSoccer); }
            }.init(this));
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }

        clients = new ArrayList<ClientData>();
        new Thread(new ConnectionReceiverRunnable(this, configuration.getPort())).start();

        while (true) {
            if (runGame) {
                long nowNs = System.nanoTime();
                tick();
                sendData();
                if (nowNs - lastPaintNs >= PAINT_INTERVAL_NS) {
                    window.repaint();
                    lastPaintNs = nowNs;
                }
                ball.crossBarCheck();
                try { Thread.sleep(16); } catch (Exception e) {}
            } else {
                if (Window.reset || (autoResetAtMs > 0 && System.currentTimeMillis() >= autoResetAtMs)) {
                    reset();
                    autoResetAtMs = 0;
                }
                try { Thread.sleep(16); } catch (Exception e) {}
            }
        }
    }

    public void init() {
        IGameFactory factory = DefaultGameFactory.INSTANCE;
        player1 = factory.createSlime(Window.WIDTH/2 - (2*Window.WIDTH/5), 0.814*Window.HEIGHT, Color.GREEN, true);
        player2 = factory.createSlime(Window.WIDTH/2 - (Window.WIDTH/5), 0.814*Window.HEIGHT, Color.CYAN, true);
        player3 = factory.createSlime(Window.WIDTH/2 + (Window.WIDTH/5), 0.814*Window.HEIGHT, Color.RED, false);
        player4 = factory.createSlime(Window.WIDTH/2 + (2*Window.WIDTH/5), 0.814*Window.HEIGHT, new Color(255, 110, 20), false);
        background = factory.createRectangle(0, 0, Window.WIDTH, Window.HEIGHT, Color.BLUE);
        floor = factory.createRectangle(0, 0.814*Window.HEIGHT, Window.WIDTH, Window.HEIGHT - 0.814*Window.HEIGHT, Color.GRAY);

        BallDirector director = new BallDirector();

        ball = director.construct(
                new ClassicBallBuilder()
                        .radius(20)
                        .physics(BallPhysicsStrategies.normal()),
                Window.WIDTH / 2, 0.278 * Window.HEIGHT
        );

        ballArrow = director.construct(
                new HeavyBallBuilder()
                        .radius(20),
                Window.WIDTH / 2, 0.046 * Window.HEIGHT
        );


        DefaultGoalFactory goalFactory = new DefaultGoalFactory();
        leftGoal = goalFactory.createGoal(0, 0.667*Window.HEIGHT, true);
        rightGoal = goalFactory.createGoal(0.952*Window.WIDTH, 0.667*Window.HEIGHT, false);

        leftGoalFoulZone = factory.createRectangle(0, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        rightGoalFoulZone = factory.createRectangle(0.896*Window.WIDTH, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        leftErrorBar = factory.createRectangle(0, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player1.getColor());
        rightErrorBar = factory.createRectangle(Window.WIDTH/2, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player4.getColor());
        goalScoredText = factory.createText("GOAL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, null);
        foulText = factory.createText("FOUL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, null);
        team1ScoreText = factory.createText("" + player1Score, 0.026*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, null);
        team2ScoreText = factory.createText("" + player2Score, 0.885*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, null);
        gamestate = 1;

        powerUps = factory.createPowerUpManager();
    }

    public void draw(Graphics g) {
        background.draw(g);
        floor.draw(g);
        if(goalScored) goalScoredText.drawString(g);
        if(foul) foulText.drawString(g);

        team1ScoreText.drawString(g);
        team2ScoreText.drawString(g);
        leftGoalFoulZone.draw(g);
        rightGoalFoulZone.draw(g);
        leftErrorBar.draw(g);
        rightErrorBar.draw(g);

        player1.draw(g, ball.getX(), ball.getY());
        player2.draw(g, ball.getX(), ball.getY());
        player3.draw(g, ball.getX(), ball.getY());
        player4.draw(g, ball.getX(), ball.getY());

        powerUps.draw(g);
        ball.draw(g);

        leftGoal.draw(g);
        rightGoal.draw(g);
    }

    void controls() {
        if(Window.playerOneRight && !Window.playerOneLeft) {
            player1.setVelX(7);
        }
        else if(Window.playerOneLeft && !Window.playerOneRight) {
            player1.setVelX(-7);
        }
        else if((!Window.playerOneLeft && !Window.playerOneRight) || (Window.playerOneLeft && Window.playerOneRight)) {
            player1.setVelX(0);
        }

        if(Window.playerTwoRight && !Window.playerTwoLeft) {
            player2.setVelX(7);
        }
        else if(Window.playerTwoLeft && !Window.playerTwoRight) {
            player2.setVelX(-7);
        }
        else if((!Window.playerTwoLeft && !Window.playerTwoRight) || (Window.playerTwoLeft && Window.playerTwoRight)) {
            player2.setVelX(0);
        }

        if(Window.playerThreeRight && !Window.playerThreeLeft) {
            player3.setVelX(7);
        }
        else if(Window.playerThreeLeft && !Window.playerThreeRight) {
            player3.setVelX(-7);
        }
        else if((!Window.playerThreeLeft && !Window.playerThreeRight) || (Window.playerThreeLeft && Window.playerThreeRight)) {
            player3.setVelX(0);
        }

        if(Window.playerFourRight && !Window.playerFourLeft) {
            player4.setVelX(7);
        }
        else if(Window.playerFourLeft && !Window.playerFourRight) {
            player4.setVelX(-7);
        }
        else if((!Window.playerFourLeft && !Window.playerFourRight) || (Window.playerFourLeft && Window.playerFourRight)) {
            player4.setVelX(0);
        }

        if(Window.playerOneJump) {
            player1.jump();
        }
        if(Window.playerTwoJump) {
            player2.jump();
        }
        if(Window.playerThreeJump) {
            player3.jump();
        }
        if(Window.playerFourJump) {
            player4.jump();
        }
    }

    public void tick() {
        player1.setX(player1.getX() + player1.getVelX()); player1.updateEyes();
        player2.setX(player2.getX() + player2.getVelX()); player2.updateEyes();
        player3.setX(player3.getX() + player3.getVelX()); player3.updateEyes();
        player4.setX(player4.getX() + player4.getVelX()); player4.updateEyes();

        Maths.bounceBallOffSlime(ball, player1);
        Maths.bounceBallOffSlime(ball, player2);
        Maths.bounceBallOffSlime(ball, player3);
        Maths.bounceBallOffSlime(ball, player4);

        controls();

        if (powerUps != null) powerUps.update(ball, player1, player2, player3, player4);

        if (ball.getY() >= leftGoal.getY() &&
                (ball.getX() <= leftGoal.getX() + leftGoal.getWidth() || ball.getX() >= rightGoal.getX())) {
            for (int i = 0; i < 10; i++) {
                if (!runGame) break;
                ball.update(1);
                ball.boundaries();
                ball.crossBarCheck();
            }
        } else {
            ball.update(10);
            ball.boundaries();
            ball.crossBarCheck();
        }

        // --- Goal collision detection: one-way posts ---
        double lgx = leftGoal.getX(),  lgy = leftGoal.getY();
        double lgw = leftGoal.getWidth(), lgh = leftGoal.getHeight();
        double rgx = rightGoal.getX(), rgy = rightGoal.getY();
        double rgw = rightGoal.getWidth(), rgh = rightGoal.getHeight();
        double bt  = leftGoal.getBarThickness();
        double bx = ball.getX(), by = ball.getY(), br = ball.getRadius();

        // Post/crossbar geometry
        double lPostX = lgx + lgw, lPostY = lgy - 5, lPostW = bt, lPostH = lgh;
        double rPostX = rgx - bt,  rPostY = rgy - 5, rPostW = bt, rPostH = rgh;
        double lBarX  = lgx,       lBarY  = lgy - 5, lBarW  = lgw, lBarH  = bt;
        double rBarX  = rgx,       rBarY  = rgy - 5, rBarW  = rgw, rBarH  = bt;

        // Left back post (behind net)
        if (Maths.circleIntersectsRect(bx, by, br, lgx + lgw - bt, lgy - 5, bt, lgh)) {
            double newX = lgx + lgw - br - 1;
            if (Math.abs(ball.getX() - newX) > 0.5) {
                ball.setX(newX);
                ball.setVelX(-Math.abs(ball.getVelX()) * 0.7); // dampen bounce
            } else {
                ball.setVelX(0);
            }
        }

        // Left crossbar (top)
        if (Maths.circleIntersectsRect(bx, by, br, lgx, lgy - 5, lgw, bt)) {
            ball.setY(lgy - br - 1);
            ball.setVelY(-Math.abs(ball.getVelY()));
        }

        // Right back post
        if (Maths.circleIntersectsRect(bx, by, br, rgx, rgy - 5, bt, rgh)) {
            double newX = rgx + br + 1;
            if (Math.abs(ball.getX() - newX) > 0.5) {
                ball.setX(newX);
                ball.setVelX(Math.abs(ball.getVelX()) * 0.7); // dampen bounce
            } else {
                ball.setVelX(0);
            }
        }

        // Right crossbar
        if (Maths.circleIntersectsRect(bx, by, br, rgx, rgy - 5, rgw, bt)) {
            ball.setY(rgy - br - 1);
            ball.setVelY(-Math.abs(ball.getVelY()));
        }

        // --- Player physics ---
        player1.downMovement(); player1.floorCheck(); player1.gravity();
        if (player1.foulCheckLeft() || player2.foulCheckLeft() || player1.foulCheckRight() || player2.foulCheckRight())
            leftErrorBar.shrinkLeft();
        else
            leftErrorBar.setWidth(Window.WIDTH / 2);

        player2.downMovement(); player2.floorCheck(); player2.gravity();

        player3.downMovement(); player3.floorCheck(); player3.gravity();
        if (player3.foulCheckRight() || player4.foulCheckRight() || player3.foulCheckLeft() || player4.foulCheckLeft()) {
            rightErrorBar.shrinkRight();
        } else {
            rightErrorBar.setWidth(Window.WIDTH / 2);
            rightErrorBar.setX(Window.WIDTH / 2);
        }

        player4.downMovement(); player4.floorCheck(); player4.gravity();

        // --- Foul checks ---
        if (rightErrorBar.getWidth() < 1) {
            if (gamestate == 1) player1Score++;
            foul = true; goalScored = false;
            runGame = false;
            if (autoResetAtMs == 0) autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
        if (leftErrorBar.getWidth() < 1) {
            if (gamestate == 1) player2Score++;
            foul = true; goalScored = false;
            runGame = false;
            if (autoResetAtMs == 0) autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }

        // --- Goal detection: whole ball must cross plane inside mouth ---
        boolean inLeftMouthY  = by - br >= lgy && by + br <= lgy + lgh;
        boolean inRightMouthY = by - br >= rgy && by + br <= rgy + rgh;

        boolean leftGoalScored  = inLeftMouthY  && (bx + br < lgx + lgw);
        boolean rightGoalScored = inRightMouthY && (bx - br > rgx);

        if (leftGoalScored) {
            if (gamestate == 1) player2Score++;
            goalScored = true; foul = false;
            runGame = false;
            if (autoResetAtMs == 0)
                autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
        if (rightGoalScored) {
            if (gamestate == 1) player1Score++;
            goalScored = true; foul = false;
            runGame = false;
            if (autoResetAtMs == 0)
                autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
    }

    void reset() {
        init();
        Window.reset = false;
        goalScored = false;
        foul = false;
        runGame = true;
        gamestate = 1;
        autoResetAtMs = 0;
        if (powerUps != null) powerUps.clearAll(ball);
    }

    public void sendData() {
        int effectCode = (powerUps == null) ? 0 : powerUps.getCurrentEffectCode();
        List<PowerUp> visiblePowerUps = (powerUps == null) ? Collections.emptyList() : powerUps.getVisiblePowerUps();

        GameStateJson.PlayerState[] players = new GameStateJson.PlayerState[]{
                new GameStateJson.PlayerState(player1.getX(), player1.getY(), player1.isFacingRight(), player1.getColor().getRGB()),
                new GameStateJson.PlayerState(player2.getX(), player2.getY(), player2.isFacingRight(), player2.getColor().getRGB()),
                new GameStateJson.PlayerState(player3.getX(), player3.getY(), player3.isFacingRight(), player3.getColor().getRGB()),
                new GameStateJson.PlayerState(player4.getX(), player4.getY(), player4.isFacingRight(), player4.getColor().getRGB())
        };

        List<GameStateJson.PowerUpState> powerStates = new ArrayList<>(visiblePowerUps.size());
        for (PowerUp powerUp : visiblePowerUps) {
            powerStates.add(new GameStateJson.PowerUpState(
                    powerUp.getX(),
                    powerUp.getY(),
                    powerUp.getRadius(),
                    powerUp.getColor().getRGB()
            ));
        }

        GameStateJson.State state = new GameStateJson.State(
                players,
                ball.getX(),
                ball.getY(),
                effectCode,
                leftErrorBar.getWidth(),
                rightErrorBar.getWidth(),
                rightErrorBar.getX(),
                player1Score,
                player2Score,
                goalScored,
                foul,
                powerStates
        );

        String payload = GameStateJson.encode(state);
        for (ClientData client : clients) {
            client.getOutputStream().println(payload);
        }
    }

    public static void main(String[] args) {
        new SlimeSoccer();
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }
}
