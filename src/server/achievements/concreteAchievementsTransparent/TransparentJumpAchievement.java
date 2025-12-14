package server.achievements.concreteAchievementsTransparent;

import server.achievements.AchievementContext;
import server.achievements.AchievementEventType;
import server.achievements.TransparentAbstractAchievementLeaf;
import server.achievements.TransparentAchievementComponent;

public class TransparentJumpAchievement extends TransparentAbstractAchievementLeaf {

    private final int threshold;
    private final TransparentAchievementComponent prerequisite; // can be null

    public TransparentJumpAchievement(
            String id,
            String title,
            String description,
            int threshold,
            TransparentAchievementComponent prerequisite
    ) {
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
