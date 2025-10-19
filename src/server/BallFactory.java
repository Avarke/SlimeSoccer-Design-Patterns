package server;

import java.util.EnumMap;
import java.util.Map;

public final class BallFactory {

    private static final Map<BallType, IBallFactory> FACTORIES = new EnumMap<>(BallType.class);
    private static final IBallFactory FALLBACK = new PrototypeBackedFactory(BallType.NORMAL);

    static {
        registerFactory(BallType.NORMAL, new PrototypeBackedFactory(BallType.NORMAL));
        registerFactory(BallType.HEAVY, new PrototypeBackedFactory(BallType.HEAVY));
        registerFactory(BallType.LIGHT, new PrototypeBackedFactory(BallType.LIGHT));
        registerFactory(BallType.FAST, new PrototypeBackedFactory(BallType.FAST));
        registerFactory(BallType.INVISIBLE, new PrototypeBackedFactory(BallType.INVISIBLE));
    }

    private BallFactory() { }

    public static Ball createBall(BallType type, double x, double y) {
        IBallFactory factory = FACTORIES.get(type);
        if (factory == null) {
            factory = FALLBACK;
        }
        return factory.create(x, y);
    }

    public static void registerFactory(BallType type, IBallFactory factory) {
        if (type != null && factory != null) {
            FACTORIES.put(type, factory);
        }
    }

    private static class PrototypeBackedFactory implements IBallFactory {
        private final BallType type;

        PrototypeBackedFactory(BallType type) {
            this.type = type;
        }

        @Override
        public Ball create(double x, double y) {
            return BallPrototypeRegistry.getInstance().create(type, x, y);
        }
    }
}
