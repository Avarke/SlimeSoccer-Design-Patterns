package server;

import java.awt.Graphics;
import java.awt.Image;

public class Picture {
    float posX, posY;
    int scaleFactor;
    Image image;

    Picture(float X, float Y, int SF, Image I) {
        posX = X;
        posY = Y;
        scaleFactor = SF;
        image = I;
    }

    public void draw(Graphics g) {
        g.drawImage(image, (int)posX, (int)posY, null);
    }
}