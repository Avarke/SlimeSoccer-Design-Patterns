package client.render;

import java.awt.*;

public interface SlimeFlyweight {
    void draw(Graphics2D g,
              float posX,
              float posY,
              boolean facingRight,
              Color color,
              float ballPosX,
              float ballPosY);
}
