package client;

import common.GameConfiguration;
import common.facade.SlimeSoccerFacade;

import client.render.BasicBallDrawable;
import client.render.Drawable;
import client.render.EffectColorBallDecorator;
import client.render.SafeZoneBallDecorator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;


public class SlimeSoccer 
{
    private static final double BASE_WIDTH = 1920.0;
    private static final double BASE_HEIGHT = 1080.0;

	Socket socket;
	DataInputStream is;
	PrintStream os;
	GameConfiguration configuration;
	ClientWindow window;
	private final Drawable ballDrawable;
	Font scoreFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 80);
	Font goalFont = new Font("Franklin Gothic Medium Italic", Font.PLAIN, 300);


    public SlimeSoccer() {
        this(promptConfiguration());
    }

    public SlimeSoccer(GameConfiguration configuration) {
        this.configuration = configuration != null ? configuration : GameConfiguration.builder().build();
        window = new ClientWindow(this);
        try {
            socket = new Socket(this.configuration.getHost(), this.configuration.getPort());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        ballDrawable = new SafeZoneBallDecorator(new EffectColorBallDecorator(new BasicBallDrawable(20)));
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

    private static GameConfiguration promptConfiguration() {
        String hostNameInput = JOptionPane.showInputDialog("Enter hostname");
        return GameConfiguration.builder()
                .withHost(hostNameInput)
                .build();
    }

    public void draw(Graphics g)
    {
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

    private void renderScene(Graphics2D g, GameData gameData) {
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, (int) BASE_WIDTH, (int) BASE_HEIGHT);

        g.setColor(Color.GRAY);
        g.fillRect(0, 900, (int) BASE_WIDTH, (int) (BASE_HEIGHT - 900));

        drawSlime(g, 1, 75);
        drawSlime(g, 2, 75);
        drawSlime(g, 3, 75);
        drawSlime(g, 4, 75);

        drawPowerUps(g, gameData);
        ballDrawable.draw(g, gameData);

        drawGoal(g, 0, 720, true);
        drawGoal(g, 1828, 720, false);

        g.setColor(gameData.getP1Color());
        g.fillRect(0, 930, (int) gameData.getP1FoulBarWidth(), 10);

        g.setColor(gameData.getP3Color());
        g.fillRect((int) gameData.getP2FoulBarX(), 930, (int) gameData.getP2FoulBarWidth(), 10);

        g.setFont(scoreFont);
        g.setColor(Color.WHITE);
        g.drawString(Integer.toString(gameData.getPlayer1Score()), 50, 100);
        g.drawString(Integer.toString(gameData.getPlayer2Score()), 1700, 100);

        if (gameData.isGoalScored()) {
            g.setFont(goalFont);
            g.drawString("GOAL!", 550, 300);
        }
        if (gameData.isFoul()) {
            g.setFont(goalFont);
            g.drawString("FOUL!", 550, 300);
        }

        drawAbilityHud(g, gameData);
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

        // --- draw the slime ---
        g.setColor(color);
        g.fillArc((int)(posX - radius), (int)(posY - radius), radius*2, radius*2, 0, 180);

        float eyePosY = posY - 35;
        float eyePosX = facingRight ? posX + 35 : posX - 35;

        float ballDist = (float)Math.sqrt(Math.pow(ballPosX - eyePosX, 2) + Math.pow(ballPosY - eyePosY, 2));

        g.setColor(Color.WHITE);
        g.fillOval((int)(eyePosX - 15), (int)(eyePosY - 15), 30, 30);

        g.setColor(Color.BLACK);
        g.fillOval((int)(eyePosX + 6 * (ballPosX - eyePosX) / ballDist - 7),
                (int)(eyePosY + 6 * (ballPosY - eyePosY) / ballDist - 7), 14, 14);
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

    private void drawAbilityHud(Graphics2D g, GameData data) {
        // Abilities HUD not used in the base build; left intentionally blank.
    }

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
	
	public static void main(String[] args)
	{
		SlimeSoccerFacade.launchClient();
	}

    public GameConfiguration getConfiguration() {
        return configuration;
    }
}
