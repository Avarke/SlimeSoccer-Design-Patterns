package client;

import client.render.*;
import client.ui.HudComposite;
import client.ui.components.*;
import common.GameConfiguration;

import common.net.InputJson;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class SlimeSoccer {
    private static final double BASE_WIDTH = 1920.0;
    private static final double BASE_HEIGHT = 1080.0;

    private static final double FLOOR_Y = 0.814 * BASE_HEIGHT;
    private static final double FOUL_Y = 0.861 * BASE_HEIGHT;
    private static final double GOAL_Y = 0.667 * BASE_HEIGHT;
    private static final double RIGHT_GOAL_X = 0.952 * BASE_WIDTH;

    Socket socket;
    DataInputStream is;
    PrintStream os;
    GameConfiguration configuration;
    ClientWindow window;
    private final BasicBallDrawable baseBallDrawable;
    private final EffectColorBallDecorator colorBallDecorator;
    private final TrailGlowBallDecorator trailBallDecorator;
    private final SafeZoneBallDecorator safeZoneBallDecorator;
    private final Drawable ballDrawable;
    private final SlimeSprite slimeSprite;
    private final HudComposite hudRoot;

    Font scoreFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 80);
    Font goalFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 300);
    Font nameFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 20);

    public SlimeSoccer() {
        this(promptConfiguration());
    }

    public SlimeSoccer(GameConfiguration configuration) {
        this.configuration = configuration != null ? configuration : GameConfiguration.builder().build();

        baseBallDrawable = new BasicBallDrawable(20);
        colorBallDecorator = new EffectColorBallDecorator(baseBallDrawable);
        trailBallDecorator = new TrailGlowBallDecorator(colorBallDecorator);
        safeZoneBallDecorator = new SafeZoneBallDecorator(trailBallDecorator, (int) (0.814 * BASE_HEIGHT));
        ballDrawable = safeZoneBallDecorator;


        this.slimeSprite = SlimeSpriteFactory.getSprite(75);

        // HUD COMPOSITE

        hudRoot = new HudComposite();
        hudRoot.addChild(new ScoreBoardComponent(scoreFont));
        hudRoot.addChild(new FoulBarsComponent());
        hudRoot.addChild(new StaminaBarsComponent());
        hudRoot.addChild(new MatchPhaseComponent(BASE_WIDTH, BASE_HEIGHT,
                new Font("Franklin Gothic Medium Italic", Font.BOLD, 120)));
        hudRoot.addChild(new ChatOverlayComponent(this));
        hudRoot.addChild(new GoalMessageComponent(goalFont));

        window = new ClientWindow(this);
        try {
            socket = new Socket(this.configuration.getHost(), this.configuration.getPort());
            os = new PrintStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        String teamChoice = promptTeamChoice(); // "LEFT" or "RIGHT"
        String nickname   = promptNickname();      // text
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = "Player";
        }



        os.println("JOIN " + teamChoice + " " + nickname.trim());
        os.flush();


        new Thread(new GameInfoReceiverRunnable(socket)).start();
        try {
            os = new PrintStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        GameData gameData = GameData.getInstance();

        while (true) {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Observer will trigger repaint on incoming data; we only send inputs here
            String payload = InputJson.encode(
                    gameData.isUpPressed(),
                    gameData.isLeftPressed(),
                    gameData.isRightPressed());
            os.println(payload);

            GameData.OutgoingChat oc = gameData.consumeOutgoingChat();
            if (oc != null) {
                String line = "CHAT:" + oc.scope + "|" + oc.text;
                os.println(line);
            }
        }
    }

    private String promptTeamChoice() {
        String[] options = {"LEFT", "RIGHT"};
        String choice = (String) JOptionPane.showInputDialog(
                null,
                "Choose your team:",
                "Team Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == null) {
            // user cancelled: default to LEFT
            return "LEFT";
        }
        return choice.toUpperCase();
    }

    private String promptNickname() {
        return JOptionPane.showInputDialog(
                null,
                "Enter your nickname:",
                "Nickname",
                JOptionPane.PLAIN_MESSAGE);
    }



    private static GameConfiguration promptConfiguration() {
        String hostNameInput = JOptionPane.showInputDialog("Enter hostname");
        return GameConfiguration.builder()
                .withHost(hostNameInput)
                .build();
    }

    private long frameCount = 0;
    private long totalRenderNs = 0;

    public void draw(Graphics g) {


        GameData gameData = GameData.getInstance();

        Graphics2D g2 = (Graphics2D) g;
        Rectangle clip = g2.getClipBounds();
        int panelWidth = (clip != null) ? clip.width : window.getWidth();
        int panelHeight = (clip != null) ? clip.height : window.getHeight();

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, panelWidth, panelHeight);

        Graphics2D scene = (Graphics2D) g2.create();
        double scale = Math.min(panelWidth / BASE_WIDTH, panelHeight / BASE_HEIGHT);
        if (scale <= 0) {
            scale = 1.0;
        }
        double offsetX = (panelWidth - BASE_WIDTH * scale) / 2.0;
        double offsetY = (panelHeight - BASE_HEIGHT * scale) / 2.0;
        scene.translate(offsetX, offsetY);
        scene.scale(scale, scale);

        renderScene(scene, gameData);

        scene.dispose();
    }


    private long slimeFramesInWindow = 0;
    private long slimeTotalNsInWindow = 0;

    private void renderScene(Graphics2D g, GameData gameData) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, (int) BASE_WIDTH, (int) BASE_HEIGHT);

        g.setColor(Color.GRAY);
        g.fillRect(0, (int) FLOOR_Y, (int) BASE_WIDTH, (int) (BASE_HEIGHT - FLOOR_Y));

        long slimeStart = System.nanoTime();


        drawSlime(g, 1, 75);
        drawSlime(g, 2, 75);
        drawSlime(g, 3, 75);
        drawSlime(g, 4, 75);

        long slimeEnd = System.nanoTime();
        slimeTotalNsInWindow += (slimeEnd - slimeStart);
        slimeFramesInWindow++;

        if (++slimeFramesInWindow == 300) {
            double avgMs = (slimeTotalNsInWindow / 300.0) / 1_000_000.0;
            System.out.println("Avg SLIME render time (last 300 frames): " + avgMs + " ms");

            slimeFramesInWindow = 0;
            slimeTotalNsInWindow = 0;
        }

        trailBallDecorator.applyEffect(gameData.getBallEffectCode());
        drawPowerUps(g, gameData);
        ballDrawable.draw(g, gameData);

        drawGoal(g, 0, (int) GOAL_Y, true);
        drawGoal(g, (int) RIGHT_GOAL_X, (int) GOAL_Y, false);

        // composite usage
        hudRoot.update(gameData);
        hudRoot.draw(g, gameData);

//        g.setColor(gameData.getP1Color());
//        g.fillRect(0, (int) FOUL_Y, (int) gameData.getP1FoulBarWidth(), 10);
//
//        g.setColor(gameData.getP3Color());
//        g.fillRect((int) gameData.getP2FoulBarX(), (int) FOUL_Y, (int) gameData.getP2FoulBarWidth(), 10);
//
//        g.setFont(scoreFont);
//        g.setColor(Color.WHITE);
//        g.drawString(Integer.toString(gameData.getPlayer1Score()), 50, 100);
//        g.drawString(Integer.toString(gameData.getPlayer2Score()), 1700, 100);
//
//        if (gameData.isGoalScored()) {
//            g.setFont(goalFont);
//            g.drawString("GOAL!", 550, 300);
//        }
//        if (gameData.isFoul()) {
//            g.setFont(goalFont);
//            g.drawString("FOUL!", 550, 300);
//        }
//
//        drawAbilityHud(g, gameData);
//        g.setFont(nameFont);
//
//        // Draw stamina bars for each player
//        drawStaminaBar(g, 1, gameData);
//        drawStaminaBar(g, 2, gameData);
//        drawStaminaBar(g, 3, gameData);
//        drawStaminaBar(g, 4, gameData);
//
//        // Draw match phase overlay if present
//        drawMatchPhaseOverlay(g, gameData);
//
//        drawChatOverlay(g,gameData);
    }

    public void drawChatOverlay(Graphics2D g, GameData gameData) {

        int boxX = 20;
        int boxY = 600;   // near bottom-left
        int boxWidth = 600;
        int lineHeight = 20;
        int maxLines = 8;

        g.setColor(new Color(0, 0, 0, 150)); // semi-transparent black
        g.fillRect(boxX - 10, boxY - 10, boxWidth, lineHeight * (maxLines + 2));

        java.util.List<GameData.ChatEntry> log = gameData.getChatLogSnapshot();
        int scrollOffset = gameData.getChatScrollOffset();
        
        g.setFont(new Font("Arial", Font.PLAIN, 16));

        // Expand all messages into individual lines first
        java.util.List<String> allLines = new java.util.ArrayList<>();
        for (GameData.ChatEntry e : log) {
            String prefix = "[" + e.scope + "] ";
            String[] textLines = e.text.split("\\n");
            
            for (int j = 0; j < textLines.length; j++) {
                if (j == 0) {
                    // First line with sender prefix
                    allLines.add(prefix + e.sender + ": " + textLines[0]);
                } else {
                    // Subsequent lines indented
                    allLines.add("  " + textLines[j]);
                }
            }
        }
        
        // Calculate which lines to show based on scroll offset
        int totalLines = allLines.size();
        int endIndex = totalLines - scrollOffset; // Where to end (most recent visible line + 1)
        int startIndex = Math.max(0, endIndex - maxLines); // Where to start
        
        // Draw the lines in view
        int y = boxY;
        for (int i = startIndex; i < endIndex && i < totalLines; i++) {
            g.setColor(Color.WHITE);
            g.drawString(allLines.get(i), boxX, y);
            y += lineHeight;
        }
        
        // Show scroll indicator if not at bottom
        if (scrollOffset > 0) {
            g.setColor(Color.YELLOW);
            g.drawString("▲ Scroll: " + scrollOffset + " lines up ▲", boxX + boxWidth - 200, boxY - 15);
        }

        // Input line if chat is active
        if (gameData.isChatInputActive()) {
            String scopeLabel = gameData.getCurrentChatScopeLabel(); // TEAM / GLOBAL
            String text = gameData.getCurrentChatInput();

            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(boxX - 10, y, boxWidth, lineHeight + 10);

            g.setColor(Color.YELLOW);
            String inputLine = "[" + scopeLabel + "] " + text + "_";
            g.drawString(inputLine, boxX, y + lineHeight);
        }
    }

    public void drawSlime(Graphics g, int playerIndex, int radius) {
        GameData gameData = GameData.getInstance();

        float posX = 0, posY = 0, ballPosX, ballPosY;
        boolean facingRight = false;
        Color color = Color.WHITE;

        // Pick the right player's data
        switch (playerIndex) {
            case 1:
                posX = gameData.getP1PosX();
                posY = gameData.getP1PosY();
                facingRight = gameData.isP1FacingRight();
                color = gameData.getP1Color();
                break;
            case 2:
                posX = gameData.getP2PosX();
                posY = gameData.getP2PosY();
                facingRight = gameData.isP2FacingRight();
                color = gameData.getP2Color();
                break;
            case 3:
                posX = gameData.getP3PosX();
                posY = gameData.getP3PosY();
                facingRight = gameData.isP3FacingRight();
                color = gameData.getP3Color();
                break;
            case 4:
                posX = gameData.getP4PosX();
                posY = gameData.getP4PosY();
                facingRight = gameData.isP4FacingRight();
                color = gameData.getP4Color();
                break;
        }

        ballPosX = gameData.getBallPosX();
        ballPosY = gameData.getBallPosY();

        int hotLevel = gameData.getHotLevel(playerIndex - 1);
        Color drawColor = applyHotTint(color, hotLevel);

        // --- draw the slime with hot aura/outline if needed ---
        if (g instanceof Graphics2D) {
            drawHotAura((Graphics2D) g, posX, posY, hotLevel, radius);
        }

        slimeSprite.draw(
                (Graphics2D) g,
                posX,
                posY,
                facingRight,
                drawColor,
                ballPosX,
                ballPosY
        );

        if (g instanceof Graphics2D) {
            drawHotOutline((Graphics2D) g, posX, posY, hotLevel, radius);
        }
//        g.setColor(color);
//        g.fillArc((int) (posX - radius), (int) (posY - radius), radius * 2, radius * 2, 0, 180);
//
//        float eyePosY = posY - 35;
//        float eyePosX = facingRight ? posX + 35 : posX - 35;
//
//        float ballDist = (float) Math.sqrt(Math.pow(ballPosX - eyePosX, 2) + Math.pow(ballPosY - eyePosY, 2));
//
//        g.setColor(Color.WHITE);
//        g.fillOval((int) (eyePosX - 15), (int) (eyePosY - 15), 30, 30);
//
//        g.setColor(Color.BLACK);
//        g.fillOval((int) (eyePosX + 6 * (ballPosX - eyePosX) / ballDist - 7),
//                (int) (eyePosY + 6 * (ballPosY - eyePosY) / ballDist - 7), 14, 14);
    }

    private void drawPowerUps(Graphics2D g, GameData data) {
        GameData.PowerUpSnapshot snapshot = data.getPowerUpSnapshot();
        for (int i = 0; i < snapshot.count; i++) {
            Color color = new Color(snapshot.colors[i], true);
            int radius = (int) snapshot.radii[i];
            int diameter = radius * 2;
            g.setColor(color);
            g.fillOval((int) (snapshot.xs[i] - radius), (int) (snapshot.ys[i] - radius), diameter, diameter);
        }
    }

    private Color applyHotTint(Color base, int hotLevel) {
        if (hotLevel <= 0) return base;
        // Subtle warm tint per level
        float mix = Math.min(0.35f, 0.12f * hotLevel);
        int r = (int) (base.getRed() * (1 - mix) + 255 * mix);
        int g = (int) (base.getGreen() * (1 - mix) + 180 * mix);
        int b = (int) (base.getBlue() * (1 - mix) + 80 * mix);
        return new Color(clamp(r), clamp(g), clamp(b), base.getAlpha());
    }

    private int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private void drawHotAura(Graphics2D g, float x, float y, int hotLevel, int radius) {
        if (hotLevel <= 0) return;
        float alpha = 0.06f * hotLevel; // softer
        int auraRadius = (int) (radius * (1.2 + 0.2 * hotLevel)); // smaller halo
        Color auraColor = new Color(255, 150, 50, (int) (alpha * 255));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(auraColor);
        g.fillOval((int) (x - auraRadius), (int) (y - auraRadius), auraRadius * 2, auraRadius * 2);
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void drawHotOutline(Graphics2D g, float x, float y, int hotLevel, int radius) {
        if (hotLevel <= 0) return;
        int thickness = 1 + hotLevel; // thinner
        int outlineR = (int) (radius * 1.02); // tighter to body
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(thickness));
        g.setColor(new Color(255, 200, 120, 170));
        g.drawArc((int) (x - outlineR), (int) (y - outlineR), outlineR * 2, outlineR * 2, 0, 180);
        g.setStroke(old);
    }

    private void drawAbilityHud(Graphics2D g, GameData data) {
        // Abilities HUD not used in the base build; left intentionally blank.
    }

    /**
     * Draws a stamina bar above each player's slime.
     */
    private void drawStaminaBar(Graphics2D g, int playerIndex, GameData gameData) {
        float posX = 0, posY = 0, stamina = 100f;

        // Get player position and stamina
        switch (playerIndex) {
            case 1:
                posX = gameData.getP1PosX();
                posY = gameData.getP1PosY();
                stamina = gameData.getP1Stamina();
                break;
            case 2:
                posX = gameData.getP2PosX();
                posY = gameData.getP2PosY();
                stamina = gameData.getP2Stamina();
                break;
            case 3:
                posX = gameData.getP3PosX();
                posY = gameData.getP3PosY();
                stamina = gameData.getP3Stamina();
                break;
            case 4:
                posX = gameData.getP4PosX();
                posY = gameData.getP4PosY();
                stamina = gameData.getP4Stamina();
                break;
        }

        // Stamina bar dimensions
        int barWidth = 100;
        int barHeight = 10;
        int barX = (int) (posX - barWidth / 2);
        int barY = (int) (posY - 120); // Above the slime

        // Background (empty bar)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Foreground (filled based on stamina)
        int filledWidth = (int) (barWidth * (stamina / 100f));

        // Color based on stamina level
        Color staminaColor;
        if (stamina > 80) {
            staminaColor = new Color(0, 255, 0); // Green (Fresh)
        } else if (stamina > 50) {
            staminaColor = new Color(255, 255, 0); // Yellow (Tired)
        } else if (stamina > 20) {
            staminaColor = new Color(255, 165, 0); // Orange (Exhausted)
        } else {
            staminaColor = new Color(255, 0, 0); // Red (Recovering)
        }

        g.setColor(staminaColor);
        g.fillRect(barX, barY, filledWidth, barHeight);

        // Border
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        String name = "";
        switch (playerIndex) {
            case 1:
                name = gameData.getP1Name();
                break;
            case 2:
                name = gameData.getP2Name();
                break;
            case 3:
                name = gameData.getP3Name();
                break;
            case 4:
                name = gameData.getP4Name();
                break;
        }
        if (name != null && !name.isEmpty()) {
            int textWidth = g.getFontMetrics().stringWidth(name);
            int textX = barX + (barWidth - textWidth) / 2;
            int textY = barY - 5; // slightly above the bar
            g.drawString(name, textX, textY);
        }
    }

    /**
     * Draws match phase overlay (e.g., "HALF TIME", "FIRST HALF").
     */
    private void drawMatchPhaseOverlay(Graphics2D g, GameData gameData) {
        String matchPhase = gameData.getMatchPhase();

        // Only draw if matchPhase is not empty
        if (matchPhase != null && !matchPhase.trim().isEmpty()) {
            Font phaseFont = new Font("Franklin Gothic Medium Italic", Font.BOLD, 120);
            g.setFont(phaseFont);
            g.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white

            // Center the text
            int textWidth = g.getFontMetrics().stringWidth(matchPhase);
            int textX = (int) ((BASE_WIDTH - textWidth) / 2);
            int textY = (int) (BASE_HEIGHT / 2 - 100);

            // Draw shadow for better visibility
            g.setColor(new Color(0, 0, 0, 150));
            g.drawString(matchPhase, textX + 3, textY + 3);

            // Draw main text
            g.setColor(new Color(255, 255, 255, 220));
            g.drawString(matchPhase, textX, textY);
        }
    }

    public void drawGoal(Graphics g, int posX, int posY, boolean isLeftGoal) {
        g.setColor(Color.WHITE);
        int xIntvl = (int) (0.005 * BASE_WIDTH);
        int yIntvl = (int) (0.009 * BASE_HEIGHT);
        int width = (int) (0.048 * BASE_WIDTH);
        int height = (int) (0.169 * BASE_HEIGHT);
        int netThickness = 2;
        int barThickness = 10;

        for (int i = 0; i < 10; i++) {
            g.fillRect(posX + (i * xIntvl), posY, netThickness, height);
        }
        for (int i = 0; i < 19; i++) {
            g.fillRect(posX, posY + (i * yIntvl), width, netThickness);
        }
        if (isLeftGoal) {
            g.fillRect(posX + width, posY - 5, barThickness, height);
        }
        if (!isLeftGoal) {
            g.fillRect(posX - barThickness, posY - 5, barThickness, height);
        }
    }

    public static void main(String[] args) {
        new SlimeSoccer();
    }

    public GameConfiguration getConfiguration() {
        return configuration;
    }

}
