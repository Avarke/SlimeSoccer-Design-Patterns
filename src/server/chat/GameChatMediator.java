package server.chat;

import common.chat.ChatInterpreter;
import common.chat.ChatMessage;
import common.chat.ChatScope;
import server.model.TeamSide;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameChatMediator implements ChatMediator {

    private final List<ChatParticipant> participants = new CopyOnWriteArrayList<>();
    private final ChatInterpreter interpreter;

    public GameChatMediator(ChatInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Override
    public void registerParticipant(ChatParticipant participant) {
        if (participant != null) {
            participants.add(participant);
        }
    }

    @Override
    public void removeParticipant(ChatParticipant participant) {
        participants.remove(participant);
    }

    @Override
    public void sendMessage(ChatMessage message) {
        if (message == null) return;

        // 1) Processor (Interpreter) handles censorship, emoji, etc.
        ChatMessage processed = (interpreter != null)
                ? interpreter.process(message)
                : message;

        // 2) Route based on scope
        switch (processed.getScope()) {
            case GLOBAL:
                sendToAll(processed);
                break;
            case TEAM:
                sendToTeam(processed.getSenderTeam(), processed);
                break;
            case PRIVATE:
                sendToPrivate(processed.getTargetNickname(), processed);
                break;
        }
    }

    private void sendToAll(ChatMessage msg) {
        for (ChatParticipant p : participants) {
            p.deliver(msg);
        }
    }

    private void sendToTeam(String team, ChatMessage msg) {
        if (team == null) return;
        for (ChatParticipant p : participants) {
            if (team.equalsIgnoreCase(p.getTeam())) {
                p.deliver(msg);
            }
        }
    }

    private void sendToPrivate(String targetNickname, ChatMessage msg) {
        if (targetNickname == null || targetNickname.isEmpty()) {
            return;
        }
        for (ChatParticipant p : participants) {
            if (targetNickname.equalsIgnoreCase(p.getNickname())) {
                p.deliver(msg);
                break; // only one recipient
            }
        }
    }
}
