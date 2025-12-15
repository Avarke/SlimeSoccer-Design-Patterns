package server;

import java.awt.Graphics;
import java.util.Collections;
import java.util.List;

public class PowerUpManagerProxy implements IPowerUpManager {

    private PowerUpManager realSubject;
    private boolean securityCheckPassed = true;
    private boolean logPerformance = false;

    public void setSecurityCheckPassed(boolean passed) {
        this.securityCheckPassed = passed;
    }

    public void setLogPerformance(boolean log) {
        this.logPerformance = log;
    }

    private PowerUpManager getRealSubject() {
        if (realSubject == null) {
            System.out.println("[Proxy] Initializing PowerUpManager (Delayed Creation)...");
            realSubject = new PowerUpManager();
        }
        return realSubject;
    }

    @Override
    public void update(Ball ball, Slime... players) {
        if (!securityCheckPassed) {
            System.out.println("[Proxy] Security check FAILED. Access denied to update.");
            return;
        }
        if (logPerformance) {
            long startTime = System.nanoTime();
            long currentMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            getRealSubject().update(ball, players);

            long endTime = System.nanoTime();

            System.out.printf("[Proxy] update() took %d ns. Current Memory Usage: %d bytes%n", (endTime - startTime),
                    currentMemory);
        } else {
            getRealSubject().update(ball, players);
        }
    }

    @Override
    public void draw(Graphics g) {
        getRealSubject().draw(g);
    }

    @Override
    public List<PowerUp> getVisiblePowerUps() {
        if (realSubject == null) {
            return Collections.emptyList();
        }
        return getRealSubject().getVisiblePowerUps();
    }

    @Override
    public void clearAll(Ball ball) {
        if (securityCheckPassed) {
            System.out.println("[Proxy] Security check passed. Clearing all powerups.");
            getRealSubject().clearAll(ball);
        } else {
            System.out.println("[Proxy] Security check FAILED. Access denied to clearAll.");
        }
    }

    @Override
    public int getCurrentEffectCode() {
        if (realSubject == null)
            return 0;
        return getRealSubject().getCurrentEffectCode();
    }
}
