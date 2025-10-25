package client;

import client.audio.SoundManager;
import client.audio.SoundManager.Sfx;

public final class ClientAudioObserver implements GameObserver {
    private boolean prevGoal, prevFoul;
    private long nextKickAtMs;
    private static final int SLIME_R = 75;   // matches drawSlime
    private static final int BALL_R  = 20;   // matches BasicBallDrawable(20)
    private static final int KICK_COOLDOWN_MS = 120;

    @Override
    public void onGameDataChanged(GameData d) {
        // goal / foul: play on rising edge
        if (!prevGoal && d.isGoalScored()) SoundManager.play(Sfx.GOAL);
        if (!prevFoul && d.isFoul())       SoundManager.play(Sfx.FOUL);
        prevGoal = d.isGoalScored();
        prevFoul = d.isFoul();

        // kick: cheap proximity check with cooldown
        long now = System.currentTimeMillis();
        if (now >= nextKickAtMs) {
            float bx = d.getBallPosX(), by = d.getBallPosY();
            if (collides(bx, by, d.getP1PosX(), d.getP1PosY())
                    || collides(bx, by, d.getP2PosX(), d.getP2PosY())
                    || collides(bx, by, d.getP3PosX(), d.getP3PosY())
                    || collides(bx, by, d.getP4PosX(), d.getP4PosY())) {
                SoundManager.play(Sfx.KICK);
                nextKickAtMs = now + KICK_COOLDOWN_MS;
            }
        }
    }

    private boolean collides(float bx, float by, float px, float py) {
        float dx = bx - px, dy = by - py;
        float r = SLIME_R + BALL_R;
        return dx*dx + dy*dy <= r*r;
    }
}
