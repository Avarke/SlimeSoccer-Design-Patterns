package server;

public class BallCaretaker {
    private BallMemento savedState;

    public void saveCheckpoint(Ball ball) {
        System.out.println("BallCaretaker: Checkpoint saved.");
        this.savedState = ball.save();
    }

    public void restoreCheckpoint(Ball ball) {
        if (savedState != null) {
            System.out.println("BallCaretaker: Restoring checkpoint...");
            ball.restore(savedState);
        } else {
            System.out.println("BallCaretaker: No checkpoint found!");
        }
    }
}
