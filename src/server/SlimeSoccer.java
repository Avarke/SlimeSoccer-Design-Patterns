package server;

import common.GameConfiguration;

import common.net.GameStateJson;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import server.achievements.PlayerAchievements;
import server.builder.BallBuilder;
import server.factory.DefaultGameFactory;
import server.factory.DefaultGoalFactory;
import server.factory.IGameFactory;
import server.model.World;
import server.model.MatchParticipants;
import server.strategy.BallPhysicsStrategies;
import server.template.AbstractMatchController;
import server.template.StandardMatchController;
import server.template.TrainingMatchController;


import server.chat.ChatMediator;
import server.chat.GameChatMediator;
import common.chat.ChatInterpreter;
import common.chat.ExampleChatInterpreter;
import server.Interpreter.ReportCommand;
import server.Visitor.GameReportVisitor;

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
    final static int SCREENRESOLUTION = SCREENWIDTH * SCREENHEIGHT;

    static int player1Score = 0;
    static int player2Score = 0;
    private long autoResetAtMs = 0;
    // Hot player tracking
    private final int[] slotGoals = new int[5];
    private final int[] slotHotRank = new int[5];
    private final double[] slotSpeedBoost = new double[5];

    // Keep a reference to the active game instance for helper methods
    private static SlimeSoccer ACTIVE_INSTANCE;

    // throttle server painting to reduce CPU/GPU
    private static final long PAINT_INTERVAL_NS = 16_666_667L; // ~60 FPS
    private long lastPaintNs = 0;
    private final boolean[] slotTaken = new boolean[5];
    private final String[] slotNicknames = new String[5];
    private final ChatMediator chatMediator;



    // Power-ups
    PowerUpManager powerUps;
    private final GameConfiguration configuration;
    private final boolean trainingMode;
    private AbstractMatchController matchController;
    private World world;
    private MatchParticipants participants;
    private final Map<Integer, PlayerAchievements> achievementsBySlot = new HashMap<>();


    public SlimeSoccer() {
        this(GameConfiguration.builder().build());
    }

    public Slime getSlimeForSlot(int slot)
    {
        switch (slot) {
            case 1: return player1;
            case 2: return player2;
            case 3: return player3;
            case 4: return player4;
            default: return null;
        }
    }

    public void setNicknameForSlot(int slot, String nickname) {
        if (slot < 1 || slot > 4) return;
        slotNicknames[slot] = (nickname != null) ? nickname : "";

        Slime s = getSlimeForSlot(slot);
        if (s != null) {
            s.setNickname(slotNicknames[slot]);
        }
    }

    /**
     * Sets the scoreboard values (used by /setscore). Also refreshes score texts if available.
     */
    public static void setScores(int team1, int team2) {
        if (team1 < 0 || team2 < 0) return; // ignore invalid
        player1Score = team1;
        player2Score = team2;
        // Try to refresh text objects if an instance exists
        SlimeSoccer instance = ACTIVE_INSTANCE;
        if (instance != null) {
            if (instance.team1ScoreText != null) {
                instance.team1ScoreText.setContent(String.valueOf(team1));
            }
            if (instance.team2ScoreText != null) {
                instance.team2ScoreText.setContent(String.valueOf(team2));
            }
        }
    }

    private void restoreNicknames() {
        for (int slot = 1; slot <= 4; slot++) {
            String n = slotNicknames[slot];
            if (n != null && !n.isEmpty()) {
                Slime s = getSlimeForSlot(slot);
                if (s != null) {
                    s.setNickname(n);
                }
            }
        }
    }

    public void registerPlayerAchievements(int slot, ClientData client) {
        PlayerAchievements pa = new PlayerAchievements(client, chatMediator);
        achievementsBySlot.put(slot, pa);
    }

    public void onPlayerScored(int slot) {
        PlayerAchievements pa = achievementsBySlot.get(slot);
        if (pa != null) {
            pa.onPlayerScored();
        }
        if (slot >= 1 && slot < slotGoals.length) {
            slotGoals[slot]++;
        }
    }

    public void onPlayerJumped(int slot) {
        PlayerAchievements pa = achievementsBySlot.get(slot);
        if (pa != null) {
            pa.onPlayerJumped();
        }
    }

    private Iterable<Slime> hotPlayersByGoals() {
        return new Iterable<Slime>() {
            @Override
            public Iterator<Slime> iterator() {
                ArrayList<Slime> list = new ArrayList<>();
                for (int slot = 1; slot <= 4; slot++) {
                    Slime s = getSlimeForSlot(slot);
                    if (s != null) list.add(s);
                }
                list.sort(new Comparator<Slime>() {
                    @Override
                    public int compare(Slime a, Slime b) {
                        int ga = (a != null) ? slotGoals[a.getSlot()] : 0;
                        int gb = (b != null) ? slotGoals[b.getSlot()] : 0;
                        return Integer.compare(gb, ga); // descending
                    }
                });
                return list.iterator();
            }
        };
    }

    /** PriorityQueue-backed iterable (goals desc, stamina tiebreak). */
    private Iterable<Slime> hotPlayersPriority() {
        return new Iterable<Slime>() {
            @Override
            public Iterator<Slime> iterator() {
                java.util.PriorityQueue<Slime> pq = new java.util.PriorityQueue<>(
                        new java.util.Comparator<Slime>() {
                            @Override
                            public int compare(Slime a, Slime b) {
                                int ga = (a != null) ? slotGoals[a.getSlot()] : 0;
                                int gb = (b != null) ? slotGoals[b.getSlot()] : 0;
                                if (ga != gb) return Integer.compare(gb, ga);
                                double sa = (a != null) ? a.getStamina() : 0;
                                double sb = (b != null) ? b.getStamina() : 0;
                                return Double.compare(sb, sa); // higher stamina first
                            }
                        });
                for (int slot = 1; slot <= 4; slot++) {
                    Slime s = getSlimeForSlot(slot);
                    if (s != null) pq.add(s);
                }
                return pq.iterator();
            }
        };
    }

    /** First available player (stable fallback when no goals yet). */
    private Slime firstAvailablePlayer() {
        for (int slot = 1; slot <= 4; slot++) {
            Slime s = getSlimeForSlot(slot);
            if (s != null) return s;
        }
        return null;
    }

    private void recomputeHotPlayerBoosts() {
        Arrays.fill(slotSpeedBoost, 1.0);
        Arrays.fill(slotHotRank, 0);

        // Rank players by goals using ArrayList + sort (iterator #1)
        int rank = 0;
        for (Slime s : hotPlayersByGoals()) {
            if (s == null) continue;
            slotHotRank[s.getSlot()] = ++rank;
        }

        // Assign boosts using PriorityQueue order (iterator #2)
        double[] boosts = new double[] {1.10, 1.05, 1.02};
        int idx = 0;
        for (Slime s : hotPlayersPriority()) {
            if (s == null) continue;
            int goals = slotGoals[s.getSlot()];
            if (goals <= 0 || idx >= boosts.length) {
                continue;
            }
            slotSpeedBoost[s.getSlot()] = boosts[idx];
            idx++;
        }

        // If no one has scored yet, leave boosts neutral (no visual highlight)
    }

    private int getHotLevelForSlot(int slot) {
        if (slot < 0 || slot >= slotHotRank.length) return 0;
        int rank = slotHotRank[slot];
        int goals = slotGoals[slot];
        if (rank == 1 && goals > 0) return 3;
        if (rank == 2 && goals > 0) return 2;
        if (rank == 3 && goals > 0) return 1;
        return 0;
    }

    private int lastBallTouchingSlot = -1;

    public void setLastBallTouchingSlot(int slot) {
        lastBallTouchingSlot = slot;
    }

    public int getLastBallTouchingSlot() {
        return lastBallTouchingSlot;
    }



    public SlimeSoccer(GameConfiguration configuration) {
        ACTIVE_INSTANCE = this;
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.trainingMode = configuration.isTrainingMatch();
        Arrays.fill(slotSpeedBoost, 1.0);

        // Initialize game objects BEFORE showing the window to avoid nulls on first
        // paint
        init();

        ChatInterpreter interpreter = new ExampleChatInterpreter(); // replace with real later
        this.chatMediator = new GameChatMediator(interpreter);

        slotTaken[1] = true;

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                SlimeSoccer slimeSoccer;

                Runnable init(SlimeSoccer slimeSoccer) {
                    this.slimeSoccer = slimeSoccer;
                    return this;
                }

                @Override
                public void run() {
                    window = new Window(slimeSoccer);
                }
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
                matchController.updateMatch();
                sendData();
                if (nowNs - lastPaintNs >= PAINT_INTERVAL_NS) {
                    window.repaint();
                    lastPaintNs = nowNs;
                }
                ball.crossBarCheck();
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
            } else {
                if (Window.reset || (autoResetAtMs > 0 && System.currentTimeMillis() >= autoResetAtMs)) {
                    reset();
                    autoResetAtMs = 0;
                }
                try {
                    Thread.sleep(16);
                } catch (Exception e) {
                }
            }
        }
    }

    public void init() {
        matchController = configuration.isTrainingMatch()
                ? new TrainingMatchController()
                : new StandardMatchController();
        IGameFactory factory = DefaultGameFactory.INSTANCE;
        player1 = factory.createSlime(Window.WIDTH / 2 - (2 * Window.WIDTH / 5), 0.814 * Window.HEIGHT, Color.GREEN, true);
        player1.setSlot(1);

        player2 = factory.createSlime(Window.WIDTH / 2 - (Window.WIDTH / 5), 0.814 * Window.HEIGHT, Color.CYAN, true);
        player2.setSlot(2);

        player3 = factory.createSlime(Window.WIDTH / 2 + (Window.WIDTH / 5), 0.814 * Window.HEIGHT, Color.RED, false);
        player3.setSlot(3);


        player4 = factory.createSlime(Window.WIDTH / 2 + (2 * Window.WIDTH / 5), 0.814 * Window.HEIGHT, new Color(255, 110, 20), false);
        player4.setSlot(4);

        background = factory.createRectangle(0, 0, Window.WIDTH, Window.HEIGHT, Color.BLUE);
        floor = factory.createRectangle(0, 0.814 * Window.HEIGHT, Window.WIDTH, Window.HEIGHT - 0.814 * Window.HEIGHT,
                Color.GRAY);

        ball = new BallBuilder()

                .atPosition(Window.WIDTH / 2, 0.278 * Window.HEIGHT)
                .withRadius(20)
                .withPhysicsStrategy(BallPhysicsStrategies.normal())
                .build();

        ballArrow = new BallBuilder()

                .atPosition(Window.WIDTH / 2, 0.046 * Window.HEIGHT)
                .withRadius(20)
                .build();

        DefaultGoalFactory goalFactory = new DefaultGoalFactory();
        leftGoal = goalFactory.createGoal(0, 0.667 * Window.HEIGHT, true);
        rightGoal = goalFactory.createGoal(0.952 * Window.WIDTH, 0.667 * Window.HEIGHT, false);

        leftGoalFoulZone = factory.createRectangle(0, 0.835 * Window.HEIGHT, 0.104 * Window.WIDTH,
                0.009 * Window.HEIGHT, Color.WHITE);
        rightGoalFoulZone = factory.createRectangle(0.896 * Window.WIDTH, 0.835 * Window.HEIGHT, 0.104 * Window.WIDTH,
                0.009 * Window.HEIGHT, Color.WHITE);
        leftErrorBar = factory.createRectangle(0, 0.861 * Window.HEIGHT, Window.WIDTH / 2, 10, player1.getColor());
        rightErrorBar = factory.createRectangle(Window.WIDTH / 2, 0.861 * Window.HEIGHT, Window.WIDTH / 2, 10,
                player4.getColor());
        goalScoredText = factory.createText("GOAL!", 0.286 * Window.WIDTH, 0.278 * Window.HEIGHT,
                (int) (0.278 * Window.HEIGHT), Color.WHITE, null);
        foulText = factory.createText("FOUL!", 0.286 * Window.WIDTH, 0.278 * Window.HEIGHT,
                (int) (0.278 * Window.HEIGHT), Color.WHITE, null);
        team1ScoreText = factory.createText("" + player1Score, 0.026 * Window.WIDTH, 0.093 * Window.HEIGHT,
                (int) (0.074 * Window.HEIGHT), Color.WHITE, null);
        team2ScoreText = factory.createText("" + player2Score, 0.885 * Window.WIDTH, 0.093 * Window.HEIGHT,
                (int) (0.074 * Window.HEIGHT), Color.WHITE, null);
        gamestate = 1;

        powerUps = factory.createPowerUpManager();

        world = new World();
        world.addPlayer(player1, true);
        world.addPlayer(player2, true);
        world.addPlayer(player3, false);
        world.addPlayer(player4, false);
        world.setBall(ball);
        participants = world.getParticipants();
        restoreNicknames();
    }

    public synchronized int assignSlotForTeam(server.model.TeamSide requestedSide) {
        if (requestedSide == server.model.TeamSide.LEFT) {
            if (!slotTaken[2]) {
                slotTaken[2] = true;
                return 2;
            }
        } else {
            if (!slotTaken[3]) {
                slotTaken[3] = true;
                return 3;
            }
            if (!slotTaken[4]) {
                slotTaken[4] = true;
                return 4;
            }
        }

        // Fallback: any free slot 2–4
        for (int i = 2; i <= 4; i++) {
            if (!slotTaken[i]) {
                slotTaken[i] = true;
                return i;
            }
        }

        // No free slots
        return -1;
    }

    /** Optional: free a slot when client disconnects */
    public synchronized void freeSlot(int playerNumber) {
        if (playerNumber >= 1 && playerNumber <= 4) {
            slotTaken[playerNumber] = false;
        }
    }


    public void draw(Graphics g) {
        background.draw(g);
        floor.draw(g);
        if (goalScored)
            goalScoredText.drawString(g);
        if (foul)
            foulText.drawString(g);

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
        if (Window.playerOneRight && !Window.playerOneLeft) {
            player1.setVelX(7);
        } else if (Window.playerOneLeft && !Window.playerOneRight) {
            player1.setVelX(-7);
        } else if ((!Window.playerOneLeft && !Window.playerOneRight)
                || (Window.playerOneLeft && Window.playerOneRight)) {
            player1.setVelX(0);
        }

        if (Window.playerTwoRight && !Window.playerTwoLeft) {
            player2.setVelX(7);
        } else if (Window.playerTwoLeft && !Window.playerTwoRight) {
            player2.setVelX(-7);
        } else if ((!Window.playerTwoLeft && !Window.playerTwoRight)
                || (Window.playerTwoLeft && Window.playerTwoRight)) {
            player2.setVelX(0);
        }

        if (Window.playerThreeRight && !Window.playerThreeLeft) {
            player3.setVelX(7);
        } else if (Window.playerThreeLeft && !Window.playerThreeRight) {
            player3.setVelX(-7);
        } else if ((!Window.playerThreeLeft && !Window.playerThreeRight)
                || (Window.playerThreeLeft && Window.playerThreeRight)) {
            player3.setVelX(0);
        }

        if (Window.playerFourRight && !Window.playerFourLeft) {
            player4.setVelX(7);
        } else if (Window.playerFourLeft && !Window.playerFourRight) {
            player4.setVelX(-7);
        } else if ((!Window.playerFourLeft && !Window.playerFourRight)
                || (Window.playerFourLeft && Window.playerFourRight)) {
            player4.setVelX(0);
        }

        if (Window.playerOneJump) {
            player1.jump();
            onPlayerJumped(1);   // host or local player slot
        }
        if (Window.playerTwoJump) {
            player2.jump();
            onPlayerJumped(2);   // host or local player slot
        }
        if (Window.playerThreeJump) {
            player3.jump();
            onPlayerJumped(3);   // host or local player slot
        }
        if (Window.playerFourJump) {
            player4.jump();
            onPlayerJumped(4);   // host or local player slot

        }
    }

    public void tick() {
        if (matchController != null && matchController.isPaused()) {
            return; // freeze gameplay during half-time pause
        }
        // Delta time for physics (16ms ~= 0.016 seconds per frame at 60 FPS)
        double deltaTime = 0.016;

        // Get the report visitor for statistics tracking
        GameReportVisitor reportVisitor = ReportCommand.getReportVisitor();

        // Update player states (stamina management) using iterator
        world.updateAll(deltaTime);

        // Recompute speed boosts for hot players (iterator over sorted scorers)
        recomputeHotPlayerBoosts();

        // Update player positions and eyes via merged iterator, applying hot-player boost
        for (Slime player : participants) {
            double boost = slotSpeedBoost[player.getSlot()];
            player.setX(player.getX() + player.getVelX() * boost);
            player.updateEyes();
            // Track player activity with visitor
            player.accept(reportVisitor);
        }

        // 2nd iterator implimentation
        for (Slime player : world.nearestToBall(3)) {
            Maths.bounceBallOffSlime(ball, player);
        }

        controls();

        if (!trainingMode && powerUps != null) {
            powerUps.update(ball, player1, player2, player3, player4);
            // Track power-up usage
            for (PowerUp powerUp : powerUps.getVisiblePowerUps()) {
                powerUp.accept(reportVisitor);
            }
        }

        // Track ball movement
        ball.accept(reportVisitor);

        if (ball.getY() >= leftGoal.getY() &&
                (ball.getX() <= leftGoal.getX() + leftGoal.getWidth() || ball.getX() >= rightGoal.getX())) {
            for (int i = 0; i < 10; i++) {
                if (!runGame)
                    break;
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
        double lgx = leftGoal.getX(), lgy = leftGoal.getY();
        double lgw = leftGoal.getWidth(), lgh = leftGoal.getHeight();
        double rgx = rightGoal.getX(), rgy = rightGoal.getY();
        double rgw = rightGoal.getWidth(), rgh = rightGoal.getHeight();
        double bt = leftGoal.getBarThickness();
        double bx = ball.getX(), by = ball.getY(), br = ball.getRadius();

        // Post/crossbar geometry
        double lPostX = lgx + lgw, lPostY = lgy - 5, lPostW = bt, lPostH = lgh;
        double rPostX = rgx - bt, rPostY = rgy - 5, rPostW = bt, rPostH = rgh;
        double lBarX = lgx, lBarY = lgy - 5, lBarW = lgw, lBarH = bt;
        double rBarX = rgx, rBarY = rgy - 5, rBarW = rgw, rBarH = bt;

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
        for (Slime player : participants) {
            player.downMovement();
            player.floorCheck();
            player.gravity();
        }

        boolean leftFoul = false;
        for (Slime player : world.getLeftTeam()) {
            if (player.foulCheckLeft() || player.foulCheckRight()) {
                leftFoul = true;
                break;
            }
        }
        if (leftFoul) {
            leftErrorBar.shrinkLeft();
        } else {
            leftErrorBar.setWidth(Window.WIDTH / 2);
        }

        boolean rightFoul = false;
        for (Slime player : world.getRightTeam()) {
            if (player.foulCheckLeft() || player.foulCheckRight()) {
                rightFoul = true;
                break;
            }
        }
        if (rightFoul) {
            rightErrorBar.shrinkRight();
        } else {
            rightErrorBar.setWidth(Window.WIDTH / 2);
            rightErrorBar.setX(Window.WIDTH / 2);
        }

        // --- Foul checks ---
        if (rightErrorBar.getWidth() < 1) {
            if (gamestate == 1)
                player1Score++;
            foul = true;
            goalScored = false;
            runGame = false;
            ReportCommand.incrementFouls();
            ReportCommand.updateScores(player1Score, player2Score);
            if (autoResetAtMs == 0)
                autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
        if (leftErrorBar.getWidth() < 1) {
            if (gamestate == 1)
                player2Score++;
            foul = true;
            goalScored = false;
            runGame = false;
            ReportCommand.incrementFouls();
            ReportCommand.updateScores(player1Score, player2Score);
            if (autoResetAtMs == 0)
                autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }

        // --- Goal detection: whole ball must cross plane inside mouth ---
        boolean inLeftMouthY = by - br >= lgy && by + br <= lgy + lgh;
        boolean inRightMouthY = by - br >= rgy && by + br <= rgy + rgh;

        boolean leftGoalScored = inLeftMouthY && (bx + br < lgx + lgw);
        boolean rightGoalScored = inRightMouthY && (bx - br > rgx);

        if (leftGoalScored) {

            int scorerSlot = ball.getLastTouchingSlot();
            if (scorerSlot != 0) {
                onPlayerScored(scorerSlot);
            }
            ball.clearLastTouchingSlot();

            if (gamestate == 1)
                player2Score++;
            goalScored = true;
            foul = false;
            runGame = false;
            ReportCommand.updateScores(player1Score, player2Score);
            if (autoResetAtMs == 0)
                autoResetAtMs = System.currentTimeMillis() + configuration.getAutoResetDelayMs();
        }
        if (rightGoalScored) {

            int scorerSlot = ball.getLastTouchingSlot();
            if (scorerSlot != 0) {
                onPlayerScored(scorerSlot);
            }
            ball.clearLastTouchingSlot();

            if (gamestate == 1)
                player1Score++;
            goalScored = true;
            foul = false;
            runGame = false;
            ReportCommand.updateScores(player1Score, player2Score);
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
        if (powerUps != null)
            powerUps.clearAll(ball);
        // Reset visitor statistics when game resets

        ball.clearLastTouchingSlot();

        ReportCommand.resetStats();
    }

    public void sendData() {
        int effectCode = (powerUps == null) ? 0 : powerUps.getCurrentEffectCode();
        List<PowerUp> visiblePowerUps = (powerUps == null) ? Collections.emptyList() : powerUps.getVisiblePowerUps();

        GameStateJson.PlayerState[] players = new GameStateJson.PlayerState[] {
                new GameStateJson.PlayerState(player1.getX(), player1.getY(), player1.isFacingRight(),
                        player1.getColor().getRGB(), player1.getStamina(), getHotLevelForSlot(1), player1.getNickname()),
                new GameStateJson.PlayerState(player2.getX(), player2.getY(), player2.isFacingRight(),
                        player2.getColor().getRGB(), player2.getStamina(), getHotLevelForSlot(2), player2.getNickname()),
                new GameStateJson.PlayerState(player3.getX(), player3.getY(), player3.isFacingRight(),
                        player3.getColor().getRGB(), player3.getStamina(), getHotLevelForSlot(3), player3.getNickname()),
                new GameStateJson.PlayerState(player4.getX(), player4.getY(), player4.isFacingRight(),
                        player4.getColor().getRGB(), player4.getStamina(), getHotLevelForSlot(4), player4.getNickname())
        };

        List<GameStateJson.PowerUpState> powerStates = new ArrayList<>(visiblePowerUps.size());
        for (PowerUp powerUp : visiblePowerUps) {
            powerStates.add(new GameStateJson.PowerUpState(
                    powerUp.getX(),
                    powerUp.getY(),
                    powerUp.getRadius(),
                    powerUp.getColor().getRGB()));
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
                matchController.getCurrentPhase(), // matchPhase from Template Method controller
                powerStates);

        String payload = GameStateJson.encode(state);
        for (ClientData client : clients) {
            client.getOutputStream().println(payload);
        }
    }

    public static void main(String[] args) {
        boolean training = parseTrainingFlag(args);
        if (!training) {
            training = promptTrainingMode();
        }

        GameConfiguration config = GameConfiguration.builder()
                .withTrainingMatch(training)
                .build();
        new SlimeSoccer(config);
    }

    private static boolean parseTrainingFlag(String[] args) {
        if (args == null) return false;
        for (String a : args) {
            if ("--training".equalsIgnoreCase(a) || "-t".equalsIgnoreCase(a)) {
                return true;
            }
        }
        return false;
    }

    private static boolean promptTrainingMode() {
        String[] options = { "Standard match", "Training match" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "Pasirinkite režimą (Template Method):",
                "Slime Soccer serveris",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return choice == 1; // Training if second option picked
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }
    public ChatMediator getChatMediator() {return chatMediator;}
}
