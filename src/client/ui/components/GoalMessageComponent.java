package client.ui.components;

import client.GameData;
import client.ui.AbstractUIComponent;

import java.awt.*;

public class GoalMessageComponent extends AbstractUIComponent {

    private final Font font;

    public GoalMessageComponent(Font font) {
        this.font = font;
    }

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!data.isGoalScored() && !data.isFoul()) {
            return;
        }

        Font old = g.getFont();
        g.setFont(font);

        String text = data.isGoalScored() ? "GOAL!" : "FOUL!";
        g.setColor(Color.WHITE);
        g.drawString(text, 550, 300);

        g.setFont(old);
    }
}
