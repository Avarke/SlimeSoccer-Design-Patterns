package client.render;

import client.GameData;

import java.awt.Graphics;

public interface Drawable {
    void draw(Graphics g, GameData data);
}
