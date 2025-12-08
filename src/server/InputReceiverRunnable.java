package server;

import common.chat.ChatMessage;
import common.chat.ChatScope;
import common.net.InputJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class InputReceiverRunnable implements Runnable {
    SlimeSoccer slimeSoccer;
    Socket socket;
    BufferedReader reader;
    int playerNumber;
    ClientData clientData;

    public InputReceiverRunnable(SlimeSoccer newSlimeSoccer,
                                 int newPlayerNumber,
                                 Socket newSocket, ClientData clientData) {
        slimeSoccer = newSlimeSoccer;
        playerNumber = newPlayerNumber;
        socket = newSocket;
        this.clientData = clientData;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                String line = reader.readLine();
                if (line == null) {
                    slimeSoccer.freeSlot(playerNumber);
                    slimeSoccer.getChatMediator().removeParticipant(clientData);
                    break;
                }
                if (line.startsWith("CHAT:")) {
                    handleChat(line.substring(5)); // remove "CHAT:"
                    continue;
                }
                try {
                    InputJson.InputState state = InputJson.decode(line);
                    switch(playerNumber){
                        case 2:
                            slimeSoccer.window.playerTwoJump = state.jump;
                            slimeSoccer.window.playerTwoLeft = state.left;
                            slimeSoccer.window.playerTwoRight = state.right;
                            break;
                        case 3:
                            slimeSoccer.window.playerThreeJump = state.jump;
                            slimeSoccer.window.playerThreeLeft = state.left;
                            slimeSoccer.window.playerThreeRight = state.right;
                            break;
                        case 4:
                            slimeSoccer.window.playerFourJump = state.jump;
                            slimeSoccer.window.playerFourLeft = state.left;
                            slimeSoccer.window.playerFourRight = state.right;
                            break;
                        default:
                            break;
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                slimeSoccer.freeSlot(playerNumber);
                e.printStackTrace();
                break;
            }
        }
    }


    private void handleChat(String payload) {
        // Expect something like: "TEAM|hello there" or "GLOBAL|hello"
        String trimmed = payload.trim();
        int sep = trimmed.indexOf('|');
        if (sep <= 0) return;

        String scopeStr = trimmed.substring(0, sep).toUpperCase();
        String text = trimmed.substring(sep + 1).trim();

        ChatScope scope = ChatScope.valueOf(scopeStr);

        ChatMessage msg = new ChatMessage(
                clientData.getNickname(),   // sender nickname
                clientData.getTeam(),       // sender team ("LEFT"/"RIGHT")
                scope,
                text,
                System.currentTimeMillis()
        );
        // SENDER → MEDIATOR (with processor & recipients behind it)
        slimeSoccer.getChatMediator().sendMessage(msg);
    }
}
