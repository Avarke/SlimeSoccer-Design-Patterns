package server.factory;

import java.awt.Color;

import server.PowerUpManager;
import server.Text;

public final class DefaultGameFactory extends AbstractGameFactory {

    public static final DefaultGameFactory INSTANCE = new DefaultGameFactory();

    private static final String DEFAULT_FONT = "Franklin Gothic Medium Italic";

    private DefaultGameFactory() { }

    @Override
    public Text createText(String content, double x, double y, int fontSize, Color color, String fontName) {
        String resolvedFont = (fontName == null || fontName.isEmpty()) ? DEFAULT_FONT : fontName;
        return super.createText(content, x, y, fontSize, color, resolvedFont);
    }

    @Override
    public PowerUpManager createPowerUpManager() {
        PowerUpManager manager = super.createPowerUpManager();
        return manager;
    }
}
