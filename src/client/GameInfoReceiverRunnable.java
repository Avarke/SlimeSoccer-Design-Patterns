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

                if (line.startsWith("CHAT|")) {
                    handleChatLine(line, gameData);
                    continue; // don't try to decode as JSON
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
                    builder.withPlayerStamina(i, (float) p.stamina);
                    builder.withPlayerName(i, p.nickname);          // NEW LINE
                }

                builder.withBall((float) state.ballX, (float) state.ballY);
                builder.withGoalFlags(state.goalScored, state.foul);
                builder.withFoulBars((float) state.leftBarWidth, (float) state.rightBarWidth, (float) state.rightBarX);
                builder.withScores(state.leftScore, state.rightScore);
                builder.withBallEffectCode(state.effectCode);
                builder.withMatchPhase(state.matchPhase);

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


    private void handleChatLine(String line, GameData gameData) {
        // Format: CHAT|scope|team|sender|text
        String[] parts = line.split("\\|", 5);
        if (parts.length < 5) {
            return; // malformed
        }

        String scope  = parts[1]; // "TEAM"/"GLOBAL"
        String team   = parts[2]; // "LEFT"/"RIGHT"
        String sender = parts[3];
        String text   = parts[4];

        gameData.addChatMessage(scope, team, sender, text);
    }

}
