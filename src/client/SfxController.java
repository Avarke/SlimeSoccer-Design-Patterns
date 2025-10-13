package client;

public class SfxController implements GameObserver {

    private boolean prevGoal = false;
    private boolean prevFoul = false;

    @Override
    public void onGameDataChanged(GameData data) {

        boolean g = data.isGoalScored();
        if (g && !prevGoal) SoundManager.play("goal.wav");
        prevGoal = g;

        boolean f = data.isFoul();
        if (f && !prevFoul) SoundManager.play("foul.wav");
        prevFoul = f;

        if (data.isBallKickedThisFrame()) {
            SoundManager.play("kick.wav");
        }
    }
}
