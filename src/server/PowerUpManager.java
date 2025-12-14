package server;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import server.strategy.BallPhysicsStrategies;

public class PowerUpManager {
    private final List<PowerUp> items = new ArrayList<>();
    private final Random rnd = new Random();

    private long nextSpawnAt = System.currentTimeMillis() + 4000;
    private long effectEndAt = 0;
    private PowerUpType currentEffect = PowerUpType.NORMAL;

    public void update(Ball ball, Slime... players) {
        long now = System.currentTimeMillis();

        if (items.isEmpty() && now >= nextSpawnAt) {
            spawnRandom();
            nextSpawnAt = now + 5000 + rnd.nextInt(4000);
        }

        // Use Iterator to safely remove while iterating (Iterator pattern)
        java.util.Iterator<PowerUp> it = items.iterator();
        while (it.hasNext()) {
            PowerUp p = it.next();
            boolean collected = false;
            for (Slime s : players) {
                if (p.collides(s)) {
                    ball.setPhysicsStrategy(p.createStrategy());
                    currentEffect = p.getType();
                    effectEndAt = now + p.getDurationMs();
                    it.remove(); // safe removal
                    collected = true;
                    break;
                }
            }
            if (collected) {
                // continue to next power-up
            }
        }

        if (effectEndAt > 0 && now >= effectEndAt) {
            ball.setPhysicsStrategy(BallPhysicsStrategies.normal());
            currentEffect = PowerUpType.NORMAL;
            effectEndAt = 0;
        }
    }

    public void draw(Graphics g) {
        server.render.PowerUpRenderer renderer = new server.render.SwingPowerUpRenderer(g);
        for (PowerUp p : items) p.draw(renderer);
    }

    public List<PowerUp> getVisiblePowerUps() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns an Iterable that iterates power-ups in reverse order.
     * Useful when external callers want stable indices while removing.
     */
    public Iterable<PowerUp> reversed() {
        return new Iterable<PowerUp>() {
            @Override
            public java.util.Iterator<PowerUp> iterator() {
                return new java.util.Iterator<PowerUp>() {
                    int idx = items.size() - 1;
                    @Override public boolean hasNext() { return idx >= 0; }
                    @Override public PowerUp next() { return items.get(idx--); }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    /** Simple predicate interface to avoid Java 8 dependency. */
    public interface PowerUpPredicate { boolean test(PowerUp p); }

    /** Returns an Iterable view filtered by the given predicate. */
    public Iterable<PowerUp> filtered(final PowerUpPredicate predicate) {
        return new Iterable<PowerUp>() {
            @Override
            public java.util.Iterator<PowerUp> iterator() {
                return new java.util.Iterator<PowerUp>() {
                    private int cursor = 0;
                    private PowerUp next;
                    private void advance() {
                        next = null;
                        while (cursor < items.size()) {
                            PowerUp cand = items.get(cursor++);
                            if (predicate == null || predicate.test(cand)) {
                                next = cand;
                                break;
                            }
                        }
                    }
                    { advance(); }
                    @Override public boolean hasNext() { return next != null; }
                    @Override public PowerUp next() { PowerUp r = next; advance(); return r; }
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                };
            }
        };
    }

    /** Convenience: iterate only power-ups of a specific type. */
    public Iterable<PowerUp> ofType(final PowerUpType type) {
        return filtered(new PowerUpPredicate() {
            @Override public boolean test(PowerUp p) { return p.getType() == type; }
        });
    }

    public void clearAll(Ball ball) {
        items.clear();
        ball.setPhysicsStrategy(BallPhysicsStrategies.normal());
        currentEffect = PowerUpType.NORMAL;
        effectEndAt = 0;
        nextSpawnAt = System.currentTimeMillis() + 4000;
    }

    public int getCurrentEffectCode() {
        switch (currentEffect) {
            case LOW_GRAVITY: return 1;
            case HEAVY: return 2;
            case REVERSE_GRAVITY: return 3;
            case NORMAL:
            default: return 0;
        }
    }

    private void spawnRandom() {
        PowerUpType type = PowerUpType.values()[1 + rnd.nextInt(3)];
        double x = 0.15 * Window.WIDTH + rnd.nextDouble() * (0.7 * Window.WIDTH);
        double y = 0.78 * Window.HEIGHT - 18;

        PowerUp prototype = PowerUpRegistry.getPrototype(type);
        PowerUp clone = prototype.cloneWithPosition(x, y);

        items.add(clone);
    }
}
