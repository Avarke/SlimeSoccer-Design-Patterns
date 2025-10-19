package server.render;

import java.awt.Color;

public interface SlimeRenderBridge {
    void setColor(Color color);
    void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle);
    void fillOval(int x, int y, int width, int height);
}
