package client.ui.components;

import client.GameData;
import client.ui.AbstractUIComponent;

import java.awt.*;

public class MatchPhaseComponent extends AbstractUIComponent {
    private final double baseWidth;
    private final double baseHeight;
    private final Font font;

    public MatchPhaseComponent(double baseWidth, double baseHeight, Font font) {
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
        this.font = font;
    }

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;

        String matchPhase = data.getMatchPhase();
        if (matchPhase == null || matchPhase.trim().isEmpty()) return;

        g.setFont(font);

        int textWidth = g.getFontMetrics().stringWidth(matchPhase);
        int textX = (int) ((baseWidth - textWidth) / 2);
        int textY = (int) (baseHeight / 2 - 100);

        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(matchPhase, textX + 3, textY + 3);

        g.setColor(new Color(255, 255, 255, 220));
        g.drawString(matchPhase, textX, textY);
    }
}
