package server;

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

    public InputReceiverRunnable( SlimeSoccer newSlimeSoccer, int newPlayerNumber, Socket newSocket ){
        slimeSoccer = newSlimeSoccer;
        playerNumber = newPlayerNumber;
        socket = newSocket;
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
                e.printStackTrace();
            }
        }
    }
}
