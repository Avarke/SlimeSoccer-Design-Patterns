package client.render;

import client.GameData;

import java.awt.Graphics;

public abstract class BallDecorator implements Drawable {
    protected final Drawable delegate;

    protected BallDecorator(Drawable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void draw(Graphics g, GameData data) {
        delegate.draw(g, data);
    }
}
