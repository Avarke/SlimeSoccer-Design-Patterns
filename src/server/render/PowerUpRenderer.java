package server.render;

import server.PowerUp;

/** Renderer implementor for PowerUp visuals (Bridge implementor). */
public interface PowerUpRenderer {
    void draw(PowerUp p);
}
