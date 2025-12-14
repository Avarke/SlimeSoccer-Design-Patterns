package server.achievements.concreteAchievements;

import server.achievements.AbstractAchievementLeaf;
import server.achievements.AchievementComponent;
import server.achievements.AchievementContext;
import server.achievements.AchievementEventType;

import java.util.List;

public class MetaAchievement extends AbstractAchievementLeaf {
    private final List<AchievementComponent> requirements;

    public MetaAchievement(
            String id,
            String title,
            String description,
            List<AchievementComponent> requirements
    ) {
        super(id, title, description);
        this.requirements = requirements;
    }

    @Override
    protected void handleEvent(AchievementEventType type, AchievementContext ctx) {
        for (AchievementComponent r : requirements) {
            if (!r.isUnlocked()) return;
        }
        unlock(ctx);
    }
}
