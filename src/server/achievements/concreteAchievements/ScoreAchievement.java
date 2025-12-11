package server.achievements.concreteAchievements;

import server.achievements.AbstractAchievementLeaf;
import server.achievements.AchievementContext;
import server.achievements.AchievementEventType;

public class ScoreAchievement extends AbstractAchievementLeaf {
    private final int threshold;

    // optional: require another achievement before this can unlock
    private final ScoreAchievement prerequisite;

    public ScoreAchievement(String id,
                            String title,
                            String description,
                            int threshold,
                            ScoreAchievement prerequisite) {
        super(id, title, description);
        this.threshold = threshold;
        this.prerequisite = prerequisite;
    }

    @Override
    protected void handleEvent(AchievementEventType type, AchievementContext ctx) {
        if (type != AchievementEventType.SCORE) return;

        if (prerequisite != null && !prerequisite.isUnlocked()) {
            return; // prerequisite not yet met
        }

        if (ctx.getScoreCount() >= threshold) {
            unlock(ctx);
        }
    }
}
