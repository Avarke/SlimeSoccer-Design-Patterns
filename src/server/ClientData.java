package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientData {
    private PrintStream os;
    private DataInputStream is;

    public ClientData(Socket socket) {
        try {
            os = new PrintStream(socket.getOutputStream());
            is = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PrintStream getOutputStream() { return os; }
    public DataInputStream getInputStream() { return is; }
}