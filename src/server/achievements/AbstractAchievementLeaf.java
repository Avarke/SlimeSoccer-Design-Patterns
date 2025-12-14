package server.achievements;

public abstract class AbstractAchievementLeaf implements AchievementComponent {
    private final String id;
    private final String title;
    private final String description;
    private boolean unlocked = false;

    protected AbstractAchievementLeaf(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    protected boolean isUnlocked() {
        return unlocked;
    }

    /** Exposes unlock status for iterator/reporting use-cases. */
    public boolean unlocked() {
        return unlocked;
    }

    protected void unlock(AchievementContext ctx) {
        if (unlocked) return;
        unlocked = true;
        ctx.notifyAchievementUnlocked(title, description);
    }

    @Override
    public final void onEvent(AchievementEventType type, AchievementContext ctx) {
        if (unlocked) return;
        handleEvent(type, ctx);
    }

    protected abstract void handleEvent(AchievementEventType type, AchievementContext ctx);
}
