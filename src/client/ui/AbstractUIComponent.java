package client.ui;

import client.GameData;
import java.awt.Graphics2D;


public abstract class AbstractUIComponent implements UIComponent {
    private boolean visible = true;

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    // default no-op update
    @Override
    public void update(GameData data) { }
}