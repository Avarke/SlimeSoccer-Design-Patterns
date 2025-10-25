package server.render;

import java.awt.Graphics;
import server.PowerUp;

/** Swing-based PowerUpRenderer that draws power-ups using java.awt.Graphics. */
public class SwingPowerUpRenderer implements PowerUpRenderer {
    private final Graphics g;

    public SwingPowerUpRenderer(Graphics g) {
        this.g = g;
    }

    @Override
    public void draw(PowerUp p) {
        g.setColor(p.getColor());
        int x = (int) (p.getX() - p.getRadius());
        int y = (int) (p.getY() - p.getRadius());
        int d = p.getRadius() * 2;
        g.fillOval(x, y, d, d);
    }
}
