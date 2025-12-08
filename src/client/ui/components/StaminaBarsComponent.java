package client.ui.components;

import client.GameData;
import client.ui.AbstractUIComponent;

import java.awt.*;

public class StaminaBarsComponent extends AbstractUIComponent {

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;

        for (int i = 1; i <= 4; i++) {
            drawForPlayer(g, data, i);
        }
    }

    private void drawForPlayer(Graphics2D g, GameData gameData, int playerIndex) {
        float posX = 0, posY = 0, stamina = 100f;
        String name = "";

        switch (playerIndex) {
            case 1:
                posX = gameData.getP1PosX();
                posY = gameData.getP1PosY();
                stamina = gameData.getP1Stamina();
                name = gameData.getP1Name();
                break;
            case 2:
                posX = gameData.getP2PosX();
                posY = gameData.getP2PosY();
                stamina = gameData.getP2Stamina();
                name = gameData.getP2Name();
                break;
            case 3:
                posX = gameData.getP3PosX();
                posY = gameData.getP3PosY();
                stamina = gameData.getP3Stamina();
                name = gameData.getP3Name();
                break;
            case 4:
                posX = gameData.getP4PosX();
                posY = gameData.getP4PosY();
                stamina = gameData.getP4Stamina();
                name = gameData.getP4Name();
                break;
        }

        int barWidth = 100;
        int barHeight = 10;
        int barX = (int) (posX - barWidth / 2);
        int barY = (int) (posY - 120);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        int filledWidth = (int) (barWidth * (stamina / 100f));

        Color staminaColor;
        if (stamina > 80)      staminaColor = new Color(0, 255, 0);
        else if (stamina > 50) staminaColor = new Color(255, 255, 0);
        else if (stamina > 20) staminaColor = new Color(255, 165, 0);
        else                   staminaColor = new Color(255, 0, 0);

        g.setColor(staminaColor);
        g.fillRect(barX, barY, filledWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        if (name != null && !name.isEmpty()) {
            int textWidth = g.getFontMetrics().stringWidth(name);
            int textX = barX + (barWidth - textWidth) / 2;
            int textY = barY - 5;
            g.drawString(name, textX, textY);
        }
    }
}
