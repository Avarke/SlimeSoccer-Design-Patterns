package client.render;

import client.GameData;

import java.awt.Graphics;

public class BasicBallDrawable implements Drawable {
    private final int radius;

    public BasicBallDrawable(int radius) {
        this.radius = radius;
    }

    @Override
    public void draw(Graphics g, GameData data) {
        int diameter = radius * 2;
        g.fillOval((int) (data.getBallPosX() - radius), (int) (data.getBallPosY() - radius), diameter, diameter);
    }
}
