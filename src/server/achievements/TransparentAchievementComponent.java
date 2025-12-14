package server.achievements;

import java.util.List;

public interface TransparentAchievementComponent {
    void onEvent(AchievementEventType type, AchievementContext ctx);
    boolean isUnlocked();

    // visible structure ops (same for leaf + composite)
    void add(TransparentAchievementComponent child);
    void remove(TransparentAchievementComponent child);
    List<TransparentAchievementComponent> getChildren();
}

