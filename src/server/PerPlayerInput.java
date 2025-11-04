package server;

import java.util.HashMap;
import java.util.Map;

public class PerPlayerInput implements PlayerInput {
    private final Map<Integer, PlayerInput> per = new HashMap<>();
    private final PlayerInput fallback;

    public PerPlayerInput(PlayerInput fallback) {
        this.fallback = fallback;
    }

    public void set(int player, PlayerInput adapter) {
        per.put(player, adapter);
    }

    private PlayerInput src(int player) {
        return per.getOrDefault(player, fallback);
    }

    @Override public boolean left(int player)  { return src(player).left(player); }
    @Override public boolean right(int player) { return src(player).right(player); }
    @Override public boolean jump(int player)  { return src(player).jump(player); }
    @Override public boolean reset()           { return fallback.reset(); }
}
