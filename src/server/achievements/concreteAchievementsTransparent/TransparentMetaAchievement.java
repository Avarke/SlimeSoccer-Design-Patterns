package server.achievements.concreteAchievementsTransparent;

import server.achievements.AchievementContext;
import server.achievements.AchievementEventType;
import server.achievements.TransparentAbstractAchievementLeaf;
import server.achievements.TransparentAchievementComponent;

import java.util.List;

public class TransparentMetaAchievement extends TransparentAbstractAchievementLeaf {

    private final List<TransparentAchievementComponent> requirements;

    public TransparentMetaAchievement(
            String id,
            String title,
            String description,
            List<TransparentAchievementComponent> requirements
    ) {
        super(id, title, description);
        this.requirements = requirements;
    }

    @Override
    protected void handleEvent(AchievementEventType type, AchievementContext ctx) {
        // You can ignore 'type' because meta can unlock after ANY event,
        // once all requirements are unlocked.
        for (TransparentAchievementComponent r : requirements) {
            if (!r.isUnlocked()) return;
        }
        unlock(ctx);
    }
}
