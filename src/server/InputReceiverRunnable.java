package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class InputReceiverRunnable implements Runnable {
	SlimeSoccer slimeSoccer;
	Socket socket;
	DataInputStream is;
	int playerNumber;
	
	public InputReceiverRunnable( SlimeSoccer newSlimeSoccer, int newPlayerNumber, Socket newSocket ){
		slimeSoccer = newSlimeSoccer;
		playerNumber = newPlayerNumber;
		socket = newSocket;
	}
	
	public void run() {
		try {
			is = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			try {
				Scanner s = new Scanner(is.readLine());
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

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
