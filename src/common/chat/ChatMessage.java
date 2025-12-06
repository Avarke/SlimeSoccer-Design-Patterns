package common.chat;

import server.model.TeamSide;

public class ChatMessage {
    private final String senderNickname;
    private final String senderTeam;
    private final ChatScope scope;
    private final String text;
    private final long timestamp;

    public ChatMessage(String senderNickname,
                       String senderTeam,
                       ChatScope scope,
                       String text,
                       long timestamp) {
        this.senderNickname = senderNickname;
        this.senderTeam = senderTeam;
        this.scope = scope;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderNickname() { return senderNickname; }
    public String getSenderTeam() { return senderTeam; }
    public ChatScope getScope() { return scope; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
}
