package server;

import java.awt.Graphics;
import java.util.List;

public interface IPowerUpManager {
    void update(Ball ball, Slime... players);
    void draw(Graphics g);
    List<PowerUp> getVisiblePowerUps();
    void clearAll(Ball ball);
    int getCurrentEffectCode();
}
