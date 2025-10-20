package client;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import common.net.GameStateJson;

public class GameInfoReceiverRunnable implements Runnable {
    Socket socket;
    BufferedReader reader;
    PrintStream os;

    public GameInfoReceiverRunnable(Socket newSocket) {
        socket = newSocket;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
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
                GameStateJson.State state;
                try {
                    state = GameStateJson.decode(line);
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    continue;
                }

                GameData.SnapshotBuilder builder = GameData.newSnapshotBuilder();
                GameStateJson.PlayerState[] players = state.players;
                int playerCount = Math.min(players.length, 4);
                for (int i = 0; i < playerCount; i++) {
                    GameStateJson.PlayerState p = players[i];
                    builder.withPlayerPosition(i, (float) p.x, (float) p.y, p.facingRight);
                    builder.withPlayerColor(i, new Color(p.color, true));
                }

                builder.withBall((float) state.ballX, (float) state.ballY);
                builder.withGoalFlags(state.goalScored, state.foul);
                builder.withFoulBars((float) state.leftBarWidth, (float) state.rightBarWidth, (float) state.rightBarX);
                builder.withScores(state.leftScore, state.rightScore);
                builder.withBallEffectCode(state.effectCode);

                for (GameStateJson.PowerUpState p : state.powerUps) {
                    builder.addPowerUp((float) p.x, (float) p.y, (float) p.radius, p.color);
                }

                gameData.applySnapshot(builder.build());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
