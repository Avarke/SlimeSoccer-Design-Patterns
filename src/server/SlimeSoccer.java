package server;

import common.GameConfiguration;
import common.facade.SlimeSoccerFacade;

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

import server.render.GraphicsSlimeRenderBridge;
import server.render.SlimeRenderBridge;

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

    // Power-ups
    PowerUpManager powerUps;
    private final GameConfiguration configuration;

    public SlimeSoccer() {
        this(GameConfiguration.builder().build());
    }

    public SlimeSoccer(GameConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
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
        init();
        while (true) {
            if (runGame) {
                tick();
                sendData();
                window.repaint();
                ball.crossBarCheck();
                try { Thread.sleep(16); } catch (Exception e) {}
            } else {
                // Auto-restart after configured delay, or allow Enter to force reset
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

        ball = factory.createBall(BallType.NORMAL, Window.WIDTH/2, 0.278*Window.HEIGHT);
        ball.setRadius(20);

        ballArrow = factory.createBall(BallType.NORMAL, Window.WIDTH/2, 0.046*Window.HEIGHT);
        ballArrow.setRadius(20);

        leftGoal = factory.createGoal(0, 0.667*Window.HEIGHT, true);
        rightGoal = factory.createGoal(0.952*Window.WIDTH, 0.667*Window.HEIGHT, false);
        leftGoalFoulZone = factory.createRectangle(0, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        rightGoalFoulZone = factory.createRectangle(0.896*Window.WIDTH, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        leftErrorBar = factory.createRectangle(0, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player1.getColor());
        rightErrorBar = factory.createRectangle(Window.WIDTH/2, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player4.getColor());
        goalScoredText = factory.createText("GOAL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, null);
        foulText = factory.createText("FOUL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, null);
        team1ScoreText = factory.createText("" + player1Score, 0.026*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, null);
        team2ScoreText = factory.createText("" + player2Score, 0.885*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, null);
        gamestate = 1;

        // power-ups
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

        SlimeRenderBridge slimeBridge = new GraphicsSlimeRenderBridge(g);
        player1.draw(slimeBridge, ball.getX(), ball.getY());
        player2.draw(slimeBridge, ball.getX(), ball.getY());
        player3.draw(slimeBridge, ball.getX(), ball.getY());
        player4.draw(slimeBridge, ball.getX(), ball.getY());

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
        // player movement & eye updates
        player1.setX(player1.getX() + player1.getVelX()); player1.updateEyes();
        player2.setX(player2.getX() + player2.getVelX()); player2.updateEyes();
        player3.setX(player3.getX() + player3.getVelX()); player3.updateEyes();
        player4.setX(player4.getX() + player4.getVelX()); player4.updateEyes();

        // collisions with ball
        Maths.bounceBallOffSlime(ball, player1);
        Maths.bounceBallOffSlime(ball, player2);
        Maths.bounceBallOffSlime(ball, player3);
        Maths.bounceBallOffSlime(ball, player4);

        // input
        controls();

        // power-ups (if you added PowerUpManager previously)
        if (powerUps != null) powerUps.update(ball, player1, player2, player3, player4);

        // ball physics
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

        // slimes physics
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

        // fouls -> stop and schedule auto reset
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

        // goals -> stop and schedule auto reset
        if (ball.getX() < leftGoal.getX() + leftGoal.getWidth() && ball.getY() > leftGoal.getY()) {
            if (gamestate == 1) player2Score++;
            goalScored = true; foul = false;
            runGame = false;
            if (autoResetAtMs == 0) autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
        if (ball.getX() > rightGoal.getX() && ball.getY() > rightGoal.getY()) {
            if (gamestate == 1) player1Score++;
            goalScored = true; foul = false;
            runGame = false;
            if (autoResetAtMs == 0) autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
    }

    void reset() {
        init();                 // repositions objects; scores persist
        Window.reset = false;
        goalScored = false;
        foul = false;
        runGame = true;
        gamestate = 1;
        autoResetAtMs = 0;      // clear timer
        if (powerUps != null) powerUps.clearAll(ball); // normalize physics/effects if used
    }

    public void sendData() {
        int effectCode = (powerUps == null) ? 0 : powerUps.getCurrentEffectCode(); // 0..3
        List<PowerUp> visiblePowerUps = (powerUps == null) ? Collections.emptyList() : powerUps.getVisiblePowerUps();

        StringBuilder builder = new StringBuilder(256);
        for(ClientData client : clients) {
            builder.setLength(0);
            builder.append(player1.getX()).append(' ').append(player1.getY()).append(' ').append(player1.isFacingRight()).append(' ')
                    .append(player2.getX()).append(' ').append(player2.getY()).append(' ').append(player2.isFacingRight()).append(' ')
                    .append(player3.getX()).append(' ').append(player3.getY()).append(' ').append(player3.isFacingRight()).append(' ')
                    .append(player4.getX()).append(' ').append(player4.getY()).append(' ').append(player4.isFacingRight()).append(' ')
                    .append(ball.getX()).append(' ').append(ball.getY()).append(' ')
                    .append(player1.getColor().getRGB()).append(' ').append(player2.getColor().getRGB()).append(' ')
                    .append(player3.getColor().getRGB()).append(' ').append(player4.getColor().getRGB()).append(' ')
                    .append(goalScored).append(' ').append(foul).append(' ')
                    .append(leftErrorBar.getWidth()).append(' ').append(rightErrorBar.getWidth()).append(' ').append(rightErrorBar.getX()).append(' ')
                    .append(player1Score).append(' ').append(player2Score).append(' ')
                    .append(effectCode).append(' ').append(visiblePowerUps.size());

            for (PowerUp powerUp : visiblePowerUps) {
                builder.append(' ')
                        .append(powerUp.getX()).append(' ')
                        .append(powerUp.getY()).append(' ')
                        .append(powerUp.getRadius()).append(' ')
                        .append(powerUp.getColor().getRGB());
            }

            client.getOutputStream().println(builder.toString());
        }
    }

    public static void main(String[] args) {
        SlimeSoccerFacade.launchServer();
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }
}
