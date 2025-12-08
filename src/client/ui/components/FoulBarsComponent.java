package client.ui.components;

import client.GameData;
import client.ui.AbstractUIComponent;

import java.awt.*;

public class FoulBarsComponent extends AbstractUIComponent {

    private static final double BASE_HEIGHT = 1080.0;
    private static final double FOUL_Y = 0.861 * BASE_HEIGHT;

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;

        int y = (int) FOUL_Y;

        g.setColor(data.getP1Color());
        g.fillRect(0, y, (int) data.getP1FoulBarWidth(), 10);

        g.setColor(data.getP3Color());
        g.fillRect((int) data.getP2FoulBarX(), y, (int) data.getP2FoulBarWidth(), 10);
    }
}
