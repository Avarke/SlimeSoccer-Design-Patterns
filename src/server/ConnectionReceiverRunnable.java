package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionReceiverRunnable implements Runnable {
	private ServerSocket echoSocket;
	private SlimeSoccer slimeSoccer;

	public ConnectionReceiverRunnable(SlimeSoccer slimeSoccer){
		this.slimeSoccer = slimeSoccer;
	}
	
	public void run() {
		try {
			echoSocket = new ServerSocket(6969);
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
