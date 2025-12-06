package server.chat;

import common.chat.ChatMessage;

public interface ChatParticipant {
    String getNickname();
    String getTeam();  // "LEFT" / "RIGHT" (string is enough)
    void deliver(ChatMessage message);
}
