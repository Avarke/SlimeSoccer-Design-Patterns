package client.render;

import client.GameData;

import java.awt.Color;
import java.awt.Graphics;

public class SafeZoneBallDecorator extends BallDecorator {
    private final int floorY;

    public SafeZoneBallDecorator(Drawable delegate, int floorY) {
        super(delegate);
        this.floorY = floorY;
    }

    @Override
    public void draw(Graphics g, GameData data) {
        Color original = g.getColor();
        int radius = Math.max(0, Math.min(200, (int) ((floorY - data.getBallPosY()) / 50)));
        if (radius > 0) {
            g.setColor(Color.GRAY);
            g.fillOval((int) data.getBallPosX() - radius, 50, radius * 2, radius * 2);
        }
        g.setColor(original);
        super.draw(g, data);
    }


}