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
		
		try 
		{
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
			try 
			{
				Thread.sleep(16);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			window.repaint();
            os.println(
                    gameData.isUpPressed() + " " +
                            gameData.isLeftPressed() + " " +
                            gameData.isRightPressed()
            );		}
	}
	
	public void draw(Graphics g)
	{

        GameData gameData = GameData.getInstance();

        g.setColor(Color.BLUE);
		g.fillRect(0, 0, 1920, 1080);
		
		g.setColor(Color.GRAY);
		g.fillRect(0, 900, 1920, 280);

        drawSlime(g, 1, 75);
        drawSlime(g, 2, 75);
        drawSlime(g, 3, 75);
        drawSlime(g, 4, 75);


		g.setColor(Color.YELLOW);
        g.fillOval((int) (gameData.getBallPosX() - 20), (int) (gameData.getBallPosY() - 20), 40, 40);

        int radius = (int) -(gameData.getBallPosY()) / 50;
		g.setColor(Color.GRAY);
        g.fillOval((int) gameData.getBallPosX() - radius, 50, radius * 2, radius * 2);

        drawGoal(g, 0, 720, true);
        drawGoal(g, 1828, 720, false);

        g.setColor(gameData.getP1Color());
        g.fillRect(0, 930, (int) gameData.getP1FoulBarWidth(), 10);

        g.setColor(gameData.getP3Color());
        g.fillRect((int) gameData.getP2FoulBarX(), 930, (int) gameData.getP2FoulBarWidth(), 10);

        g.setFont(scoreFont);
        g.setColor(Color.WHITE);
        g.drawString("" + gameData.getPlayer1Score(), 50, 100);
        g.drawString("" + gameData.getPlayer2Score(), 1700, 100);

        if (gameData.isGoalScored()) {
            g.setFont(goalFont);
            g.drawString("GOAL!", 550, 300);
        }
        if (gameData.isFoul()) {
            g.setFont(goalFont);
            g.drawString("FOUL!", 550, 300);
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
		new SlimeSoccer();
	}
}
