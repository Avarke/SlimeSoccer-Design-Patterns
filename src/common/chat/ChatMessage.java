package common.chat;

import server.model.TeamSide;

public class ChatMessage {
    private final String senderNickname;
    private final String senderTeam;
    private final ChatScope scope;
    private final String text;
    private final long timestamp;

    private final String targetNickname;

    public ChatMessage(String senderNickname,
                       String senderTeam,
                       ChatScope scope,
                       String text,
                       long timestamp, String targetNickname) {
        this.senderNickname = senderNickname;
        this.senderTeam = senderTeam;
        this.scope = scope;
        this.text = text;
        this.timestamp = timestamp;
        this.targetNickname = targetNickname;
    }

    public String getSenderNickname() { return senderNickname; }
    public String getSenderTeam() { return senderTeam; }
    public ChatScope getScope() { return scope; }
    public String getText() { return text; }
    public long getTimestamp() { return timestamp; }
    public String getTargetNickname() { return targetNickname; }
}
