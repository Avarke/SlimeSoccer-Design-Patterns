package client.render;

import client.GameData;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Adds an obvious visual effect to the ball by drawing a glowing aura and a fading trail
 * based on recent positions. Makes the use of the decorator pattern visible in-game.
 */
public final class TrailGlowBallDecorator extends BallDecorator {
    private static final int MAX_TRAIL_POINTS = 12;
    private static final float MIN_DISTANCE = 3.5f;
    private static final int BASE_RADIUS = 20;

    private final Deque<Point2D.Float> trail = new ArrayDeque<>(MAX_TRAIL_POINTS);
    private float lastX = Float.NaN;
    private float lastY = Float.NaN;
    private boolean enabled = true;
    private int lastEffectCode = -1;

    private Color trailFillColor = new Color(255, 140, 0);
    private Color trailEdgeColor = new Color(255, 220, 120);
    private Color auraOuterColor = new Color(255, 255, 0);
    private Color auraInnerColor = new Color(255, 165, 0);

    public TrailGlowBallDecorator(Drawable delegate) {
        super(delegate);
    }

    @Override
    public void draw(Graphics g, GameData data) {
        if (!enabled) {
            super.draw(g, data);
            return;
        }

        float x = data.getBallPosX();
        float y = data.getBallPosY();
        updateTrail(x, y);

        Graphics2D g2 = g instanceof Graphics2D ? (Graphics2D) g : null;
        if (g2 != null) {
            drawTrail(g2);
            drawAura(g2, x, y);
        } else {
            // Fallback: draw a simple halo even if Graphics2D is not available.
            g.setColor(new Color(255, 215, 0));
            g.fillOval((int) (x - BASE_RADIUS * 1.6f), (int) (y - BASE_RADIUS * 1.6f),
                    (int) (BASE_RADIUS * 3.2f), (int) (BASE_RADIUS * 3.2f));
        }

        super.draw(g, data);
    }

    private void updateTrail(float x, float y) {
        if (Float.isNaN(lastX) || distance(lastX, lastY, x, y) >= MIN_DISTANCE) {
            if (trail.size() == MAX_TRAIL_POINTS) {
                trail.removeFirst();
            }
            trail.addLast(new Point2D.Float(x, y));
            lastX = x;
            lastY = y;
        } else if (!trail.isEmpty()) {
            // Update the newest point to match the latest location for smoother animation.
            Point2D.Float latest = trail.getLast();
            latest.x = x;
            latest.y = y;
        } else {
            trail.addLast(new Point2D.Float(x, y));
        }
    }

    private void drawTrail(Graphics2D g2) {
        var it = trail.iterator();
        int index = 0;
        int size = trail.size();
        AlphaComposite originalComposite = (AlphaComposite) g2.getComposite();

        while (it.hasNext()) {
            Point2D.Float point = it.next();
            float progress = (float) index / Math.max(size - 1, 1);
            float alpha = 0.65f * (1.0f - progress);
            int radius = (int) (BASE_RADIUS * (1.2f - progress * 0.6f));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.setColor(trailFillColor);
            g2.fillOval((int) (point.x - radius), (int) (point.y - radius), radius * 2, radius * 2);
            g2.setColor(trailEdgeColor);
            g2.drawOval((int) (point.x - radius), (int) (point.y - radius), radius * 2, radius * 2);

            index++;
        }

        g2.setComposite(originalComposite);
    }

    private void drawAura(Graphics2D g2, float x, float y) {
        AlphaComposite originalComposite = (AlphaComposite) g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        int outerRadius = (int) (BASE_RADIUS * 2.6f);
        g2.setColor(auraOuterColor);
        g2.fillOval((int) (x - outerRadius), (int) (y - outerRadius), outerRadius * 2, outerRadius * 2);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        int innerRadius = (int) (BASE_RADIUS * 1.8f);
        g2.setColor(auraInnerColor);
        g2.fillOval((int) (x - innerRadius), (int) (y - innerRadius), innerRadius * 2, innerRadius * 2);
        g2.setComposite(originalComposite);
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        this.enabled = enabled;
        if (!enabled) {
            trail.clear();
            lastX = Float.NaN;
            lastY = Float.NaN;
        }
        if (!enabled) {
            lastEffectCode = 0;
        }
    }

    public void applyEffect(int effectCode) {
        if (effectCode <= 0) {
            setEnabled(false);
            return;
        }
        if (!enabled) {
            setEnabled(true);
        }
        if (effectCode == lastEffectCode) {
            return;
        }
        lastEffectCode = effectCode;
        configurePalette(effectCode);
    }

    private void configurePalette(int effectCode) {
        switch (effectCode) {
            case 1: // Low gravity
                trailFillColor = new Color(80, 180, 255);
                trailEdgeColor = new Color(180, 230, 255);
                auraOuterColor = new Color(135, 206, 250);
                auraInnerColor = new Color(70, 130, 180);
                break;
            case 2: // Heavy
                trailFillColor = new Color(120, 120, 120);
                trailEdgeColor = new Color(200, 200, 200);
                auraOuterColor = new Color(180, 180, 180);
                auraInnerColor = new Color(90, 90, 90);
                break;
            case 3: // Reverse / special
                trailFillColor = new Color(255, 120, 220);
                trailEdgeColor = new Color(255, 200, 240);
                auraOuterColor = new Color(255, 182, 193);
                auraInnerColor = new Color(219, 112, 147);
                break;
            default:
                trailFillColor = new Color(255, 140, 0);
                trailEdgeColor = new Color(255, 220, 120);
                auraOuterColor = new Color(255, 255, 0);
                auraInnerColor = new Color(255, 165, 0);
        }
    }
}
