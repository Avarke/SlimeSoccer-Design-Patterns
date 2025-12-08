package client.ui;

import client.GameData;

import java.awt.*;

public interface UIComponent {
    void draw(Graphics2D g, GameData data);
    void update(GameData data);

    void setVisible(boolean visible);
    boolean isVisible();
}
