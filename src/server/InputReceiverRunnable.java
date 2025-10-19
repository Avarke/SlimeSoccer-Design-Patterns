package server;

import common.io.DataInputStreamAdapter;
import common.io.LineReader;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class InputReceiverRunnable implements Runnable {
    SlimeSoccer slimeSoccer;
    Socket socket;
    LineReader reader;
    int playerNumber;

    public InputReceiverRunnable( SlimeSoccer newSlimeSoccer, int newPlayerNumber, Socket newSocket ){
        slimeSoccer = newSlimeSoccer;
        playerNumber = newPlayerNumber;
        socket = newSocket;
    }

    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            reader = new DataInputStreamAdapter(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                String line = reader.readLine();
                if (line == null) {
                    continue;
                }
                Scanner s = new Scanner(line);
                switch(playerNumber){
                    case 2:
                        slimeSoccer.window.playerTwoJump=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerTwoLeft=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerTwoRight=Boolean.parseBoolean(s.next());
                        break;
                    case 3:
                        slimeSoccer.window.playerThreeJump=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerThreeLeft=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerThreeRight=Boolean.parseBoolean(s.next());
                        break;
                    case 4:
                        slimeSoccer.window.playerFourJump=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerFourLeft=Boolean.parseBoolean(s.next());
                        slimeSoccer.window.playerFourRight=Boolean.parseBoolean(s.next());
                        break;
                    default:
                        break;
                }
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
