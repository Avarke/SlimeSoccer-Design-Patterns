package server.chat;

import common.chat.ChatMessage;
import server.model.TeamSide;

public interface ChatMediator {
    void registerParticipant(ChatParticipant participant);
    void removeParticipant(ChatParticipant participant);


    void sendMessage(ChatMessage message);
}
