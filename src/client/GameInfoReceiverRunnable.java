package client;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class GameInfoReceiverRunnable implements Runnable
{
	Socket socket;
	DataInputStream is;
	PrintStream os;
	
	public GameInfoReceiverRunnable(Socket newSocket)
	{
		socket = newSocket;
	}
	
	public void run()
	{
		try
		{
			is = new DataInputStream(socket.getInputStream());
			os = new PrintStream(socket.getOutputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
            return;
		}

        GameData gameData = GameData.getInstance();
        while(true)
        {
            try {
                String line = is.readLine();
                if (line == null) break;

                Scanner s = new Scanner(line);

                gameData.setP1PosX(Float.parseFloat(s.next()));
                gameData.setP1PosY(Float.parseFloat(s.next()));
                gameData.setP1FacingRight(Boolean.parseBoolean(s.next()));

                gameData.setP2PosX(Float.parseFloat(s.next()));
                gameData.setP2PosY(Float.parseFloat(s.next()));
                gameData.setP2FacingRight(Boolean.parseBoolean(s.next()));

                gameData.setP3PosX(Float.parseFloat(s.next()));
                gameData.setP3PosY(Float.parseFloat(s.next()));
                gameData.setP3FacingRight(Boolean.parseBoolean(s.next()));

                gameData.setP4PosX(Float.parseFloat(s.next()));
                gameData.setP4PosY(Float.parseFloat(s.next()));
                gameData.setP4FacingRight(Boolean.parseBoolean(s.next()));

                gameData.setBallPosX(Float.parseFloat(s.next()));
                gameData.setBallPosY(Float.parseFloat(s.next()));

                gameData.setP1Color(new Color(Integer.parseInt(s.next())));
                gameData.setP2Color(new Color(Integer.parseInt(s.next())));
                gameData.setP3Color(new Color(Integer.parseInt(s.next())));
                gameData.setP4Color(new Color(Integer.parseInt(s.next())));

                gameData.setGoalScored(Boolean.parseBoolean(s.next()));
                gameData.setFoul(Boolean.parseBoolean(s.next()));

                gameData.setP1FoulBarWidth(Float.parseFloat(s.next()));
                gameData.setP2FoulBarWidth(Float.parseFloat(s.next()));
                gameData.setP2FoulBarX(Float.parseFloat(s.next()));

                gameData.setPlayer1Score(Integer.parseInt(s.next()));
                gameData.setPlayer2Score(Integer.parseInt(s.next()));

                boolean kick = false;
                if (s.hasNextInt()) {
                    kick = s.nextInt() == 1;
                }
                gameData.setBallKickedThisFrame(kick);

                int puc = 0;
                if (s.hasNextInt()) {
                    puc = s.nextInt();
                }
                float[] xs = new float[Math.min(puc, 4)];
                float[] ys = new float[Math.min(puc, 4)];
                int[] types = new int[Math.min(puc, 4)];
                int[] radii = new int[Math.min(puc, 4)];
                for (int i = 0; i < xs.length; i++) {
                    xs[i] = Float.parseFloat(s.next());
                    ys[i] = Float.parseFloat(s.next());
                    types[i] = Integer.parseInt(s.next());
                    radii[i] = Integer.parseInt(s.next());
                }
                gameData.setPowerUps(puc, xs, ys, types, radii);

                s.close();

                gameData.notifyObservers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
