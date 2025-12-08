package client.ui.components;

import client.GameData;
import client.ui.AbstractUIComponent;

import java.awt.*;

public class ScoreBoardComponent extends AbstractUIComponent {
    private final Font font;

    public ScoreBoardComponent(Font font) {
        this.font = font;
    }

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;

        g.setFont(font);
        g.setColor(Color.WHITE);

        g.drawString(Integer.toString(data.getPlayer1Score()), 50, 100);
        g.drawString(Integer.toString(data.getPlayer2Score()), 1700, 100);
    }
}
