package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class InputReceiverRunnable implements Runnable {
    SlimeSoccer slimeSoccer;
    Socket socket;
    BufferedReader reader;
    int playerNumber;

    public InputReceiverRunnable(SlimeSoccer s, int playerNum, Socket sock) {
        this.slimeSoccer = s;
        this.playerNumber = playerNum;
        this.socket = sock;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            return;
        }
        while (true) {
            String line;
            try {
                line = reader.readLine();
                if (line == null) break;
            } catch (IOException e) {
                break;
            }
            boolean left = false;
            boolean right = false;
            boolean jump = false;
            boolean reset = false;
            Scanner s = new Scanner(line);
            if (s.hasNext()) jump = Boolean.parseBoolean(s.next());
            if (s.hasNext()) left = Boolean.parseBoolean(s.next());
            if (s.hasNext()) right = Boolean.parseBoolean(s.next());
            if (s.hasNext()) reset = Boolean.parseBoolean(s.next());
            s.close();
            slimeSoccer.getInputState().set(playerNumber, left, right, jump);
            slimeSoccer.getInputState().reset = reset;
        }
        try {
            reader.close();
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
