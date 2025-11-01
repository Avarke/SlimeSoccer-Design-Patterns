package server;

import java.util.EnumMap;
import java.util.Map;

public class PowerUpRegistry {
    private static final Map<PowerUpType, PowerUp> prototypes = new EnumMap<>(PowerUpType.class);

    static {
        // Base prototypes: position and radius don’t matter here
        prototypes.put(PowerUpType.LOW_GRAVITY, new PowerUp(0, 0, 18, PowerUpType.LOW_GRAVITY, 6000));
        prototypes.put(PowerUpType.HEAVY, new PowerUp(0, 0, 18, PowerUpType.HEAVY, 6000));
        prototypes.put(PowerUpType.REVERSE_GRAVITY, new PowerUp(0, 0, 18, PowerUpType.REVERSE_GRAVITY, 6000));
    }

    public static PowerUp getPrototype(PowerUpType type) {
        return prototypes.get(type);
    }
}
