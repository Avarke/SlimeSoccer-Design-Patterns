package client;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import common.io.DataInputStreamAdapter;
import common.io.LineReader;

public class GameInfoReceiverRunnable implements Runnable
{
	Socket socket;
	LineReader reader;
	PrintStream os;
	
	public GameInfoReceiverRunnable(Socket newSocket)
	{
		socket = newSocket;
	}
	
	public void run()
	{
		try
		{
			DataInputStream inputStream = new DataInputStream(socket.getInputStream());
			reader = new DataInputStreamAdapter(inputStream);
			os = new PrintStream(socket.getOutputStream());
		} catch (IOException e)
		{
			e.printStackTrace();
            return;
		}

        GameData gameData = GameData.getInstance();
        while(true)
        {
            try
            {
                String line = reader.readLine();
                if (line == null) {
                    continue;
                }

                Scanner s = new Scanner(line);
                GameData.SnapshotBuilder builder = GameData.newSnapshotBuilder();

                builder.withPlayerPosition(0, Float.parseFloat(s.next()), Float.parseFloat(s.next()), Boolean.parseBoolean(s.next()));
                builder.withPlayerPosition(1, Float.parseFloat(s.next()), Float.parseFloat(s.next()), Boolean.parseBoolean(s.next()));
                builder.withPlayerPosition(2, Float.parseFloat(s.next()), Float.parseFloat(s.next()), Boolean.parseBoolean(s.next()));
                builder.withPlayerPosition(3, Float.parseFloat(s.next()), Float.parseFloat(s.next()), Boolean.parseBoolean(s.next()));

                builder.withBall(Float.parseFloat(s.next()), Float.parseFloat(s.next()));

                builder.withPlayerColor(0, new Color(Integer.parseInt(s.next())));
                builder.withPlayerColor(1, new Color(Integer.parseInt(s.next())));
                builder.withPlayerColor(2, new Color(Integer.parseInt(s.next())));
                builder.withPlayerColor(3, new Color(Integer.parseInt(s.next())));

                builder.withGoalFlags(Boolean.parseBoolean(s.next()), Boolean.parseBoolean(s.next()));
                builder.withFoulBars(Float.parseFloat(s.next()), Float.parseFloat(s.next()), Float.parseFloat(s.next()));
                builder.withScores(Integer.parseInt(s.next()), Integer.parseInt(s.next()));

                if (s.hasNext()) {
                    builder.withBallEffectCode(Integer.parseInt(s.next()));
                }

                s.close();
                gameData.applySnapshot(builder.build());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
