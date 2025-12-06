package server;

import common.chat.ChatMessage;
import server.chat.ChatParticipant;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ClientData implements ChatParticipant {
    private PrintStream os;
    private DataInputStream is;

    private int slot;        // player slot (2,3,4...)
    private String team;     // "LEFT" or "RIGHT"
    private String nickname; // from JOIN / connection setup

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

    public void setSlot(int slot)      { this.slot = slot; }
    public int  getSlot()              { return slot; }

    public void setTeam(String team)   { this.team = team; }
    public String getTeamRaw()         { return team; }

    public void setNickname(String nickname) { this.nickname = nickname; }

    // --- ChatParticipant implementation ---

    @Override
    public String getNickname() {
        return nickname != null ? nickname : "";
    }

    @Override
    public String getTeam() {
        return team != null ? team : "";
    }

    @Override
    public void deliver(ChatMessage message) {
        if (os == null || message == null) return;

        // For now: simple text protocol to simplify client implementation
        // Format: CHAT|scope|team|sender|text
        StringBuilder b = new StringBuilder();
        b.append("CHAT|")
                .append(message.getScope().name()).append('|')
                .append(message.getSenderTeam()).append('|')
                .append(escape(message.getSenderNickname())).append('|')
                .append(escape(message.getText()));

        os.println(b.toString());
    }

    private String escape(String s) {
        if (s == null) return "";
        // crude escaping; good enough for prototype
        return s.replace("|", "\\|");
    }
}