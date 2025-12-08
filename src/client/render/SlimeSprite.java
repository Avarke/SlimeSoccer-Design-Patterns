package client.render;

import java.awt.Color;
import java.awt.Graphics2D;

public class SlimeSprite implements SlimeFlyweight{

    private final int radius;
    private final int eyeOffsetX;
    private final int eyeOffsetY;
    private final int eyeRadius;
    private final int pupilRadius;

    public SlimeSprite(int radius) {
        this.radius = radius;
        this.eyeOffsetX = 35;
        this.eyeOffsetY = 35;
        this.eyeRadius = 15;
        this.pupilRadius = 7;
    }

    @Override
    public void draw(Graphics2D g,
                     float posX,
                     float posY,
                     boolean facingRight,
                     Color color,
                     float ballPosX,
                     float ballPosY) {

        // --- Body (intrinsic shape + extrinsic position/color) ---
        g.setColor(color);
        g.fillArc(
                (int) (posX - radius),
                (int) (posY - radius),
                radius * 2,
                radius * 2,
                0,
                180
        );

        // --- Eye position (intrinsic offsets, extrinsic posX/posY/facing) ---
        float eyePosY = posY - eyeOffsetY;
        float eyePosX = facingRight
                ? posX + eyeOffsetX
                : posX - eyeOffsetX;

        g.setColor(Color.WHITE);
        g.fillOval(
                (int) (eyePosX - eyeRadius),
                (int) (eyePosY - eyeRadius),
                eyeRadius * 2,
                eyeRadius * 2
        );

        // --- Pupil pointing towards the ball ---
        float dx = ballPosX - eyePosX;
        float dy = ballPosY - eyePosY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.0001f) dist = 1f; // avoid division by zero

        float pupilOffset = 6f;
        float pupilCenterX = eyePosX + pupilOffset * dx / dist;
        float pupilCenterY = eyePosY + pupilOffset * dy / dist;

        g.setColor(Color.BLACK);
        g.fillOval(
                (int) (pupilCenterX - pupilRadius),
                (int) (pupilCenterY - pupilRadius),
                pupilRadius * 2,
                pupilRadius * 2
        );
    }
}
