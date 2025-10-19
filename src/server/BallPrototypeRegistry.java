package server;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

/**
 * Registry holding reusable ball prototypes.
 */
public final class BallPrototypeRegistry {
    private static final BallPrototypeRegistry INSTANCE = new BallPrototypeRegistry();
    private final Map<BallType, Ball> prototypes = new EnumMap<>(BallType.class);

    private BallPrototypeRegistry() {
        registerPrototype(BallType.NORMAL, createBaseBall(10, Color.WHITE, 0, 0));
        registerPrototype(BallType.HEAVY, createBaseBall(12, Color.DARK_GRAY, 0, 0));
        registerPrototype(BallType.LIGHT, createBaseBall(8, Color.LIGHT_GRAY, 0, 0));
        registerPrototype(BallType.FAST, createBaseBall(10, Color.WHITE, 5, -5));
        registerPrototype(BallType.INVISIBLE, createBaseBall(10, new Color(0, 0, 0, 0), 0, 0));
    }

    private Ball createBaseBall(double radius, Color color, double velX, double velY) {
        Ball prototype = new Ball(0, 0, radius, color);
        prototype.setVelX(velX);
        prototype.setVelY(velY);
        return prototype;
    }

    public static BallPrototypeRegistry getInstance() {
        return INSTANCE;
    }

    public void registerPrototype(BallType type, Ball prototype) {
        if (type != null && prototype != null) {
            prototypes.put(type, prototype);
        }
    }

    public Ball create(BallType type, double x, double y) {
        Ball prototype = prototypes.get(type);
        if (prototype == null) {
            prototype = prototypes.get(BallType.NORMAL);
        }
        return prototype.copyWithPosition(x, y);
    }
}
