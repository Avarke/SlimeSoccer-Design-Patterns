package client.render;

import client.GameData;

import java.awt.Color;
import java.awt.Graphics;

public class EffectColorBallDecorator extends BallDecorator {

    public EffectColorBallDecorator(Drawable delegate) {
        super(delegate);
    }

    @Override
    public void draw(Graphics g, GameData data) {
        Color original = g.getColor();
        g.setColor(resolveColor(data.getBallEffectCode()));
        super.draw(g, data);
        g.setColor(original);
    }

    private Color resolveColor(int effectCode) {
        switch (effectCode) {
            case 1: return new Color(135, 206, 250);
            case 2: return new Color(80, 80, 80);
            case 3: return new Color(255, 105, 180);
            default: return Color.YELLOW;
        }
    }
}
