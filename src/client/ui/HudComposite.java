package client.ui;

import client.GameData;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class HudComposite extends AbstractUIComponent {
    private final List<UIComponent> children = new ArrayList<>();

    public void addChild(UIComponent c) {
        if (c != null) children.add(c);
    }

    public void removeChild(UIComponent c) {
        children.remove(c);
    }

    @Override
    public void draw(Graphics2D g, GameData data) {
        if (!isVisible()) return;

        for (UIComponent c : children) {
            if (c.isVisible()) {
                c.draw(g, data);
            }
        }
    }

    @Override
    public void update(GameData data) {
        for (UIComponent c : children) {
            c.update(data);
        }
    }
}
