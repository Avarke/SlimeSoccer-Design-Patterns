package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;


public class SlimeSoccer 
{
	Socket socket;
	DataInputStream is;
	PrintStream os;
	String hostName = "localhost";
	ClientWindow window;
	int port = 6969;
	Font scoreFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 80);
	Font goalFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 300);


    public SlimeSoccer()
    {
        hostName = JOptionPane.showInputDialog("Enter hostname");
        window = new ClientWindow(this);

        try {
            socket = new Socket(hostName, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        new Thread(new GameInfoReceiverRunnable(socket)).start();
        try {
            os = new PrintStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        GameData gameData = GameData.getInstance();

        while(true)
        {
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Observer will trigger repaint on incoming data; we only send inputs here
            os.println(
                    gameData.isUpPressed() + " " +
                            gameData.isLeftPressed() + " " +
                            gameData.isRightPressed()
            );
        }
    }

    public void draw(Graphics g)
    {
        GameData gameData = GameData.getInstance();

        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Panel size we actually have to paint into
        java.awt.Rectangle clip = g2.getClipBounds();
        int PW = (clip != null ? clip.width : 1920);
        int PH = (clip != null ? clip.height : 1080);

        // Fill entire panel first so no white edges ever appear
        g2.setColor(new Color(8, 16, 64));
        g2.fillRect(0, 0, PW, PH);

        // ---- World coordinates (server uses 1920x1080) ----
        final int WW = 1920, WH = 1080;
        final int FLOOR_Y  = 879;   // (int)(0.814 * 1080)
        final int GOAL_Y   = 720;   // (int)(0.667 * 1080)
        final int RIGHT_GOAL_X = 1828; // (int)(0.952 * 1920)
        final int FOUL_BAR_Y  = 930;   // (int)(0.861 * 1080)

        // Uniform “fit” scale and center the world inside the panel
        double sx = PW / (double) WW;
        double sy = PH / (double) WH;
        double s  = Math.min(sx, sy);
        double tx = (PW - WW * s) * 0.5;
        double ty = (PH - WH * s) * 0.5;

        java.awt.Graphics2D gw = (java.awt.Graphics2D) g2.create();
        gw.translate(tx, ty);
        gw.scale(s, s); // from now on, draw in world units (1920x1080)

        // Sky + ground (in world units)
        java.awt.GradientPaint sky = new java.awt.GradientPaint(0, 0, new Color(30, 60, 200),
                0, FLOOR_Y, new Color(10, 10, 120));
        gw.setPaint(sky);
        gw.fillRect(0, 0, WW, FLOOR_Y);

        java.awt.GradientPaint ground = new java.awt.GradientPaint(0, FLOOR_Y, new Color(120,120,120),
                0, WH,       new Color(90,90,90));
        gw.setPaint(ground);
        gw.fillRect(0, FLOOR_Y, WW, WH - FLOOR_Y);

        // Slimes (classic, no stretching)
        drawSlime(gw, 1, 75);
        drawSlime(gw, 2, 75);
        drawSlime(gw, 3, 75);
        drawSlime(gw, 4, 75);

        // Power-ups
        int puc = gameData.getPowerUpCount();
        for (int i = 0; i < puc; i++) {
            float x = gameData.getPowerUpX(i);
            float y = gameData.getPowerUpY(i);
            int type = gameData.getPowerUpType(i);
            int r = gameData.getPowerUpRadius(i);
            Color c = switch (type) {
                case 1 -> new Color(135,206,250);
                case 2 -> new Color(80,80,80);
                case 3 -> new Color(255,105,180);
                default -> Color.WHITE;
            };
            gw.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
            gw.fillOval((int)(x - r), (int)(y - r), r * 2, r * 2);
            gw.setColor(new Color(255,255,255,140));
            gw.drawOval((int)(x - r), (int)(y - r), r * 2, r * 2);
        }

        // Ball trail
        float bx = gameData.getBallPosX();
        float by = gameData.getBallPosY();
        ballTrail.addLast(new float[]{bx, by});
        while (ballTrail.size() > TRAIL_LEN) ballTrail.removeFirst();
        int i = 0, n = ballTrail.size();
        for (float[] p : ballTrail) {
            int alpha = Math.max(20, (int)(255.0 * (i + 1) / n));
            gw.setColor(new Color(255, 230, 80, Math.min(alpha, 180)));
            int rr = 20 - (n - 1 - i); if (rr < 8) rr = 8;
            gw.fillOval((int)(p[0] - rr), (int)(p[1] - rr), rr * 2, rr * 2);
            i++;
        }

        // Ball + halo
        gw.setColor(Color.YELLOW);
        gw.fillOval((int) (bx - 20), (int) (by - 20), 40, 40);
        int haloR = Math.max(0, Math.min(200, (FLOOR_Y - (int)by) / 50));
        gw.setColor(new Color(160, 160, 160, 160));
        if (haloR > 0) gw.fillOval((int) bx - haloR, (int)(0.046 * WH), haloR * 2, haloR * 2);

        // Goals & bars
        drawGoal(gw, 0, GOAL_Y, true);
        drawGoal(gw, RIGHT_GOAL_X, GOAL_Y, false);

        gw.setColor(gameData.getP1Color());
        gw.fillRect(0, FOUL_BAR_Y, (int) gameData.getP1FoulBarWidth(), 10);
        gw.setColor(gameData.getP3Color());
        gw.fillRect((int) gameData.getP2FoulBarX(), FOUL_BAR_Y, (int) gameData.getP2FoulBarWidth(), 10);

        // Scores / banners
        gw.setFont(scoreFont);
        gw.setColor(Color.WHITE);
        gw.drawString("" + gameData.getPlayer1Score(), 50, 100);
        gw.drawString("" + gameData.getPlayer2Score(), 1700, 100);

        if (gameData.isGoalScored()) { gw.setFont(goalFont); gw.drawString("GOAL!", 550, 300); }
        if (gameData.isFoul())       { gw.setFont(goalFont); gw.drawString("FOUL!", 550, 300); }

        gw.dispose();
    }


    public void drawSlime(Graphics g, int playerIndex, int radius) {
        GameData gameData = GameData.getInstance();

        float posX = 0, posY = 0, ballPosX, ballPosY;
        boolean facingRight = false;
        Color color = Color.WHITE;

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

        // --- Classic slime: NO stretching/squashing ---
        // Body (semicircle) — baseline exactly at posY (same as server)
        int left = Math.round(posX - radius);
        int top  = Math.round(posY - radius);
        g.setColor(color);
        g.fillArc(left, top, radius * 2, radius * 2, 0, 180);

        // Eye (same offsets server uses: 0.467 * radius)
        float eyePosY = posY - 0.467f * radius;
        float eyePosX = posX + (facingRight ? 1 : -1) * 0.467f * radius;

        float dx = ballPosX - eyePosX;
        float dy = ballPosY - eyePosY;
        float ballDist = (float)Math.sqrt(dx * dx + dy * dy);
        if (ballDist < 0.001f) ballDist = 0.001f;

        g.setColor(Color.WHITE);
        g.fillOval((int)(eyePosX - 15), (int)(eyePosY - 15), 30, 30);

        g.setColor(Color.BLACK);
        g.fillOval(
                (int)(eyePosX + 6 * dx / ballDist - 7),
                (int)(eyePosY + 6 * dy / ballDist - 7),
                14, 14
        );
    }

    private static float clamp(float v, float lo, float hi) { return (v < lo) ? lo : (v > hi ? hi : v); }

    public void drawGoal(Graphics g, int posX, int posY, boolean isLeftGoal){
		g.setColor(Color.WHITE);
		for(int i = 0; i < 10; i++)
		{
			g.fillRect(posX + (i*10), posY, 2, 182);
		}
		for(int i = 0; i < 19; i++)
		{
			g.fillRect(posX, posY + (i*10), 92, 2);
		}
		if(isLeftGoal)
		{
			g.fillRect(posX + 90, posY - 5, 10, 190);
		}
		if(!isLeftGoal)
		{
			g.fillRect(posX - 10, posY - 5, 10, 190);
		}
	}

    private static class DeformState {
        float sx = 1f, sy = 1f, vx = 0f, vy = 0f;
        float prevX, prevY;
        boolean inited = false;
    }
    private final DeformState[] deform = { new DeformState(), new DeformState(), new DeformState(), new DeformState() };

    private final java.util.ArrayDeque<float[]> ballTrail = new java.util.ArrayDeque<>();
    private static final int TRAIL_LEN = 10;


	public static void main(String[] args)
	{
		new SlimeSoccer();
	}
}
