package server;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

        for (int i = 0; i < items.size(); i++) {
            PowerUp p = items.get(i);
            for (Slime s : players) {
                if (p.collides(s)) {
                    ball.setPhysicsStrategy(p.createStrategy());
                    currentEffect = p.getType();
                    effectEndAt = now + p.getDurationMs();
                    items.remove(i);
                    i--;
                    break;
                }
            }
        }

        if (effectEndAt > 0 && now >= effectEndAt) {
            ball.setPhysicsStrategy(BallPhysicsStrategies.normal());
            currentEffect = PowerUpType.NORMAL;
            effectEndAt = 0;
        }
    }

    public void draw(Graphics g) {
        for (PowerUp p : items) p.draw(g);
    }

    public List<PowerUp> getVisiblePowerUps() {
        return Collections.unmodifiableList(items);
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
        PowerUpType type = PowerUpType.values()[1 + rnd.nextInt(3)]; // pick from LOW/HEAVY/REVERSE
        int r = 18;
        double x = 0.15 * Window.WIDTH + rnd.nextDouble() * (0.7 * Window.WIDTH);
        double y = 0.78 * Window.HEIGHT - r;
        long durMs = 6000;
        items.add(new PowerUp(x, y, r, type, durMs));
    }
}
