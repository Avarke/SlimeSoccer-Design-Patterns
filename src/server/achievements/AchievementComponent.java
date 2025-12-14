package server.achievements;

public interface AchievementComponent {
    void onEvent(AchievementEventType type, AchievementContext ctx);
    boolean isUnlocked();
}
