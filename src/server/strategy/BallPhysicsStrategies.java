package server.strategy;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central registry for sharing immutable physics strategies.
 */
import server.PowerUpType;

public final class BallPhysicsStrategies {
    private static final Map<PowerUpType, BallPhysicsStrategy> STRATEGIES = new EnumMap<>(PowerUpType.class);
    private static final BallPhysicsStrategy DEFAULT = new NormalPhysics();

    static {
        STRATEGIES.put(PowerUpType.NORMAL, DEFAULT);
        STRATEGIES.put(PowerUpType.LOW_GRAVITY, new LowGravityPhysics());
        STRATEGIES.put(PowerUpType.HEAVY, new HeavyPhysics());
        STRATEGIES.put(PowerUpType.REVERSE_GRAVITY, new ReverseGravityPhysics());
    }

    private BallPhysicsStrategies() { }

    public static BallPhysicsStrategy forType(PowerUpType type) {
        BallPhysicsStrategy strategy = STRATEGIES.get(type);
        return strategy != null ? strategy : DEFAULT;
    }

    public static BallPhysicsStrategy normal() {
        return DEFAULT;
    }
}
