package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiverRunnable implements Runnable {
    private ServerSocket echoSocket;
    private final SlimeSoccer slimeSoccer;
    private final int port;

    public ConnectionReceiverRunnable(SlimeSoccer slimeSoccer, int port){
        this.slimeSoccer = slimeSoccer;
        this.port = port;
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
                slimeSoccer.clients.add(new ClientData(socket));
                new Thread(new InputReceiverRunnable(slimeSoccer, slimeSoccer.clients.size()+1, socket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
