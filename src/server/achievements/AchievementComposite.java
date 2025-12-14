package server.achievements;

import java.util.ArrayList;
import java.util.List;

public class AchievementComposite implements AchievementComponent {
    private final String name;  // optional, for debugging
    private final List<AchievementComponent> children = new ArrayList<>();

    public AchievementComposite(String name) {
        this.name = name;
    }

    public void addChild(AchievementComponent child) {
        if (child != null) {
            children.add(child);
        }
    }

    @Override
    public void onEvent(AchievementEventType type, AchievementContext ctx) {
        for (AchievementComponent c : children) {
            c.onEvent(type, ctx);
        }
    }

    @Override
    public boolean isUnlocked() {
        if (children.isEmpty()) return false;
        for (AchievementComponent c : children) {
            if (!c.isUnlocked()) return false;
        }
        return true;
    }
}

