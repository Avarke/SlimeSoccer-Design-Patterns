package server.achievements.concreteAchievements;

import server.achievements.AbstractAchievementLeaf;
import server.achievements.AchievementContext;
import server.achievements.AchievementEventType;

public class JumpAchievement extends AbstractAchievementLeaf {
    private final int threshold;
    private final JumpAchievement prerequisite;

    public JumpAchievement(String id,
                           String title,
                           String description,
                           int threshold,
                           JumpAchievement prerequisite) {
        super(id, title, description);
        this.threshold = threshold;
        this.prerequisite = prerequisite;
    }

    @Override
    protected void handleEvent(AchievementEventType type, AchievementContext ctx) {
        if (type != AchievementEventType.JUMP) return;

        if (prerequisite != null && !prerequisite.isUnlocked()) {
            return;
        }

        if (ctx.getJumpCount() >= threshold) {
            unlock(ctx);
        }
    }
}
