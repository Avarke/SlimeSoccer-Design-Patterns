package server.achievements;

import java.util.List;

public abstract class TransparentAbstractAchievementLeaf implements TransparentAchievementComponent {

    private final String id;
    private final String title;
    private final String description;
    private boolean unlocked = false;

    protected TransparentAbstractAchievementLeaf(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    @Override
    public boolean isUnlocked() {
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

    // ---- transparent child ops (unsafe) ----
    @Override
    public void add(TransparentAchievementComponent child) {
        throw new UnsupportedOperationException("Leaf cannot have children");
    }

    @Override
    public void remove(TransparentAchievementComponent child) {
        throw new UnsupportedOperationException("Leaf cannot have children");
    }

    @Override
    public List<TransparentAchievementComponent> getChildren() {
        return List.of();
    }
}

