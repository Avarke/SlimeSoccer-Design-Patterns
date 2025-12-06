package server;

import server.model.TeamSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;




public class ConnectionReceiverRunnable implements Runnable {
    private ServerSocket echoSocket;
    private final SlimeSoccer slimeSoccer;
    private final int port;

    public ConnectionReceiverRunnable(SlimeSoccer slimeSoccer, int port){
        this.slimeSoccer = slimeSoccer;
        this.port = port;
    }

    private static class JoinRequest {
        final TeamSide side;
        final String nickname;
        JoinRequest(TeamSide side, String nickname) {
            this.side = side;
            this.nickname = nickname;
        }
    }

    public void run() {
        try {
            echoSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true) {
            try {
                Socket socket = echoSocket.accept();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                String joinLine = reader.readLine();
                if (joinLine == null) {
                    socket.close();
                    continue;
                }

                JoinRequest join = parseJoinLine(joinLine);

                int playerNumber = slimeSoccer.assignSlotForTeam(join.side);
                if (playerNumber == -1) {
                    // No free slots -> politely close
                    PrintStream tmpOut = new PrintStream(socket.getOutputStream());
                    tmpOut.println("SERVER_FULL");
                    tmpOut.flush();
                    socket.close();
                    continue;
                }

                slimeSoccer.setNicknameForSlot(playerNumber, join.nickname);

                ClientData clientData = new ClientData(socket);
                clientData.setSlot(playerNumber);
                clientData.setTeam(join.side.name());     // "LEFT" or "RIGHT"
                clientData.setNickname(join.nickname);

                slimeSoccer.getChatMediator().registerParticipant(clientData);
                slimeSoccer.clients.add(clientData);
                new Thread(new InputReceiverRunnable(slimeSoccer, playerNumber, socket, clientData)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private JoinRequest parseJoinLine(String line) {
        // Expect: JOIN LEFT Nick Name
        String trimmed = line.trim();
        String[] parts = trimmed.split("\\s+", 3); // at most 3 tokens

        TeamSide side = TeamSide.LEFT; // default
        String nickname = "Player";

        if (parts.length >= 2) {
            String sideStr = parts[1].toUpperCase();
            if (sideStr.equals("RIGHT")) {
                side = TeamSide.RIGHT;
            }
        }
        if (parts.length == 3) {
            nickname = parts[2].trim();
            if (nickname.isEmpty()) nickname = "Player";
        }

        return new JoinRequest(side, nickname);
    }
}
