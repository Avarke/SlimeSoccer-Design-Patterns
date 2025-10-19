package client;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import common.io.DataInputStreamAdapter;
import common.io.LineReader;

public class GameInfoReceiverRunnable implements Runnable {
    Socket socket;
    LineReader reader;
    PrintStream os;

    public GameInfoReceiverRunnable(Socket newSocket) {
        socket = newSocket;
    }

    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            reader = new DataInputStreamAdapter(inputStream);
            os = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        GameData gameData = GameData.getInstance();
        while (true) {
            try {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    continue;
                }

                GameData.SnapshotBuilder builder = GameData.newSnapshotBuilder();
                Scanner s = new Scanner(line);
                try {
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

                    int effectCode = s.hasNext() ? Integer.parseInt(s.next()) : 0;
                    builder.withBallEffectCode(effectCode);

                    int powerUpCount = s.hasNext() ? Integer.parseInt(s.next()) : 0;
                    for (int i = 0; i < powerUpCount; i++) {
                        if (!s.hasNext()) break;
                        float px = Float.parseFloat(s.next());
                        if (!s.hasNext()) break;
                        float py = Float.parseFloat(s.next());
                        if (!s.hasNext()) break;
                        float radius = Float.parseFloat(s.next());
                        if (!s.hasNext()) break;
                        int color = Integer.parseInt(s.next());
                        builder.addPowerUp(px, py, radius, color);
                    }

                    gameData.applySnapshot(builder.build());
                } finally {
                    s.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
