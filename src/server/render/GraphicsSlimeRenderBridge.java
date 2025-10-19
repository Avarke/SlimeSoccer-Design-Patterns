package server.render;

import java.awt.Color;
import java.awt.Graphics;

public class GraphicsSlimeRenderBridge implements SlimeRenderBridge {
    private final Graphics graphics;

    public GraphicsSlimeRenderBridge(Graphics graphics) {
        this.graphics = graphics;
    }

    @Override
    public void setColor(Color color) {
        graphics.setColor(color);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
    }
}
