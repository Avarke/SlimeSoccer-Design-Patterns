package server.achievements;

import server.chat.ChatMediator;
import server.chat.GameChatMediator;
import common.chat.ChatMessage;
import common.chat.ChatScope;
import server.ClientData;

public class AchievementContext {
    private final ClientData player;
    private final ChatMediator mediator;

    private int scoreCount = 0;
    private int jumpCount = 0;

    public AchievementContext(ClientData player, ChatMediator mediator) {
        this.player = player;
        this.mediator = mediator;
    }

    public ClientData getPlayer() {
        return player;
    }

    // --- Counters ---

    public void incrementScore() {
        scoreCount++;
    }

    public void incrementJump() {
        jumpCount++;
    }

    public int getScoreCount() {
        return scoreCount;
    }

    public int getJumpCount() {
        return jumpCount;
    }


    public void notifyAchievementUnlocked(String title, String description) {
        if (mediator == null) return;

        ChatMessage msg = new ChatMessage(
                "Server",                                  // senderNickname
                player.getTeam(),                          // senderTeam (e.g. LEFT/RIGHT or "")
                ChatScope.PRIVATE,                         // PRIVATE channel
                "[ACHIEVEMENT] " + title + ": " + description,
                System.currentTimeMillis(),
                player.getNickname()                       // targetNickname
        );

        mediator.sendMessage(msg);
    }
}
