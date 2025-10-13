package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

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

    private boolean kickThisFrame = false;

    // Power-ups
    PowerUpManager powerUps;

    public SlimeSoccer() {
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
        new Thread(new ConnectionReceiverRunnable(this)).start();
        init();
        while (true) {
            if (runGame) {
                tick();
                sendData();
                window.repaint();
                ball.crossBarCheck();
                try { Thread.sleep(16); } catch (Exception e) {}
            } else {
                // Auto-restart after ~2.5s, or allow Enter to force reset
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
        background =  new Rectangle(0, 0, Window.WIDTH, Window.HEIGHT, Color.BLUE);
        floor = new Rectangle(0, 0.814*Window.HEIGHT, Window.WIDTH, Window.HEIGHT - 0.814*Window.HEIGHT, Color.GRAY);

        ball = factory.createBall(BallType.NORMAL, Window.WIDTH/2, 0.278*Window.HEIGHT);
        ball.setRadius(20);

        ballArrow = factory.createBall(BallType.NORMAL, Window.WIDTH/2, 0.046*Window.HEIGHT);
        ballArrow.setRadius(20);

        leftGoal = new Goal(0, 0.667*Window.HEIGHT, true);
        rightGoal = new Goal(0.952*Window.WIDTH, 0.667*Window.HEIGHT, false);
        leftGoalFoulZone = new Rectangle(0, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        rightGoalFoulZone = new Rectangle(0.896*Window.WIDTH, 0.835*Window.HEIGHT, 0.104*Window.WIDTH, 0.009*Window.HEIGHT, Color.WHITE);
        leftErrorBar = new Rectangle(0, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player1.getColor());
        rightErrorBar = new Rectangle(Window.WIDTH/2, 0.861*Window.HEIGHT, Window.WIDTH/2, 10, player4.getColor());
        goalScoredText = new Text("GOAL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        foulText = new Text("FOUL!", 0.286*Window.WIDTH, 0.278*Window.HEIGHT, (int) (0.278*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        team1ScoreText = new Text("" + player1Score, 0.026*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        team2ScoreText = new Text("" + player2Score, 0.885*Window.WIDTH, 0.093*Window.HEIGHT, (int) (0.074*Window.HEIGHT), Color.WHITE, "Franklin Gothic Medium Italic");
        gamestate = 1;

        // power-ups
        powerUps = new PowerUpManager();
    }

    public void draw(Graphics g) {
        background.draw(g);
        floor.draw(g);
        if (goalScored) goalScoredText.drawString(g);
        if (foul)       foulText.drawString(g);

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
        //ballArrow.draw(g);

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
        // this-frame kick flag (cleared every tick)
        kickThisFrame = false;

        // Removed: ballArrow.setX(ball.getX());
        // Removed: ballArrow.setRadius((int) -(ball.getY()) / 50);

        player1.setX(player1.getX() + player1.getVelX()); player1.updateEyes();
        player2.setX(player2.getX() + player2.getVelX()); player2.updateEyes();
        player3.setX(player3.getX() + player3.getVelX()); player3.updateEyes();
        player4.setX(player4.getX() + player4.getVelX()); player4.updateEyes();

        // Accumulate if any slime actually bounces the ball this frame
        kickThisFrame |= Maths.bounceBallOffSlime(ball, player1);
        kickThisFrame |= Maths.bounceBallOffSlime(ball, player2);
        kickThisFrame |= Maths.bounceBallOffSlime(ball, player3);
        kickThisFrame |= Maths.bounceBallOffSlime(ball, player4);

        controls();

        // Power-ups
        if (powerUps != null) powerUps.update(ball, player1, player2, player3, player4);

        // Increase update freq if near goalpost.
        if (ball.getY() >= leftGoal.getY() &&
                (ball.getX() <= leftGoal.getX() + leftGoal.getWidth() || ball.getX() >= rightGoal.getX())) {
            for (int i = 0; i < 10; i++) {
                if (runGame) {
                    ball.update(1);
                    ball.boundaries();
                    ball.crossBarCheck();
                }
            }
        } else {
            ball.update(10);
            ball.boundaries();
            ball.crossBarCheck();
        }

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

        if (rightErrorBar.getWidth() < 1) {
            if (gamestate == 1) player1Score++;
            foul = true; runGame = false;
        }
        if (leftErrorBar.getWidth() < 1) {
            if (gamestate == 1) player2Score++;
            foul = true; runGame = false;
        }
        if (ball.getX() < leftGoal.getX() + leftGoal.getWidth() && ball.getY() > leftGoal.getY()) {
            if (gamestate == 1) player2Score++;
            goalScored = true; runGame = false;
        }
        if (ball.getX() > rightGoal.getX() && ball.getY() > rightGoal.getY()) {
            if (gamestate == 1) player1Score++;
            goalScored = true; runGame = false;
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
        kickThisFrame = false;
        if (powerUps != null) powerUps.clearAll(ball); // normalize physics/effects if used
    }

    public void sendData() {
        // Snapshot power-ups (limit to 4 to keep packet small)
        java.util.List<PowerUp> pus =
                (powerUps == null) ? java.util.Collections.emptyList() : powerUps.getItemsSnapshot();
        int count = Math.min(pus.size(), 4);

        for (ClientData client : clients) {
            StringBuilder sb = new StringBuilder(256);
            sb.append(player1.getX()).append(' ').append(player1.getY()).append(' ').append(player1.isFacingRight()).append(' ')
                    .append(player2.getX()).append(' ').append(player2.getY()).append(' ').append(player2.isFacingRight()).append(' ')
                    .append(player3.getX()).append(' ').append(player3.getY()).append(' ').append(player3.isFacingRight()).append(' ')
                    .append(player4.getX()).append(' ').append(player4.getY()).append(' ').append(player4.isFacingRight()).append(' ')
                    .append(ball.getX()).append(' ').append(ball.getY()).append(' ')
                    .append(player1.getColor().getRGB()).append(' ').append(player2.getColor().getRGB()).append(' ')
                    .append(player3.getColor().getRGB()).append(' ').append(player4.getColor().getRGB()).append(' ')
                    .append(goalScored).append(' ').append(foul).append(' ')
                    .append(leftErrorBar.getWidth()).append(' ').append(rightErrorBar.getWidth()).append(' ').append(rightErrorBar.getX()).append(' ')
                    .append(player1Score).append(' ').append(player2Score).append(' ')
                    .append(kickThisFrame ? 1 : 0).append(' ')
                    .append(count);

            for (int i = 0; i < count; i++) {
                PowerUp p = pus.get(i);
                int typeCode;
                switch (p.getType()) {
                    case LOW_GRAVITY:      typeCode = 1; break;
                    case HEAVY:            typeCode = 2; break;
                    case REVERSE_GRAVITY:  typeCode = 3; break;
                    default:               typeCode = 0; break;
                }
                sb.append(' ')
                        .append(p.getX()).append(' ')
                        .append(p.getY()).append(' ')
                        .append(typeCode).append(' ')
                        .append(p.getRadius());
            }

            client.getOutputStream().println(sb.toString());
        }
    }

    public static void main(String[] args) {
        new SlimeSoccer();
    }
}