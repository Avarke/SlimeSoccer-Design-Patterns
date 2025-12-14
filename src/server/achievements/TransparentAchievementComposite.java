package server.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransparentAchievementComposite implements TransparentAchievementComponent {

    private final String name;
    private final List<TransparentAchievementComponent> children = new ArrayList<>();

    public TransparentAchievementComposite(String name) {
        this.name = name;
    }

    @Override
    public void add(TransparentAchievementComponent child) {
        if (child != null) children.add(child);
    }

    @Override
    public void remove(TransparentAchievementComponent child) {
        children.remove(child);
    }

    @Override
    public List<TransparentAchievementComponent> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public void onEvent(AchievementEventType type, AchievementContext ctx) {
        for (TransparentAchievementComponent c : children) {
            c.onEvent(type, ctx);
        }
    }

    @Override
    public boolean isUnlocked() {
        if (children.isEmpty()) return false;
        for (TransparentAchievementComponent c : children) {
            if (!c.isUnlocked()) return false;
        }
        return true;
    }
}
