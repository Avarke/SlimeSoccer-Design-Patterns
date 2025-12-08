package client.ui.components;

import client.GameData;
import client.SlimeSoccer;
import client.ui.AbstractUIComponent;

import java.awt.Graphics2D;

public class ChatOverlayComponent extends AbstractUIComponent {
    private final SlimeSoccer game;

    public ChatOverlayComponent(SlimeSoccer game) {
        this.game = game;
    }

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;
        game.drawChatOverlay(g, data);
    }
}
